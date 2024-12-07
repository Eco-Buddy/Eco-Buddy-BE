package com.Lobo.KakaoLoginTest.JPA.Repository;

import com.Lobo.KakaoLoginTest.JPA.Entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    // deviceId로 토큰 삭제
    void deleteByDevice_deviceId(String deviceId);

    Optional<Token> findByDevice_deviceId(String deviceId);

}
