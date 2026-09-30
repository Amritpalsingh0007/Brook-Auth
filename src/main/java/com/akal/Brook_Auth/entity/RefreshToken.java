package com.akal.Brook_Auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;


@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String refreshToken;
    private Instant expiryInstant;
    @OneToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private UserInfo userInfo;
}
