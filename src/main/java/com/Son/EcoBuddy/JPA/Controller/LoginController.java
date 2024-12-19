package com.Son.EcoBuddy.JPA.Controller;

import com.Son.EcoBuddy.JPA.Service.PetService;
import com.Son.EcoBuddy.JPA.Service.TokenValidationService;
import com.Son.EcoBuddy.JPA.Service.UserService;
import com.Son.EcoBuddy.Utility.AESUtil;
import com.Son.EcoBuddy.JPA.Service.TokenService;
import com.Son.EcoBuddy.JPA.Entity.User;
import com.Son.EcoBuddy.Login.KakaoAPI;
import com.Son.EcoBuddy.Login.NaverAPI;
import com.Son.EcoBuddy.Utility.TokenValidationResult;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Controller
public class LoginController {
    private final KakaoAPI kakaoAPI;
    private final NaverAPI naverAPI;
    private final UserService userService;
    private final TokenService tokenService;
    private final PetService petService;
    private final TokenValidationService tokenValidationService;

    @RequestMapping("/start")
    public ResponseEntity<?> getDeviceId(@RequestBody HashMap<String, Object> start_id, HttpSession session) {
        session.setAttribute("deviceId", (String) start_id.get("id"));

        return ResponseEntity.ok(Map.of());
    }

    // 카카오, 네이버 로그인 API를 이용한 로그인.
    @ResponseBody
    @RequestMapping("/login/request/{provider}")
    public String redirectToLogin(@PathVariable String provider, HttpSession session) {
        String deviceId = (String) session.getAttribute("deviceId");

        String authUri = "";
        if (provider.equalsIgnoreCase("kakao")) {
            authUri = "https://kauth.kakao.com/oauth/authorize"
                    + "?response_type=code"
                    + "&client_id=" + kakaoAPI.getKakaoApiKey()
                    + "&redirect_uri=" + kakaoAPI.getKakaoRedirectUri();
        }
        else if (provider.equalsIgnoreCase("naver")) {
            authUri = "https://nid.naver.com/oauth2.0/authorize"
                    + "?response_type=code"
                    + "&client_id=" + naverAPI.getNaverClientId()
                    + "&redirect_uri=" + naverAPI.getNaverRedirectUri()
                    + "&state=" + naverAPI.getState();
        }

        return authUri;
    }

    @RequestMapping("/login/oauth2/code/{provider}")
    public ResponseEntity<?> login(@PathVariable String provider, @RequestParam String code, HttpSession session) throws Exception {
        HashMap<String, String> tokens = new HashMap<>();
        User user = null;
        String deviceId = (String) session.getAttribute("deviceId");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Content-Type", "application/json;charset=UTF-8");

        if (deviceId == null) {
            return ResponseEntity.badRequest().body("Device ID is missing.");
        }

        if(provider.equalsIgnoreCase("kakao")) {
            tokens = kakaoAPI.getAccessToken(code);
            user = kakaoAPI.getUserInfo(tokens.get("access_token"));
        }
        else if (provider.equalsIgnoreCase("naver")) {
            tokens = naverAPI.getAccessToken(code);
            user = naverAPI.getUserInfo(tokens.get("access_token"));
        }

        user = userService.findOrRegisterUser(user);

        HashMap<String, String> encryptedTokens = AESUtil.encryptTokens(tokens);
        tokenService.createOrUpdateToken(encryptedTokens, user, deviceId);

        if(petService.findPet(user.getId()))
            headers.set("isNew", "0");
        else
            headers.set("isNew", "1");

        Map<String, Object> responseBody = Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "profile_image", user.getImageUrl(),
                "access_token", tokens.get("access_token")
        );

        return ResponseEntity.ok()
                .headers(headers)
                .body(responseBody);
    }

    @PostMapping("/check")
    public ResponseEntity<?> check(
            @RequestHeader("authorization") String accessToken,
            @RequestHeader("deviceId") String device_id,
            @RequestHeader("userId") String user_id) {

        String new_accessToken = null;

        try {
            TokenValidationResult result = tokenValidationService.validateAccessToken(user_id, device_id, accessToken);

            new_accessToken = AESUtil.decrypt(result.getSavedToken().getAccessToken());
        } catch (IllegalArgumentException e) {
            // 사용자 ID나 토큰이 없는 경우
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (SecurityException e) {
            // 유효하지 않은 access_token
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (IllegalStateException e) {
            // 토큰 갱신 간 문제 발생
            return ResponseEntity.status(500).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(Map.of("new_accessToken", new_accessToken));

    }

    // 로그아웃 처리
    @PostMapping("/{provider}/logout")
    public ResponseEntity<?> logout(@PathVariable String provider, @RequestBody HashMap<String, Object> token, HttpSession session) throws Exception {

        log.info(token.get("deviceId") + " logout request");
        session.invalidate();
        if (provider.equalsIgnoreCase("kakao")) {
            kakaoAPI.kakaoLogout((String) token.get("access_token"));
        } else if (provider.equalsIgnoreCase("naver")) {
            naverAPI.naverLogout((String) token.get("access_token"));
        }

        tokenService.deleteToken((String) token.get("deviceId"));

        return ResponseEntity.ok(Map.of());
    }

    // 회원탈퇴 처리
    @PostMapping("/{provider}/withdraw")
    public ResponseEntity<?> withdraw(@PathVariable String provider, @RequestBody HashMap<String, Object> token, HttpSession session) throws Exception {

        log.info(token.get("userId") + " withdraw reqeust");
        session.invalidate();
        if (provider.equalsIgnoreCase("kakao")) {
            kakaoAPI.kakaoLogout((String) token.get("access_token"));
        } else if (provider.equalsIgnoreCase("naver")) {
            naverAPI.naverLogout((String) token.get("access_token"));
        }

        tokenService.withdraw((String) token.get("userId"));

        return ResponseEntity.ok(Map.of());
    }
}