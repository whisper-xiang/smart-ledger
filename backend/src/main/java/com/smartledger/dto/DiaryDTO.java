package com.smartledger.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class DiaryDTO {
    private Long id;
    
    @NotBlank(message = "标题不能为空")
    private String title;
    
    @NotBlank(message = "内容不能为空")
    private String content;
    
    private String mood;
    
    @NotNull(message = "日期不能为空")
    private LocalDate diaryDate;
}
