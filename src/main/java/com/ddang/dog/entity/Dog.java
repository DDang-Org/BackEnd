package com.ddang.dog.entity;

import com.ddang.family.entity.Family;
import com.ddang.global.entity.BaseEntity;
import com.ddang.global.entity.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Dog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dogId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String breed;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false)
    @Digits(integer = 3, fraction = 2)
    private BigDecimal weight;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    private String profileImg;

    private Integer walkCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IsNeutered isNeutered;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id", nullable = true)
    private Family family;

    @Column(length = 30, nullable = false)
    private String comment;


    @Builder
    private Dog(String name, String breed, LocalDate birthDate, Gender gender, BigDecimal weight, IsNeutered isNeutered, String profileImg, Family family, String comment) {
        this.name = name;
        this.breed = breed;
        this.birthDate = birthDate;
        this.gender = gender;
        this.weight = weight;
        this.profileImg = profileImg;
        this.isNeutered = isNeutered;
        this.family = family;
        this.comment = comment;
        this.walkCount = 0;
    }
}
