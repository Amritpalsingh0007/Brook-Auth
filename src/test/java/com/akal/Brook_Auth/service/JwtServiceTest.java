package com.akal.Brook_Auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class JwtServiceTest {
    private final String USERNAME = "username";
    private String SECRET_KEY = Base64.getEncoder().encodeToString("Orz9MkWUyUQuxdoZRj2o2ciYAIMpFdexRRKPzdiO96c=".getBytes(StandardCharsets.UTF_8));
    private JwtService jwtService;

    @BeforeEach
    void setup(){
        jwtService=new JwtService(SECRET_KEY);
    }

    @Test
    public void  generateTokenTest(){
        String token = jwtService.generateToken(USERNAME);
        assertEquals(3, token.split("\\.").length);
    }
}
