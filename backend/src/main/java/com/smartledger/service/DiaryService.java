package com.smartledger.service;

import com.smartledger.common.PageResult;
import com.smartledger.dto.DiaryDTO;
import com.smartledger.entity.Diary;

import java.time.LocalDate;

public interface DiaryService {
    void addDiary(Long userId, DiaryDTO diaryDTO);
    
    void updateDiary(Long userId, DiaryDTO diaryDTO);
    
    void deleteDiary(Long userId, Long diaryId);
    
    Diary getDiaryById(Long userId, Long diaryId);
    
    PageResult<Diary> getDiaryList(Long userId, LocalDate startDate, LocalDate endDate, Integer page, Integer size);
}
