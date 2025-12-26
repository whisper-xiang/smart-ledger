package com.smartledger.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartledger.common.PageResult;
import com.smartledger.dto.DiaryDTO;
import com.smartledger.entity.Diary;
import com.smartledger.mapper.DiaryMapper;
import com.smartledger.service.DiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DiaryServiceImpl implements DiaryService {

    private final DiaryMapper diaryMapper;

    @Override
    public void addDiary(Long userId, DiaryDTO diaryDTO) {
        Diary diary = new Diary();
        diary.setUserId(userId);
        diary.setTitle(diaryDTO.getTitle());
        diary.setContent(diaryDTO.getContent());
        diary.setMood(diaryDTO.getMood());
        diary.setDiaryDate(diaryDTO.getDiaryDate());
        diaryMapper.insert(diary);
    }

    @Override
    public void updateDiary(Long userId, DiaryDTO diaryDTO) {
        Diary diary = diaryMapper.selectById(diaryDTO.getId());
        if (diary == null || !diary.getUserId().equals(userId)) {
            throw new RuntimeException("日记不存在");
        }
        diary.setTitle(diaryDTO.getTitle());
        diary.setContent(diaryDTO.getContent());
        diary.setMood(diaryDTO.getMood());
        diary.setDiaryDate(diaryDTO.getDiaryDate());
        diaryMapper.updateById(diary);
    }

    @Override
    public void deleteDiary(Long userId, Long diaryId) {
        Diary diary = diaryMapper.selectById(diaryId);
        if (diary == null || !diary.getUserId().equals(userId)) {
            throw new RuntimeException("日记不存在");
        }
        diaryMapper.deleteById(diaryId);
    }

    @Override
    public Diary getDiaryById(Long userId, Long diaryId) {
        Diary diary = diaryMapper.selectById(diaryId);
        if (diary == null || !diary.getUserId().equals(userId)) {
            throw new RuntimeException("日记不存在");
        }
        return diary;
    }

    @Override
    public PageResult<Diary> getDiaryList(Long userId, LocalDate startDate, LocalDate endDate, Integer page, Integer size) {
        LambdaQueryWrapper<Diary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Diary::getUserId, userId);
        
        if (startDate != null) {
            wrapper.ge(Diary::getDiaryDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(Diary::getDiaryDate, endDate);
        }
        
        wrapper.orderByDesc(Diary::getDiaryDate).orderByDesc(Diary::getId);
        
        Page<Diary> pageResult = diaryMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(pageResult.getTotal(), pageResult.getRecords());
    }
}
