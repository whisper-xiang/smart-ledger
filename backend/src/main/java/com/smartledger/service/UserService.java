package com.smartledger.service;

import com.smartledger.dto.LoginDTO;
import com.smartledger.dto.RegisterDTO;
import com.smartledger.entity.User;

import java.util.Map;

public interface UserService {
    Map<String, Object> login(LoginDTO loginDTO);
    
    void register(RegisterDTO registerDTO);
    
    User getUserById(Long id);
    
    void updateUser(User user);
    
    void updatePassword(Long userId, String oldPassword, String newPassword);
}
