package com.Lobo.KakaoLoginTest;

import com.Lobo.KakaoLoginTest.Login.KakaoAPI;
import com.Lobo.KakaoLoginTest.Login.NaverAPI;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class loginMain {
    @Autowired
    private final KakaoAPI kakaoApi;

    @Autowired
    private final NaverAPI naverAPI;

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("kakaoApiKey", kakaoApi.getKakaoApiKey());
        model.addAttribute("redirectUri", kakaoApi.getKakaoRedirectUri());
        model.addAttribute("naverClientId", naverAPI.getNaverClientId());
        model.addAttribute("naverClientSecret", naverAPI.getNaverClientSecret());
        model.addAttribute("naverRedirectUri", naverAPI.getNaverRedirectUri());
        model.addAttribute("naverState", naverAPI.getState());
        return "LogIn";
    }
}
