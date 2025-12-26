package com.smartledger.controller;

import com.smartledger.common.Result;
import com.smartledger.dto.LoginDTO;
import com.smartledger.dto.RegisterDTO;
import com.smartledger.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO loginDTO) {
        Map<String, Object> data = userService.login(loginDTO);
        return Result.success("登录成功", data);
    }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO registerDTO) {
        userService.register(registerDTO);
        return Result.success("注册成功", null);
    }
}
