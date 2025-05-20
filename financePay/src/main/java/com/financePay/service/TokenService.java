package com.financePay.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.financePay.model.Token;
import com.financePay.model.UserBasic;

@Service
public class TokenService {

    private final Algorithm algorithm = Algorithm.HMAC256("secret");

    public Token createToken(UserBasic user) {
        Instant expiresAt = LocalDateTime.now()
                .plusHours(8)
                .toInstant(ZoneOffset.UTC);

        String jwt = JWT.create()
                .withSubject(String.valueOf(user.getId()))
                .withClaim("email", user.getEmail())
                .withExpiresAt(expiresAt)
                .sign(algorithm);

        return new Token(jwt, user.getEmail());
    }

    public UserBasic getUserFromToken(String token) {
        var verifiedToken = JWT.require(algorithm)
                .build()
                .verify(token);

        return UserBasic.builder()
                .id(Long.valueOf(verifiedToken.getSubject()))
                .email(verifiedToken.getClaim("email").asString())
                .build();
    }
}
