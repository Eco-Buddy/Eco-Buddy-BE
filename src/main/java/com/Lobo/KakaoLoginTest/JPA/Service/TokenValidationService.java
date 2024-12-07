package com.Lobo.KakaoLoginTest.JPA.Service;

import com.Lobo.KakaoLoginTest.Utility.TokenValidationResult;
import com.Lobo.KakaoLoginTest.JPA.Entity.Token;
import com.Lobo.KakaoLoginTest.JPA.Entity.User;
import com.Lobo.KakaoLoginTest.JPA.Repository.DeviceRepository;
import com.Lobo.KakaoLoginTest.Utility.AESUtil;
import com.Lobo.KakaoLoginTest.JPA.Repository.TokenRepository;
import com.Lobo.KakaoLoginTest.JPA.Repository.UserRepository;
import com.Lobo.KakaoLoginTest.Login.KakaoAPI;
import com.Lobo.KakaoLoginTest.Login.NaverAPI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TokenValidationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final DeviceRepository deviceRepository;
    private final KakaoAPI kakaoAPI;
    private final NaverAPI naverAPI;

    public TokenValidationResult validateAccessToken(String user_id, String device_id, String accessToken) throws Exception {
        // User 검색
        Optional<User> userOpt = userRepository.findById(user_id);
        if(userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found for ID: " + user_id);
        }

        User user = userOpt.get();

        // Token 검색
        Token savedToken = tokenRepository.findByDevice_deviceId(device_id)
                .orElseThrow(() -> new IllegalArgumentException("Token not found for User ID : " + device_id));

        // Acceses Token 검증
        if (!accessToken.equals(AESUtil.decrypt(savedToken.getAccessToken()))) {
            throw new SecurityException("Invalid access token.");
        }

        // Token 만료 확인 및 갱신
        if(savedToken.isExpired()) {
            HashMap<String, String> refreshedToken = null;

            System.out.println(user.getPlatform());

            if(user.getPlatform().equals("kakao")) {
                refreshedToken = kakaoAPI.refreshAccessToken(savedToken);
            } else if(user.getPlatform().equals("naver")) {
                refreshedToken = naverAPI.refreshAccessToken(savedToken);
            }

            if (refreshedToken != null) {
                savedToken.setAccessToken(AESUtil.encrypt(refreshedToken.get("access_token")));
                savedToken.setAccExpPeriod(Integer.parseInt(refreshedToken.get("acc_exp_period")));
                savedToken.setAccIssueDate(LocalDateTime.now());

                if (refreshedToken.containsKey("refresh_token")) {
                    savedToken.setRefreshToken(AESUtil.encrypt(refreshedToken.get("refresh_token")));
                    savedToken.setRefExpPeriod(Integer.parseInt(refreshedToken.get("ref_exp_period")));
                    savedToken.setRefIssueDate(LocalDateTime.now());
                }

                tokenRepository.save(savedToken);
            } else {
                throw new IllegalStateException("Failed to refresh the token.");
            }
        }

        return new TokenValidationResult(user, savedToken);
    }
}
