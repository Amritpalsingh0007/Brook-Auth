package com.akal.Brook_Auth.service;

import io.jsonwebtoken.security.WeakKeyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class JwtServiceTest {
    private final String USERNAME = "username";
    private String SECRET_KEY = Base64.getEncoder().encodeToString("Orz9MkWUyUQuxdoZRj2o2ciYAIMpFdexRRKPzdiO96c=".getBytes(StandardCharsets.UTF_8));
    private JwtService jwtService;

    @BeforeEach
    void setup(){
        jwtService=new JwtService(SECRET_KEY);
    }

    @Test
    @DisplayName("Checking the token is valid Jwt token with 3 dot-separated parts")
    public void  generateTokenTest(){
        String token = jwtService.generateToken(USERNAME);
        assertThat(token).isNotBlank();
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    @DisplayName("generateToken - setsSubject correctly")
    public void generateTokenTestSubject(){
        String token =  jwtService.generateToken(USERNAME);
        assertThat(token).isNotBlank();
        assertEquals(USERNAME, jwtService.extractUsername(token));
    }

    @Test
    @DisplayName("generateToken - checking if expiry is set to 10 minutes")
    public void generateTokenTestExpiryTenMin(){
            long before = System.currentTimeMillis();
            String token = jwtService.generateToken(USERNAME);
            long after = System.currentTimeMillis();

            long expiry = jwtService.extractExpiration(token).getTime();
            long tenMill = 10 * 60 * 1000L;

            assertThat(expiry).isBetween(before + tenMill - 1000, after + tenMill + 1000);
    }

    @ParameterizedTest
    @ValueSource(strings = {"alice", "book@example.com", "user with sapce","ünïcödé"})
    public void generateTokenTestDiffUsernames(String username){
        String token = jwtService.generateToken(username);

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo(username);
    }

    @Test
    @DisplayName("Chekcing WeakKeyException")
    public void generateTokenTestWeakKeyException(){
        String secret = Base64.getEncoder().encodeToString("too-short".getBytes(StandardCharsets.UTF_8));

        JwtService weakJwtService = new JwtService(secret);
        assertThatThrownBy(()->{weakJwtService.generateToken(USERNAME);}).isInstanceOf(WeakKeyException.class);

    }
}
