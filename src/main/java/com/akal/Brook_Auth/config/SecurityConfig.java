package com.akal.Brook_Auth.config;

import com.akal.Brook_Auth.filter.JwtAuthFilter;
import com.akal.Brook_Auth.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

@Component
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserInfoService userInfoService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, JwtAuthFilter jwtAuthFilter) throws Exception{
        return httpSecurity
                .csrf(CsrfConfigurer::disable)
                .cors(CorsConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/v1/login", "/auth/v1/signup", "/auth/v1/refreshToken", "/auth/v1/ping","/auth/v1/health").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(Customizer.withDefaults())
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(userInfoService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return daoAuthenticationProvider;
    }
    //    AuthenticationManager is required for programmatic authentication e.g. /generateToken
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration){
        return configuration.getAuthenticationManager();
    }
}
