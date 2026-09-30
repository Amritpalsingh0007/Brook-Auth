package com.akal.Brook_Auth.service;

import com.akal.Brook_Auth.entity.RefreshToken;
import com.akal.Brook_Auth.entity.UserInfo;
import com.akal.Brook_Auth.repository.RefreshTokenRepository;
import com.akal.Brook_Auth.repository.UserInfoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserInfoRepository userInfoRepository;

    public RefreshToken generateRefreshToken(String username){
        Optional<UserInfo> userInfo = userInfoRepository.findByUsername(username);
        if(userInfo.isEmpty()) throw new RuntimeException("User is not register! Please sign up...!");

        RefreshToken refreshToken = RefreshToken.builder()
                .refreshToken(UUID.randomUUID().toString())
                .userInfo(userInfo.get())
                .expiryInstant(Instant.now().plusMillis(3600_000))
                .build();
        return refreshTokenRepository.save(refreshToken);
    }
    public RefreshToken isExpiredToken(RefreshToken token){
        if(token.getExpiryInstant().compareTo(Instant.now()) < 0){
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getRefreshToken() + " Refresh token is expired. Please login again...!");
        }
        return token;
    }

    public Optional<RefreshToken> findByToken(String token){
        return refreshTokenRepository.findByRefreshToken(token);
    }
}
