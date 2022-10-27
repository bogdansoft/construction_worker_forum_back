package com.construction_worker_forum_back.service;

import com.construction_worker_forum_back.config.security.JwtTokenUtil;
import com.construction_worker_forum_back.model.dto.LoginDto;
import com.construction_worker_forum_back.model.dto.LoginRequestDto;
import com.construction_worker_forum_back.model.security.UserDetailsImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@AllArgsConstructor
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    public LoginDto login(LoginRequestDto loginRequestDto) {

        Authentication authenticate = authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequestDto.getUsername(), loginRequestDto.getPassword()
                        )
                );

        var user = (UserDetailsImpl) authenticate.getPrincipal();

        if (!authenticate.isAuthenticated()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        return new LoginDto(jwtTokenUtil.generateToken(user), user.getUser().getId(), user.getUsername());
    }
}
