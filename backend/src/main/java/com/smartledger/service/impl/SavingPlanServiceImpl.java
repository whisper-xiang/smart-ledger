package com.smartledger.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartledger.dto.SavingPlanDTO;
import com.smartledger.entity.SavingPlan;
import com.smartledger.mapper.SavingPlanMapper;
import com.smartledger.service.SavingPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SavingPlanServiceImpl implements SavingPlanService {

    private final SavingPlanMapper savingPlanMapper;

    @Override
    public void addPlan(Long userId, SavingPlanDTO planDTO) {
        SavingPlan plan = new SavingPlan();
        plan.setUserId(userId);
        plan.setName(planDTO.getName());
        plan.setGoalAmount(planDTO.getGoalAmount());
        plan.setSavedAmount(BigDecimal.ZERO);
        plan.setStartDate(planDTO.getStartDate());
        plan.setEndDate(planDTO.getEndDate());
        plan.setColor(planDTO.getColor() != null ? planDTO.getColor() : "#409eff");
        plan.setRemark(planDTO.getRemark());
        savingPlanMapper.insert(plan);
    }

    @Override
    public void updatePlan(Long userId, SavingPlanDTO planDTO) {
        SavingPlan plan = savingPlanMapper.selectById(planDTO.getId());
        if (plan == null || !plan.getUserId().equals(userId)) {
            throw new RuntimeException("存钱计划不存在");
        }
        plan.setName(planDTO.getName());
        plan.setGoalAmount(planDTO.getGoalAmount());
        plan.setStartDate(planDTO.getStartDate());
        plan.setEndDate(planDTO.getEndDate());
        plan.setColor(planDTO.getColor());
        plan.setRemark(planDTO.getRemark());
        savingPlanMapper.updateById(plan);
    }

    @Override
    public void deletePlan(Long userId, Long planId) {
        SavingPlan plan = savingPlanMapper.selectById(planId);
        if (plan == null || !plan.getUserId().equals(userId)) {
            throw new RuntimeException("存钱计划不存在");
        }
        savingPlanMapper.deleteById(planId);
    }

    @Override
    public SavingPlan getPlanById(Long userId, Long planId) {
        SavingPlan plan = savingPlanMapper.selectById(planId);
        if (plan == null || !plan.getUserId().equals(userId)) {
            throw new RuntimeException("存钱计划不存在");
        }
        return plan;
    }

    @Override
    public List<SavingPlan> getPlanList(Long userId) {
        LambdaQueryWrapper<SavingPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SavingPlan::getUserId, userId)
                .orderByDesc(SavingPlan::getCreateTime);
        return savingPlanMapper.selectList(wrapper);
    }

    @Override
    public void deposit(Long userId, Long planId, BigDecimal amount) {
        SavingPlan plan = savingPlanMapper.selectById(planId);
        if (plan == null || !plan.getUserId().equals(userId)) {
            throw new RuntimeException("存钱计划不存在");
        }
        plan.setSavedAmount(plan.getSavedAmount().add(amount));
        savingPlanMapper.updateById(plan);
    }
}
