package com.construction_worker_forum_back.service;

import com.construction_worker_forum_back.config.security.JwtTokenUtil;
import com.construction_worker_forum_back.model.dto.LoginDto;
import com.construction_worker_forum_back.model.dto.LoginRequestDto;
import com.construction_worker_forum_back.model.security.UserDetailsImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@AllArgsConstructor
public class LoginService {

    AuthenticationManager authenticationManager;
    JwtTokenUtil jwtTokenUtil;

    public LoginDto login(LoginRequestDto loginRequestDto) {

        Authentication authenticate = authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequestDto.getUsername(), loginRequestDto.getPassword()
                        )
                );

        if (!authenticate.isAuthenticated()) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        var user = (UserDetailsImpl) authenticate.getPrincipal();
        var generatedToken = jwtTokenUtil.generateToken(user);

        return new LoginDto(generatedToken, user);
    }
}
