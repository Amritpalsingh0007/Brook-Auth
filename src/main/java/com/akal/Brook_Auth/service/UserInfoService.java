package com.akal.Brook_Auth.service;

import com.akal.Brook_Auth.dto.request.UserInfoDto;
import com.akal.Brook_Auth.entity.UserInfo;
import com.akal.Brook_Auth.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserInfoService implements UserDetailsService {
    private final UserInfoRepository userInfoRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserInfo> userInfo = userInfoRepository.findByUsername(username);
        if(userInfo.isEmpty()) throw new UsernameNotFoundException("Username does not exist!");
        return new CustomUserDetails(userInfo.get());
    }

    private boolean usernameAlreadyExsist(String username){
        return userInfoRepository.findByUsername(username).isPresent();
    }
    public boolean signUp(UserInfoDto userInfo){
        //check if user already exsist or not
        if(usernameAlreadyExsist(userInfo.getUsername())) return false;
        userInfoRepository.save(new UserInfo(userInfo.getUsername(), passwordEncoder.encode(userInfo.getPassword())));
        //Add code for saving the new user in auth userinfo and then send the message to kafka topic to be consumed by the user service.
        return true;
    }

    public Long getUserIdByUsername(String name) {
        Optional<UserInfo> userInfoOptional = userInfoRepository.findByUsername(name);
        if(userInfoOptional.isEmpty()) throw new UsernameNotFoundException("Username does not exsist");
        return userInfoOptional.get().getId();
    }
}
