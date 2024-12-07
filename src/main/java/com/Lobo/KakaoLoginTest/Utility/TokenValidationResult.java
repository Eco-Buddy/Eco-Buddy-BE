package com.Lobo.KakaoLoginTest.Utility;

import com.Lobo.KakaoLoginTest.JPA.Entity.Token;
import com.Lobo.KakaoLoginTest.JPA.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenValidationResult {
    private final User user;
    private final Token savedToken;
}
