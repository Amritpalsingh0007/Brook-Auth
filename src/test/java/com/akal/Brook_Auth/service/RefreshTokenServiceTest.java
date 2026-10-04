package com.akal.Brook_Auth.service;


import com.akal.Brook_Auth.BrookAuthApplication;
import com.akal.Brook_Auth.entity.RefreshToken;
import com.akal.Brook_Auth.entity.UserInfo;
import com.akal.Brook_Auth.repository.RefreshTokenRepository;
import com.akal.Brook_Auth.repository.UserInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {
    @Mock
    private UserInfoRepository userInfoRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @InjectMocks
    private RefreshTokenService refreshTokenService;
    private RefreshToken refreshToken;
    private UserInfo userInfo;

    @BeforeEach
    public void initializeObjects(){
        userInfo = (UserInfo.builder().id(1).username("username").password("passWord1@").build());
        refreshToken = RefreshToken.builder()
                .userInfo(userInfo)
                .expiryInstant(Instant.now().plusSeconds(3600)).build();

    }

    @Test
    public void generateRefreshTokenTest(){
        String username = "username";
        when(userInfoRepository.findByUsername(username)).thenReturn(Optional.of(userInfo));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);
        RefreshToken refreshTokenGenerated = refreshTokenService.generateRefreshToken(username);
        assertNotNull(refreshTokenGenerated);
        assertSame(refreshToken, refreshTokenGenerated);
        assertSame(userInfo, refreshTokenGenerated.getUserInfo());
        verify(userInfoRepository).findByUsername(username);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    public void generateRefreshTokenWhenUserDoesNotExsist(){
        String username = "Unknown";
        when(userInfoRepository.findByUsername(username)).thenReturn(Optional.empty());
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            refreshTokenService.generateRefreshToken(username);
        });

        assertEquals("User is not register! Please sign up...!", runtimeException.getMessage());

        verify(userInfoRepository).findByUsername(username);
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }
}
