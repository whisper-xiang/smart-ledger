package com.smartledger.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BillDTO {
    private Long id;
    
    @NotBlank(message = "类型不能为空")
    private String type;
    
    @NotBlank(message = "分类不能为空")
    private String category;
    
    @NotNull(message = "金额不能为空")
    @Positive(message = "金额必须大于0")
    private BigDecimal amount;
    
    private String description;
    
    @NotNull(message = "日期不能为空")
    private LocalDate billDate;
}
