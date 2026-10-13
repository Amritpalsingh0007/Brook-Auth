package com.akal.Brook_Auth.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.security.WeakKeyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class JwtServiceTest {
    private final String USERNAME = "username";
    private String SECRET_KEY = Base64.getEncoder().encodeToString("Orz9MkWUyUQuxdoZRj2o2ciYAIMpFdexRRKPzdiO96c=".getBytes(StandardCharsets.UTF_8));
    private JwtService jwtService;

    private SecretKey key (String secret){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }
    private String buildToken(String username, Date issuedAt, Date end, SecretKey key){
        return Jwts.builder()
                .subject(username)
                .expiration(end)
                .issuedAt(issuedAt)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    private UserDetails userDetails(String username){
        return User.builder().username(username).password("irrelevant").authorities(List.of()).build();
    }

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

    @Test
    @DisplayName("Expired Token Test -> ExperiedJwtException")
    public void expiredTokenTest(){
        long now = System.currentTimeMillis();
        String expired = buildToken(USERNAME, new Date(now - 60000), new Date(now - 1000), key(SECRET_KEY));

        assertThatThrownBy(()->{jwtService.extractUsername(expired);}).isInstanceOf(ExpiredJwtException.class);
        assertThatThrownBy(()->{jwtService.extractExpiration(expired);}).isInstanceOf(ExpiredJwtException.class);
        assertThatThrownBy(()->{jwtService.validateToken(expired, userDetails(USERNAME));}).isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    @DisplayName("Forged Token Test")
    public void forgedTokenTest(){
        String forgedSecret = Base64.getEncoder().encodeToString("23712472431086f38b706f57a9096026b3139b56104357c38ced46c2a13af9e7".getBytes());
        long now = System.currentTimeMillis();
        String forgedToken = buildToken(USERNAME, new Date(now), new Date(now + 60_000), key(forgedSecret));
        assertThatThrownBy(()->{jwtService.extractUsername(forgedToken);}).isInstanceOf(SignatureException.class);
    }

    @Test
    @DisplayName("Tempered Payload Test")
    public void temperedPayloadTest(){
        long now = System.currentTimeMillis();
        String token = buildToken(USERNAME, new Date(now), new Date(now + 60_000), key(SECRET_KEY));

        String[] parts =  token.split("\\.");

        String temperedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString("{\"role\": \"admin\"}".getBytes());
        String temperedToken = parts[0] + "." + temperedPayload + "." + parts[2];
        assertThatThrownBy(() -> {jwtService.extractUsername(temperedToken);}).isInstanceOf(JwtException.class);
    }

    @Test
    @DisplayName("Malformed Jwt Token")
    public void malformedJwtTokenTest(){
        assertThatThrownBy(()->{jwtService.extractUsername("not a valid jwt");}).isInstanceOf(MalformedJwtException.class);
    }

    @Test
    @DisplayName("Unsigned token test")
    public void unsignedTokenTest(){
        String unsignedToken = Jwts.builder().subject(USERNAME).expiration(new Date(System.currentTimeMillis() + 60_000)).issuedAt(new Date(System.currentTimeMillis())).compact();
        assertThatThrownBy(()->{jwtService.extractUsername(unsignedToken);}).isInstanceOf(JwtException.class);
    }

}
