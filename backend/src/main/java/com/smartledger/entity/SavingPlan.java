package com.smartledger.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("saving_plan")
public class SavingPlan {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String name;
    
    private BigDecimal goalAmount;
    
    private BigDecimal savedAmount;
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private String color;
    
    private String remark;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
