package com.Son.EcoBuddy.JPA.Dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class DataUsageDto {

    private LocalDateTime usageTime;
    private Double dataUsed;
    private Double wifiUsed;
}
