package com.akal.Brook_Auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class JwtResponseDto {
    private String token;
    private String refreshToken;
}
