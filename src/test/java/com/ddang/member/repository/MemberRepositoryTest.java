package com.ddang.member.repository;

import com.ddang.family.entity.Family;
import com.ddang.member.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.ddang.global.entity.Gender.MALE;
import static com.ddang.member.entity.FamilyRole.FATHER;
import static com.ddang.member.entity.IsMatched.TRUE;
import static com.ddang.member.entity.Provider.KAKAO;
import static com.ddang.member.entity.Role.USER;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private Member member;
    private Family family;

    @BeforeEach
    void setUp() {
        family = Family.create();
        testEntityManager.persist(family);

        member = Member.builder()
                .email("test@naver.com")
                .provider(KAKAO)
                .name("홍길동")
                .gender(MALE)
                .birthDate(LocalDate.of(1990, 1, 1))
                .address("서울시 강남구")
                .familyRole(FATHER)
                .profileImg(1)
                .isMatched(TRUE)
                .family(family)
                .role(USER)
                .build();
    }

    @Test
    @DisplayName("이메일로 회원 조회 테스트")
    void findByEmailTest() {
        // given
        memberRepository.save(member);

        // when
        Optional<Member> foundMember = memberRepository.findByEmail("test@naver.com");

        // then
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getEmail()).isEqualTo("test@naver.com");
    }

    @Test
    @DisplayName("회원 ID로 회원 조회 테스트")
    void findByIdTest() {
        // given
        Member savedMember = memberRepository.save(member);

        // when
        Optional<Member> foundMember = memberRepository.findById(savedMember.getMemberId());

        // then
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getMemberId()).isEqualTo(savedMember.getMemberId());
    }

    @Test
    @DisplayName("가족으로 회원 조회 테스트")
    void findAllByFamilyTest() {
        // given
        memberRepository.save(member);

        // when
        List<Member> members = memberRepository.findAllByFamily(family);

        // then
        assertThat(members)
                .isNotNull()
                .isNotEmpty()
                .contains(member);
    }
}
