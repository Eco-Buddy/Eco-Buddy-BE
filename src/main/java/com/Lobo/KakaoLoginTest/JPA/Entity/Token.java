package com.Lobo.KakaoLoginTest.JPA.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Table(name = "token")
public class Token {

    @Id
    @Column(name = "token_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "device_id", nullable = false, unique = true)
    private Device device;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "acc_exp_period")
    private int accExpPeriod;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "acc_issue_date")
    private LocalDateTime accIssueDate;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "ref_exp_period")
    private int refExpPeriod;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ref_issue_date")
    private LocalDateTime refIssueDate;

    @Builder
    public Token(Long id, Device deviceId,
                 String accessToken, int accExpPeriod, LocalDateTime accIssueDate,
                 String refreshToken, int refExpPeriod, LocalDateTime refIssueDate) {
        this.id = id;
        this.device = deviceId;
        this.accessToken = accessToken;
        this.accExpPeriod = accExpPeriod;
        this.accIssueDate = accIssueDate;
        this.refreshToken = refreshToken;
        this.refExpPeriod = refExpPeriod;
        this.refIssueDate = refIssueDate;
    }

    public boolean isExpired() {
        LocalDateTime expiryTime = accIssueDate.plusSeconds(accExpPeriod);

        return LocalDateTime.now().isAfter(expiryTime);
    }
}
