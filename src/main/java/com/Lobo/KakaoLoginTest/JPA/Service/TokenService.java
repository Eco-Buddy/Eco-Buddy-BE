package com.Lobo.KakaoLoginTest.JPA.Service;

import com.Lobo.KakaoLoginTest.JPA.Entity.Device;
import com.Lobo.KakaoLoginTest.JPA.Entity.Token;
import com.Lobo.KakaoLoginTest.JPA.Entity.User;
import com.Lobo.KakaoLoginTest.JPA.Repository.DeviceRepository;
import com.Lobo.KakaoLoginTest.JPA.Repository.TokenRepository;
import com.Lobo.KakaoLoginTest.JPA.Repository.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class TokenService {

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;

    @Autowired
    private EntityManager entityManager;

    public void createOrUpdateToken(HashMap<String, String> tokens, User user, String deviceId) {
        userRepository.save(user);

        Device new_device = Device.builder().deviceId(deviceId).user(user).build();
        deviceRepository.save(new_device);

        // 기존 토큰 데이터 조회
        Optional<Token> existingToken = tokenRepository.findByDevice_deviceId(deviceId);

        Token userToken;
        if (existingToken.isPresent()) {
            // 기존 데이터 수정
            Token oldToken = existingToken.get();
            oldToken.setAccessToken(tokens.get("access_token"));
            oldToken.setAccExpPeriod(Integer.parseInt(tokens.get("acc_exp_period")));
            oldToken.setAccIssueDate(LocalDateTime.now());
            oldToken.setRefreshToken(tokens.get("refresh_token"));
            oldToken.setRefExpPeriod(Integer.parseInt(tokens.get("ref_exp_period")));
            oldToken.setRefIssueDate(LocalDateTime.now());

            tokenRepository.save(oldToken);
        } else {
            // 새로운 데이터 삽입
            userToken = Token.builder()
                    .deviceId(new_device)
                    .accessToken(tokens.get("access_token"))
                    .accExpPeriod(Integer.parseInt(tokens.get("acc_exp_period")))
                    .accIssueDate(LocalDateTime.now())
                    .refreshToken(tokens.get("refresh_token"))
                    .refExpPeriod(Integer.parseInt(tokens.get("ref_exp_period")))
                    .refIssueDate(LocalDateTime.now())
                    .build();

            tokenRepository.save(userToken);
        }
    }

    public void deleteToken(String deviceId) {
        tokenRepository.deleteByDevice_deviceId(deviceId);
    }
}
