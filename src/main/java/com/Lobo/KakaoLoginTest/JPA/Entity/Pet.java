package com.Lobo.KakaoLoginTest.JPA.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Table(name = "pet")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "pet_name", nullable = false, length = 50)
    private String petName;

    @Column(name = "pet_level", nullable = false)
    private int petLevel;

    @Column(name = "experience", nullable = false)
    private int experience;

    @Column(name = "points", nullable = false)
    private int points;

    @Column(name = "background", nullable = false)
    private int background;

    @Column(name = "floor", nullable = false)
    private int floor;

    @Builder
    public Pet(Long id, User user, String petName, int petLevel, int experience, int points, int background, int floor) {
        this.id = id;
        this.user = user;
        this.petName = petName;
        this.petLevel = petLevel;
        this.experience = experience;
        this.points = points;
        this.background = background;
        this.floor = floor;
    }
}
