package com.smartledger.controller;

import com.smartledger.common.Result;
import com.smartledger.dto.SavingPlanDTO;
import com.smartledger.entity.SavingPlan;
import com.smartledger.service.SavingPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/savings")
@RequiredArgsConstructor
public class SavingPlanController {

    private final SavingPlanService savingPlanService;

    @PostMapping
    public Result<Void> addPlan(@RequestAttribute("userId") Long userId, @Valid @RequestBody SavingPlanDTO planDTO) {
        savingPlanService.addPlan(userId, planDTO);
        return Result.success("创建成功", null);
    }

    @PutMapping("/{id}")
    public Result<Void> updatePlan(@RequestAttribute("userId") Long userId, @PathVariable Long id, @Valid @RequestBody SavingPlanDTO planDTO) {
        planDTO.setId(id);
        savingPlanService.updatePlan(userId, planDTO);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deletePlan(@RequestAttribute("userId") Long userId, @PathVariable Long id) {
        savingPlanService.deletePlan(userId, id);
        return Result.success("删除成功", null);
    }

    @GetMapping("/{id}")
    public Result<SavingPlan> getPlan(@RequestAttribute("userId") Long userId, @PathVariable Long id) {
        SavingPlan plan = savingPlanService.getPlanById(userId, id);
        return Result.success(plan);
    }

    @GetMapping
    public Result<List<SavingPlan>> getPlanList(@RequestAttribute("userId") Long userId) {
        List<SavingPlan> plans = savingPlanService.getPlanList(userId);
        return Result.success(plans);
    }

    @PostMapping("/{id}/deposit")
    public Result<Void> deposit(@RequestAttribute("userId") Long userId, @PathVariable Long id, @RequestBody Map<String, BigDecimal> params) {
        BigDecimal amount = params.get("amount");
        savingPlanService.deposit(userId, id, amount);
        return Result.success("存入成功", null);
    }
}
