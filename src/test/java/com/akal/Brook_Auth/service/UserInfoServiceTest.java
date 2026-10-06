package com.akal.Brook_Auth.service;

import com.akal.Brook_Auth.dto.request.UserInfoDto;
import com.akal.Brook_Auth.entity.UserInfo;
import com.akal.Brook_Auth.repository.UserInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserInfoServiceTest {

    private static final String USERNAME = "username";
    private static final String UNKNOWN_USERNAME = "unknown";
    private static final String RAW_PASSWORD = "rawPassword";
    private static final String ENCODED_PASSWORD = "encodedPassword";

    @Mock
    private UserInfoRepository userInfoRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserInfoService userInfoService;
    @Captor
    private ArgumentCaptor<UserInfo> userInfoCaptor;

    private UserInfo userInfo;
    private UserInfoDto signUpRequest;

    @BeforeEach
    void setUp() {
        userInfo = UserInfo.builder().id(1L).username(USERNAME).password(ENCODED_PASSWORD).build();
        signUpRequest = UserInfoDto.builder().username(USERNAME).password(RAW_PASSWORD).build();
    }

    @Test
    void loadUserByUsername_whenUserExists_returnsUserDetails() {
        when(userInfoRepository.findByUsername(USERNAME)).thenReturn(Optional.of(userInfo));

        UserDetails result = userInfoService.loadUserByUsername(USERNAME);

        assertNotNull(result);
        assertEquals(USERNAME, result.getUsername());
        assertEquals(ENCODED_PASSWORD, result.getPassword());
    }

    @Test
    void loadUserByUsername_whenUserMissing_throwsUsernameNotFound() {
        when(userInfoRepository.findByUsername(UNKNOWN_USERNAME)).thenReturn(Optional.empty());

        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class,
                () -> userInfoService.loadUserByUsername(UNKNOWN_USERNAME));

        assertEquals("Username does not exist!", ex.getMessage());
    }

    @Test
    void signUp_whenUsernameIsFree_savesUserWithEncodedPassword() {
        when(userInfoRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);

        boolean result = userInfoService.signUp(signUpRequest);

        assertTrue(result);
        verify(userInfoRepository).save(userInfoCaptor.capture());
        UserInfo saved = userInfoCaptor.getValue();
        assertEquals(USERNAME, saved.getUsername());
        assertEquals(ENCODED_PASSWORD, saved.getPassword()); // fails if raw password is saved
    }

    @Test
    void signUp_whenUsernameExists_returnsFalseAndSavesNothing() {
        when(userInfoRepository.findByUsername(USERNAME)).thenReturn(Optional.of(userInfo));

        boolean result = userInfoService.signUp(signUpRequest);

        assertFalse(result);
        verify(userInfoRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void getUserIdByUsername_whenUserExists_returnsId() {
        when(userInfoRepository.findByUsername(USERNAME)).thenReturn(Optional.of(userInfo));

        Long userId = userInfoService.getUserIdByUsername(USERNAME);

        assertEquals(1L, userId);
    }

    @Test
    void getUserIdByUsername_whenUserMissing_throwsUsernameNotFound() {
        when(userInfoRepository.findByUsername(UNKNOWN_USERNAME)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userInfoService.getUserIdByUsername(UNKNOWN_USERNAME));
    }
}