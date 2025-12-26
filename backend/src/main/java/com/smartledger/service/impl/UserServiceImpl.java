package com.smartledger.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartledger.dto.LoginDTO;
import com.smartledger.dto.RegisterDTO;
import com.smartledger.entity.Bill;
import com.smartledger.entity.Diary;
import com.smartledger.entity.SavingPlan;
import com.smartledger.entity.User;
import com.smartledger.mapper.BillMapper;
import com.smartledger.mapper.DiaryMapper;
import com.smartledger.mapper.SavingPlanMapper;
import com.smartledger.mapper.UserMapper;
import com.smartledger.service.UserService;
import com.smartledger.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final BillMapper billMapper;
    private final DiaryMapper diaryMapper;
    private final SavingPlanMapper savingPlanMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public Map<String, Object> login(LoginDTO loginDTO) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, loginDTO.getUsername())
        );
        
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("email", user.getEmail());
        
        return result;
    }

    @Override
    public void register(RegisterDTO registerDTO) {
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, registerDTO.getUsername())
        );
        
        if (count > 0) {
            throw new RuntimeException("用户名已存在");
        }
        
        count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getEmail, registerDTO.getEmail())
        );
        
        if (count > 0) {
            throw new RuntimeException("邮箱已被注册");
        }
        
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setEmail(registerDTO.getEmail());
        user.setCurrency("CNY");
        user.setMonthlyBudget(5000.0);
        
        userMapper.insert(user);
    }

    @Override
    public User getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }

    @Override
    public void updateUser(User user) {
        user.setPassword(null);
        userMapper.updateById(user);
    }

    @Override
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
    }

    @Override
    public Map<String, Object> getUserStatistics(Long userId) {
        Long billCount = billMapper.selectCount(
                new LambdaQueryWrapper<Bill>().eq(Bill::getUserId, userId)
        );
        Long diaryCount = diaryMapper.selectCount(
                new LambdaQueryWrapper<Diary>().eq(Diary::getUserId, userId)
        );
        Long savingPlanCount = savingPlanMapper.selectCount(
                new LambdaQueryWrapper<SavingPlan>().eq(SavingPlan::getUserId, userId)
        );
        
        Map<String, Object> result = new HashMap<>();
        result.put("billCount", billCount);
        result.put("diaryCount", diaryCount);
        result.put("savingPlanCount", savingPlanCount);
        
        return result;
    }
}
