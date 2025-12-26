package com.smartledger.service;

import com.smartledger.dto.SavingPlanDTO;
import com.smartledger.entity.SavingPlan;

import java.math.BigDecimal;
import java.util.List;

public interface SavingPlanService {
    void addPlan(Long userId, SavingPlanDTO planDTO);
    
    void updatePlan(Long userId, SavingPlanDTO planDTO);
    
    void deletePlan(Long userId, Long planId);
    
    SavingPlan getPlanById(Long userId, Long planId);
    
    List<SavingPlan> getPlanList(Long userId);
    
    void deposit(Long userId, Long planId, BigDecimal amount);
}
