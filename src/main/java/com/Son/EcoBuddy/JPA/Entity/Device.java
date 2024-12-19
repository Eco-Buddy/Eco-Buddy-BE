package com.Son.EcoBuddy.JPA.Entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "device")
@Getter
public class Device {

    @Id
    @Column(name = "device_id")
    private String deviceId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(mappedBy = "device", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Token token;

    @OneToMany(mappedBy = "device", cascade = CascadeType.REMOVE, orphanRemoval = true)
    List<DailyDataUsage> dailyDataUsageList = new ArrayList<>();

    @OneToMany(mappedBy = "device", cascade = CascadeType.REMOVE, orphanRemoval = true)
    List<HourlyDataUsage> hourlyDataUsageList = new ArrayList<>();

    @Builder
    public Device(String deviceId, User user){
        this.deviceId = deviceId;
        this.user = user;
    }
}
