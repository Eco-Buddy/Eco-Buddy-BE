package com.Lobo.KakaoLoginTest.JPA.Controller;

import com.Lobo.KakaoLoginTest.JPA.Service.PetService;
import com.Lobo.KakaoLoginTest.Utility.AESUtil;
import com.Lobo.KakaoLoginTest.JPA.Service.TokenService;
import com.Lobo.KakaoLoginTest.JPA.Entity.User;
import com.Lobo.KakaoLoginTest.Login.KakaoAPI;
import com.Lobo.KakaoLoginTest.Login.NaverAPI;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Controller
public class LoginController {
    private final KakaoAPI kakaoAPI;
    private final NaverAPI naverAPI;
    private final TokenService dbManager;
    private final PetService petService;

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

        HashMap<String, String> encryptedTokens = AESUtil.encryptTokens(tokens);
        dbManager.createOrUpdateToken(encryptedTokens, user, deviceId);

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

    // 로그아웃 처리
    @PostMapping("/{provider}/logout")
    public ResponseEntity<?> logout(@PathVariable String provider, @RequestBody HashMap<String, Object> token, HttpSession session) throws Exception {

        session.invalidate();
        if (provider.equalsIgnoreCase("kakao")) {
            kakaoAPI.kakaoLogout((String) token.get("access_token"));
        } else if (provider.equalsIgnoreCase("naver")) {
            naverAPI.naverLogout((String) token.get("access_token"));
        }

        dbManager.deleteToken((String) token.get("id"));

        return ResponseEntity.ok(Map.of());
    }
}