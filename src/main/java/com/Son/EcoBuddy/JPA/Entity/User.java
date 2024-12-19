package com.Son.EcoBuddy.JPA.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Item> items = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Device> devices = new ArrayList<>();

    @JsonIgnore
    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Pet pet;

    @Builder
    public User(String id, String name, LocalDate joinDate, String platform, String imageUrl) {
        this.id = id;
        this.name = name;
        this.joinDate = joinDate;
        this.platform = platform;
        this.imageUrl = imageUrl;
    }
}
