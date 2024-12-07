package com.Lobo.KakaoLoginTest.JPA.Entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Builder
    public Device(String deviceId, User user){
        this.deviceId = deviceId;
        this.user = user;
    }
}
