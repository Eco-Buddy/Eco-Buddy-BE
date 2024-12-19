package com.Son.EcoBuddy.JPA.Repository;

import com.Son.EcoBuddy.JPA.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
}
