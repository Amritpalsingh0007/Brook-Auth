package com.akal.Brook_Auth.controller;

import com.akal.Brook_Auth.dto.request.AuthRequestDto;
import com.akal.Brook_Auth.dto.request.RefreshTokenRequestDto;
import com.akal.Brook_Auth.dto.response.JwtResponseDto;
import com.akal.Brook_Auth.entity.RefreshToken;
import com.akal.Brook_Auth.service.JwtService;
import com.akal.Brook_Auth.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("auth/v1/")
@AllArgsConstructor
public class TokenController {
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    @PostMapping("login")
    public ResponseEntity<Object> login(@RequestBody @Valid AuthRequestDto authRequestDto){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequestDto.getUsername(), authRequestDto.getPassword()));
        if(authentication.isAuthenticated()){
            RefreshToken refreshToken = refreshTokenService.generateRefreshToken(authRequestDto.getUsername());
            return new ResponseEntity<>(JwtResponseDto.builder()
                    .refreshToken(refreshToken.getRefreshToken())
                    .token(jwtService.generateToken(authRequestDto.getUsername()))
                    .build(), HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Username or Password is incorrect. Please try again!", HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("refreshToken")
    public ResponseEntity<JwtResponseDto> refreshToken(@RequestBody @Valid RefreshTokenRequestDto refreshTokenRequestDto){
        Optional<RefreshToken> refreshTokenOptional = refreshTokenService.findByToken(refreshTokenRequestDto.getToken());
        return refreshTokenOptional.map(refreshTokenService::isExpiredToken)
                .map(RefreshToken::getUserInfo)
                .map(userInfo -> new ResponseEntity<>(
                        JwtResponseDto.builder()
                                .token(jwtService.generateToken(userInfo.getUsername()))
                                .refreshToken(refreshTokenRequestDto.getToken())
                                .build(), HttpStatus.OK
                ))
            .orElse(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
    }
}
