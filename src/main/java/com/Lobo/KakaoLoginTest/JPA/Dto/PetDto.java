package com.Lobo.KakaoLoginTest.JPA.Dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PetDto {

    private String petName;
    private int petLevel;
    private int experience;
    private int points;
    private int background;
    private int floor;

    @Builder
    public PetDto(String petName, int petLevel, int experience, int points, int background, int floor) {
        this.petName = petName;
        this.petLevel = petLevel;
        this.experience = experience;
        this.points = points;
        this.background = background;
        this.floor = floor;
    }
}
