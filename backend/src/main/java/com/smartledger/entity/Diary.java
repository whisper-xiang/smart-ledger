package com.smartledger.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("diary")
public class Diary {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String title;
    
    private String content;
    
    private String mood;
    
    private LocalDate diaryDate;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
