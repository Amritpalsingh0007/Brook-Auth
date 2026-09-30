package com.akal.Brook_Auth.controller;

import com.akal.Brook_Auth.dto.request.UserInfoDto;
import com.akal.Brook_Auth.dto.response.JwtResponseDto;
import com.akal.Brook_Auth.service.JwtService;
import com.akal.Brook_Auth.service.RefreshTokenService;
import com.akal.Brook_Auth.service.UserInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/v1/")
@RequiredArgsConstructor
public class AuthController {
    private final JwtService jwtService;
    private final UserInfoService userInfoService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("signup")
    public ResponseEntity<Object> addNewUser(@RequestBody @Valid UserInfoDto userInfoDto){
        try{
            boolean isSignedUp = userInfoService.signUp(userInfoDto);
           if(!isSignedUp) return new ResponseEntity<>("Username already exsist!!", HttpStatus.CONFLICT);
           return new ResponseEntity<>(JwtResponseDto.builder().token(jwtService.generateToken(userInfoDto.getUsername())).refreshToken(refreshTokenService.generateRefreshToken(userInfoDto.getUsername()).getRefreshToken()).build(), HttpStatus.CREATED);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("ping")
    public ResponseEntity<String> ping(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.isAuthenticated()){
            Long userId = userInfoService.getUserIdByUsername(authentication.getName());
            return ResponseEntity.status(HttpStatus.OK).body(String.valueOf(userId));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("UnAuthorized");
    }

    @GetMapping("health")
    public ResponseEntity<Boolean> checkHealth(){
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

}
