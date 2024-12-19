package com.Son.EcoBuddy.Utility;

import com.Son.EcoBuddy.JPA.Entity.Token;
import com.Son.EcoBuddy.JPA.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenValidationResult {
    private final User user;
    private final Token savedToken;
}
