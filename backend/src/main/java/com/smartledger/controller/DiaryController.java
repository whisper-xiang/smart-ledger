package com.smartledger.controller;

import com.smartledger.common.PageResult;
import com.smartledger.common.Result;
import com.smartledger.dto.DiaryDTO;
import com.smartledger.entity.Diary;
import com.smartledger.service.DiaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/diaries")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    @PostMapping
    public Result<Void> addDiary(@RequestAttribute("userId") Long userId, @Valid @RequestBody DiaryDTO diaryDTO) {
        diaryService.addDiary(userId, diaryDTO);
        return Result.success("添加成功", null);
    }

    @PutMapping("/{id}")
    public Result<Void> updateDiary(@RequestAttribute("userId") Long userId, @PathVariable Long id, @Valid @RequestBody DiaryDTO diaryDTO) {
        diaryDTO.setId(id);
        diaryService.updateDiary(userId, diaryDTO);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteDiary(@RequestAttribute("userId") Long userId, @PathVariable Long id) {
        diaryService.deleteDiary(userId, id);
        return Result.success("删除成功", null);
    }

    @GetMapping("/{id}")
    public Result<Diary> getDiary(@RequestAttribute("userId") Long userId, @PathVariable Long id) {
        Diary diary = diaryService.getDiaryById(userId, id);
        return Result.success(diary);
    }

    @GetMapping
    public Result<PageResult<Diary>> getDiaryList(
            @RequestAttribute("userId") Long userId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        PageResult<Diary> result = diaryService.getDiaryList(userId, startDate, endDate, page, size);
        return Result.success(result);
    }
}
