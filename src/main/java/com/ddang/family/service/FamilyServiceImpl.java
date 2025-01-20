package com.ddang.family.service;

import com.ddang.family.entity.Family;
import com.ddang.family.repository.FamilyRepository;
import com.ddang.family.service.response.InviteCodeResponse;
import com.ddang.global.exception.BadRequestException;
import com.ddang.global.exception.ErrorCode;
import com.ddang.member.entity.Member;
import com.ddang.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FamilyServiceImpl implements FamilyService{

    private static final String REDIS_INVITE_KEY_PREFIX = "invite:";

    private final RedisTemplate<String, String> redisTemplate;
    private final MemberRepository memberRepository;
    private final FamilyRepository familyRepository;


    @Override
    public InviteCodeResponse createInviteCode(Member member) {
        Member currentMember = validateMemberInFamily(member);

        Family family = currentMember.getFamily();
        String redisSearchKey = REDIS_INVITE_KEY_PREFIX;

        List<String> keys = Objects.requireNonNull(redisTemplate.keys(redisSearchKey + "*")).stream().toList();
        for (String key : keys) {
            String familyIdStr = redisTemplate.opsForValue().get(key);
            if (familyIdStr != null && familyIdStr.equals(String.valueOf(family.getFamilyId()))) {
                Long ttl = redisTemplate.getExpire(key);
                if (ttl != null && ttl > 0) {
                    String existingInviteCode = key.replace(redisSearchKey, "");
                    return new InviteCodeResponse(family.getFamilyId(), existingInviteCode, ttl);
                }
            }
        }

        String newInviteCode = generateInviteCode(family.getFamilyId());
        redisTemplate.opsForValue().set(REDIS_INVITE_KEY_PREFIX + newInviteCode, String.valueOf(family.getFamilyId()), Duration.ofMinutes(5));

        return new InviteCodeResponse(family.getFamilyId(), newInviteCode, Duration.ofMinutes(5).toSeconds());
    }




    private String generateInviteCode(Long familyId) {
        String code;
        boolean isSet;
        do {
            code = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
            isSet = Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(REDIS_INVITE_KEY_PREFIX + code, String.valueOf(familyId), Duration.ofMinutes(5)));
        } while (!isSet);
        return code;
    }

    private Member validateMemberInFamily(Member member) {
        Member currentMember = findMemberByEmailOrThrowException(member.getEmail());
        if (currentMember.getFamily() == null) {
            throw new BadRequestException(ErrorCode.MEMBER_NOT_IN_FAMILY);
        }
        return currentMember;
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
