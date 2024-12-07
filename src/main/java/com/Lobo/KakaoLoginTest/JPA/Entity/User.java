package com.Lobo.KakaoLoginTest.JPA.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Table(name = "user")
public class User {

    @Id
    @Column(nullable = false)
    private String id;

    @JsonIgnore
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @JsonIgnore
    @Column(name = "join_date", nullable = false)
    private LocalDate joinDate;

    @JsonIgnore
    @Column(name = "platform", nullable = false)
    private String platform;

    @JsonIgnore
    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Builder
    public User(String id, String name, LocalDate joinDate, String platform, String imageUrl) {
        this.id = id;
        this.name = name;
        this.joinDate = joinDate;
        this.platform = platform;
        this.imageUrl = imageUrl;
    }
}
