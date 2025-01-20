package com.ddang.family.service;

import com.ddang.dog.entity.Dog;
import com.ddang.dog.entity.MemberDog;
import com.ddang.dog.repository.DogRepository;
import com.ddang.dog.repository.MemberDogRepository;
import com.ddang.dog.service.response.DogResponse;
import com.ddang.family.entity.Family;
import com.ddang.family.entity.WalkSchedule;
import com.ddang.family.entity.WeekDay;
import com.ddang.family.repository.DayOfWeekRepository;
import com.ddang.family.repository.FamilyRepository;
import com.ddang.family.repository.WalkScheduleRepository;
import com.ddang.family.service.response.*;
import com.ddang.global.exception.BadRequestException;
import com.ddang.global.exception.ErrorCode;
import com.ddang.member.entity.Member;
import com.ddang.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FamilyServiceImpl implements FamilyService {

    private static final String REDIS_INVITE_KEY_PREFIX = "invite:";

    private final RedisTemplate<String, String> redisTemplate;
    private final MemberRepository memberRepository;
    private final FamilyRepository familyRepository;
    private final MemberDogRepository memberDogRepository;
    private final DogRepository dogRepository;
    private final DayOfWeekRepository dayOfWeekRepository;
    private final WalkScheduleRepository walkScheduleRepository;
//    private final WalkRepository walkRepository;


    @Override
    public InviteCodeResponse createInviteCode(Member member) {
        Member currentMember = validateMemberInFamily(member);
        Family family = currentMember.getFamily();

        String existingInviteCode = findExistingInviteCode(family.getFamilyId());
        if (existingInviteCode != null) {
            long ttl = getInviteCodeTTL(existingInviteCode);
            return InviteCodeResponse.of(family.getFamilyId(), existingInviteCode, ttl);
        }

        String newInviteCode = generateAndStoreInviteCode(family.getFamilyId());
        return InviteCodeResponse.of(family.getFamilyId(), newInviteCode, Duration.ofMinutes(5).toSeconds());
    }

    @Override
    @Transactional
    public FamilyResponse addMemberToFamily(String inviteCode, Member member) {
        Member currentMember = validateMemberNotInFamily(member);
        validateMemberWithoutDog(member);
        Family family = getFamilyByInviteCode(inviteCode);
        addMemberToFamilyAssociations(currentMember, family);
        return FamilyResponse.from(family);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DogResponse> getFamilyDogs(String inviteCode) {
        Family family = getFamilyByInviteCode(inviteCode);
        return dogRepository.findAllByFamilyId(family.getFamilyId())
                .stream()
                .map(DogResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FamilyDogResponse> getMyFamilyDogs(Member member) {
        Member currentMember = validateMemberInFamily(member);
        Family family = currentMember.getFamily();

        List<Dog> dogs = dogRepository.findAllByFamilyId(family.getFamilyId());
        List<Long> dogIds = dogs.stream().map(Dog::getDogId).toList();
        Map<Long, Integer> totalDistances = getTotalDistancesByDogIds(dogIds);

        return dogs.stream()
                .map(dog -> {
                    int totalDistanceMeters = totalDistances.getOrDefault(dog.getDogId(), 0);
                    double totalDistanceKilometers = totalDistanceMeters / 1000.0;
                    int totalCalories = calculateCalorie(dog.getWeight(), totalDistanceMeters);
                    return FamilyDogResponse.of(
                            dog,
                            totalDistanceKilometers,
                            totalCalories
                    );
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FamilyMemberResponse>getMyFamily(Member member){
        Member currentMember = validateMemberInFamily(member);
        Family family = currentMember.getFamily();

        List<Member> familyMembers = memberRepository.findAllByFamily(family);
        Map<Long, WalkScheduleInfo> walkSchedules = getWalkSchedulesWithDaysForMembers(familyMembers);
        return familyMembers.stream()
                .map(m -> {
                    WalkScheduleInfo scheduleInfo = walkSchedules.getOrDefault(m.getMemberId(), null);
                    boolean isRepresent = family.getRepresentativeMemberId().equals(m.getMemberId());
                    return FamilyMemberResponse.of(m, scheduleInfo, isRepresent);
                })
                .toList();

    }


    @Override
    @Transactional
    public void removeMemberFromFamily(Long memberIdToRemove, Member member) {
        Member currentMember = validateFamilyBoss(member);
        Member removeMember = findMemberByIdOrThrowException(memberIdToRemove);

        validateRemoveMember(currentMember, removeMember);

        List<WalkSchedule> schedules = walkScheduleRepository.findByMemberId(removeMember.getMemberId());
        schedules.forEach(dayOfWeekRepository::deleteByWalkSchedule);
        walkScheduleRepository.deleteByMemberId(removeMember.getMemberId());

        memberDogRepository.softDeleteByMember(removeMember);
        removeMember.updateFamily(null);
    }

    @Override
    @Transactional
    public void leaveFamily(Member member) {
        Member currentMember = validateMemberInFamily(member);
        validateNotFamilyBossForLeaving(currentMember);

        List<WalkSchedule> schedules = walkScheduleRepository.findByMemberId(member.getMemberId());
        schedules.forEach(dayOfWeekRepository::deleteByWalkSchedule);
        walkScheduleRepository.deleteByMemberId(member.getMemberId());

        memberDogRepository.softDeleteByMember(member);
        member.updateFamily(null);
    }



    // Helper Method
    private String findExistingInviteCode(Long familyId) {

        return Objects.requireNonNull(redisTemplate.keys(REDIS_INVITE_KEY_PREFIX + "*"))
                .stream()
                .filter(key -> familyId.equals(getFamilyIdFromKey(key)))
                .findFirst()
                .orElse(null);
    }

    private Family getFamilyByInviteCode(String inviteCode) {
        String familyIdStr = redisTemplate.opsForValue().get(REDIS_INVITE_KEY_PREFIX + inviteCode);
        if (familyIdStr == null) {
            throw new BadRequestException(ErrorCode.INVALID_INVITE_CODE);
        }
        Long familyId = Long.valueOf(familyIdStr);
        return findFamilyByIdOrThrowException(familyId);
    }

    private void addMemberToFamilyAssociations(Member member, Family family) {
        member.updateFamily(family);
        List<Dog> dogs = dogRepository.findAllByFamilyId(family.getFamilyId());
        dogs.forEach(dog -> memberDogRepository.save(
                MemberDog.builder()
                        .member(member)
                        .dog(dog)
                        .build()
        ));
    }

    private Map<Long, Integer> getTotalDistancesByDogIds(List<Long> dogIds) {
        // TODO : walkRepository에 추가하고 수정

//        @Query("""
//    SELECT w.dog.dogId, SUM(w.distanceInKilometers)
//    FROM Walk w
//    WHERE w.dog.dogId IN :dogIds
//    GROUP BY w.dog.dogId
//    """)
//        List<Object[]> findTotalDistancesByDogIds(@Param("dogIds") List<Long> dogIds);


//        List<Object[]> results = walkRepository.findTotalDistancesByDogIds(dogIds);
//
//        return results.stream()
//                .collect(Collectors.toMap(
//                        result -> (Long) result[0],
//                        result -> (Double) result[1]
//                ));
        return null;
    }

    public int calculateCalorie(BigDecimal weight, int totalDistance){
        return (int) (0.75 * weight.doubleValue() * totalDistance / 1000);
    }

    private Map<Long, WalkScheduleInfo> getWalkSchedulesWithDaysForMembers(List<Member> members) {
        if (members.isEmpty()) {
            return Map.of();
        }

        List<Long> memberIds = members.stream()
                .map(Member::getMemberId)
                .toList();

        List<Object[]> results = dayOfWeekRepository.findSchedulesWithDaysByMemberIds(memberIds);

        return results.stream()
                .collect(Collectors.toMap(
                        result -> (Long) result[0],
                        result -> {
                            Long walkScheduleId = (Long) result[1];
                            String weekDayStr = (String) result[2];
                            LocalTime walkTime = (LocalTime) result[3];

                            List<WeekDay> weekDays = parseWeekDays(weekDayStr);

                            return new WalkScheduleInfo(walkScheduleId, weekDays, walkTime);
                        }
                ));
    }


    // second Helper Method
    private Long getFamilyIdFromKey(String key) {
        String familyIdStr = redisTemplate.opsForValue().get(key);
        return familyIdStr != null ? Long.valueOf(familyIdStr) : null;
    }

    private long getInviteCodeTTL(String inviteCode) {
        Long ttl = redisTemplate.getExpire(REDIS_INVITE_KEY_PREFIX + inviteCode);
        return (ttl != null && ttl > 0) ? ttl : 0;
    }

    private String generateAndStoreInviteCode(Long familyId) {
        String code;
        boolean isSet;
        do {
            code = generateInviteCode();
            isSet = Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(
                    REDIS_INVITE_KEY_PREFIX + code, String.valueOf(familyId), Duration.ofMinutes(5)));
        } while (!isSet);
        return code;
    }

    private String generateInviteCode() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8)
                .toUpperCase();
    }

    private List<WeekDay> parseWeekDays(String weekDayStr) {
        if (weekDayStr == null || weekDayStr.isEmpty()) {
            return List.of();
        }

        return Arrays.stream(weekDayStr.split(","))
                .map(WeekDay::valueOf)
                .toList();
    }

    // validate method
    private Member validateFamilyBoss(Member member) {
        Member currentMember = validateMemberInFamily(member);
        if (!currentMember.getFamily().getRepresentativeMemberId().equals(currentMember.getMemberId())) {
            throw new BadRequestException(ErrorCode.MEMBER_NOT_FAMILY_BOSS);
        }
        return currentMember;
    }

    private void validateNotFamilyBossForLeaving(Member member) {
        Family family = member.getFamily();
        if (family.getRepresentativeMemberId().equals(member.getMemberId())) {
            throw new BadRequestException(ErrorCode.INVALID_ACTION_FAMILY_BOSS);
        }
    }

    private void validateRemoveMember(Member currentMember, Member removeMember) {
        if (currentMember.getMemberId().equals(removeMember.getMemberId())) {
            throw new BadRequestException(ErrorCode.SELF_REMOVE_NOT_ALLOWED);
        }
        if (removeMember.getFamily() == null) {
            throw new BadRequestException(ErrorCode.MEMBER_NOT_IN_FAMILY);
        }
        if (!currentMember.getFamily().getFamilyId().equals(removeMember.getFamily().getFamilyId())) {
            throw new BadRequestException(ErrorCode.INVALID_FAMILY_MEMBER);
        }
    }

    private Member validateMemberInFamily(Member member) {
        Member currentMember = findMemberByEmailOrThrowException(member.getEmail());
        if (currentMember.getFamily() == null) {
            throw new BadRequestException(ErrorCode.MEMBER_NOT_IN_FAMILY);
        }
        return currentMember;
    }

    private Member validateMemberNotInFamily(Member member) {
        Member currentMember = findMemberByEmailOrThrowException(member.getEmail());
        if (currentMember.getFamily() != null) {
            throw new BadRequestException(ErrorCode.MEMBER_IN_FAMILY);
        }
        return currentMember;
    }

    private void validateMemberWithoutDog(Member member) {
        if (!memberDogRepository.findAllByMember(member).isEmpty()) {
            throw new BadRequestException(ErrorCode.MEMBER_HAVE_DOG);
        }
    }

    private Member findMemberByIdOrThrowException(Long id) {
        return memberRepository.findActiveById(id)
                .orElseThrow(() -> {
                    log.warn(">>>> {} : {} <<<<", id, ErrorCode.MEMBER_NOT_FOUND);
                    return new BadRequestException(ErrorCode.MEMBER_NOT_FOUND);
                });
    }

    private Member findMemberByEmailOrThrowException(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn(">>>> {} : {} <<<<", email, ErrorCode.MEMBER_NOT_FOUND);
                    return new BadRequestException(ErrorCode.MEMBER_NOT_FOUND);
                });
    }

    private Family findFamilyByIdOrThrowException(Long id) {
        return familyRepository.findActiveById(id)
                .orElseThrow(() -> {
                    log.warn(">>>> {} : {} <<<<", id, ErrorCode.FAMILY_NOT_FOUND);
                    return new BadRequestException(ErrorCode.FAMILY_NOT_FOUND);
                });
    }
}
