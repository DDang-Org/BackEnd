package com.ddang.dog.entity;

import com.ddang.IntegrationTestSupport;
import com.ddang.dog.controller.request.UpdateDogRequest;
import com.ddang.family.entity.Family;
import com.ddang.family.repository.FamilyRepository;
import com.ddang.global.entity.Gender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;


class DogTest extends IntegrationTestSupport {

    @Autowired
    FamilyRepository familyRepository;

    @Test
    @DisplayName("dog 정보 수정을 한다.")
    void update() {
        //given
        Family family = Family.create(1L);
        familyRepository.save(family);

        Dog dog = Dog.builder()
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

        UpdateDogRequest request = new UpdateDogRequest("banana", null,
                null, BigDecimal.valueOf(4.5), null, null,
                "how kind of you");

        //when
        dog.update(request.toServiceRequest(), null);

        //then
        assertThat(dog)
                .extracting("name", "weight", "comment")
                .containsExactlyInAnyOrder(
                        "banana", BigDecimal.valueOf(4.5), "how kind of you"
                );

    }
}