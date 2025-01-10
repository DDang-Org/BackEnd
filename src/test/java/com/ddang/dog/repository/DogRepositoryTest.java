package com.ddang.dog.repository;

import com.ddang.IntegrationTestSupport;
import com.ddang.dog.entity.Dog;
import com.ddang.dog.entity.IsNeutered;
import com.ddang.family.entity.Family;
import com.ddang.family.repository.FamilyRepository;
import com.ddang.global.entity.Gender;
import com.ddang.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
class DogRepositoryTest extends IntegrationTestSupport {

    @Autowired
    DogRepository dogRepository;

    @Autowired
    FamilyRepository familyRepository;

    @Test
    @DisplayName("Dog 을 Soft Delete 처리를 한다.")
    void softDeleteById() {
        //given
        Family family = Family.create();
        familyRepository.save(family);
        Dog dog = createDog(family);
        dogRepository.save(dog);

        //when
        dogRepository.softDeleteById(dog.getDogId());


        //then
        assertThat(dogRepository.findActiveById(dog.getDogId())).isEmpty();
    }

    @Test
    @DisplayName("Delete 되지 않은 Dog 을 Id 로 조회한다.")
    void findActiveById() {
        //given
        Family family = Family.create();
        familyRepository.save(family);

        Dog dog = createDog(family);
        dogRepository.save(dog);

        //when
        Dog findDog = dogRepository.findActiveById(dog.getDogId()).get();

        //then
        assertThat(findDog)
                .extracting("dogId", "name", "breed", "birthDate", "weight", "gender", "profileImg", "walkCount",
                        "isNeutered", "family", "comment")
                .containsExactlyInAnyOrder(
                        dog.getDogId(), dog.getName(), dog.getBreed(),
                        dog.getBirthDate(), dog.getWeight(), dog.getGender(), dog.getProfileImg(),
                        dog.getIsNeutered(), dog.getWalkCount(), dog.getFamily(), dog.getComment()
                );
    }

    private Dog createDog(Family family){
        return  Dog.builder()
                .name("choco")
                .gender(Gender.MALE)
                .profileImg("url")
                .birthDate(LocalDate.of(2023,5,7))
                .breed("sigol")
                .family(family)
                .weight(BigDecimal.valueOf(3.7))
                .comment("kind")
                .isNeutered(IsNeutered.TRUE)
                .build();
    }
}