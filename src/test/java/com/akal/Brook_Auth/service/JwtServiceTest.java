package com.akal.Brook_Auth.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {
    private final String USERNAME = "username";
    private final String SECRET_KEY;

    @InjectMocks
    private JwtService jwtService;

    public JwtServiceTest(@Value("{Jwt.Secret}") String secret){
        SECRET_KEY = secret;
    }

    @Test
    public void  generateTokenTest(){
        String token = jwtService.generateToken(USERNAME);
        assertEquals(3, token.split(".").length);
    }
}
