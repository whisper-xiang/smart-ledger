package com.smartledger.controller;

import com.smartledger.common.Result;
import com.smartledger.entity.User;
import com.smartledger.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/info")
    public Result<User> getUserInfo(@RequestAttribute("userId") Long userId) {
        User user = userService.getUserById(userId);
        return Result.success(user);
    }

    @PutMapping("/info")
    public Result<Void> updateUserInfo(@RequestAttribute("userId") Long userId, @RequestBody User user) {
        user.setId(userId);
        userService.updateUser(user);
        return Result.success();
    }

    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestAttribute("userId") Long userId, @RequestBody Map<String, String> params) {
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        userService.updatePassword(userId, oldPassword, newPassword);
        return Result.success("密码修改成功", null);
    }
}
