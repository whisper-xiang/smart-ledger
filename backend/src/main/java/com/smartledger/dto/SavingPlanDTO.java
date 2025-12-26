package com.smartledger.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SavingPlanDTO {
    private Long id;
    
    @NotBlank(message = "计划名称不能为空")
    private String name;
    
    @NotNull(message = "目标金额不能为空")
    @Positive(message = "目标金额必须大于0")
    private BigDecimal goalAmount;
    
    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;
    
    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;
    
    private String color;
    
    private String remark;
}
