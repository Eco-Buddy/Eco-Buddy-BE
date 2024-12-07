package com.Lobo.KakaoLoginTest.JPA.Repository;

import com.Lobo.KakaoLoginTest.JPA.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
}
