package com.construction_worker_forum_back.controller;

import com.construction_worker_forum_back.model.dto.LoginDto;
import com.construction_worker_forum_back.model.dto.LoginRequestDto;
import com.construction_worker_forum_back.service.LoginService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("http://localhost:3000")
@RequestMapping("/api")
@AllArgsConstructor
public class LoginController {
    LoginService loginService;

    @PostMapping("/login")
    public LoginDto loginUser(@RequestBody LoginRequestDto loginRequestDto) {
        return loginService.login(loginRequestDto);
    }
}
