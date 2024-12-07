package com.Lobo.KakaoLoginTest.JPA.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "daily_data_usage",
        indexes = {
                @Index(name = "idx_device_id", columnList = "device_id"),
                @Index(name = "idx_usage_time", columnList = "usage_time")
})
public class DailyDataUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Column(name = "usage_time", nullable = false)
    private LocalDateTime usageTime;

    @Column(name = "data_used", nullable = false)
    private Double dataUsed;

    @Column(name = "wifi_used", nullable = false)
    private Double wifiUsed;

    @Builder
    public DailyDataUsage(Long id, Device deviceId, LocalDateTime usageTime, Double dataUsed, Double wifiUsed) {
        this.id = id;
        this.device = deviceId;
        this.usageTime = usageTime;
        this.dataUsed = dataUsed;
        this.wifiUsed = wifiUsed;
    }

}
