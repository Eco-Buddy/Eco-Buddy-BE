package com.Son.EcoBuddy.JPA.Repository;

import com.Son.EcoBuddy.JPA.Entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    Optional<Pet> findByUserId(String userId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE pet SET mission = 3", nativeQuery = true)
    void resetMission();
}
