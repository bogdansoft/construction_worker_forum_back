package com.construction_worker_forum_back.controller;

import com.construction_worker_forum_back.model.dto.LoginDto;
import com.construction_worker_forum_back.model.dto.LoginRequestDto;
import com.construction_worker_forum_back.service.LoginService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@CrossOrigin("https://localhost:3000")
@RequestMapping("/api")
@Tag(name = "Login", description = "The Login API. Contains all the operations that can be performed on a log in.")
@AllArgsConstructor
public class LoginController {
    LoginService loginService;

    @PostMapping("/login")
    public LoginDto loginUser(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        return loginService.login(loginRequestDto);
    }
}
