package com.smartledger.service;

import com.smartledger.common.PageResult;
import com.smartledger.dto.BillDTO;
import com.smartledger.entity.Bill;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface BillService {
    void addBill(Long userId, BillDTO billDTO);
    
    void updateBill(Long userId, BillDTO billDTO);
    
    void deleteBill(Long userId, Long billId);
    
    Bill getBillById(Long userId, Long billId);
    
    PageResult<Bill> getBillList(Long userId, String type, String category, LocalDate startDate, LocalDate endDate, Integer page, Integer size);
    
    List<Bill> getRecentBills(Long userId, Integer limit);
    
    Map<String, Object> getMonthStatistics(Long userId, Integer year, Integer month);
    
    List<Map<String, Object>> getCategoryStatistics(Long userId, String type, LocalDate startDate, LocalDate endDate);
    
    List<Map<String, Object>> getDailyTrend(Long userId, LocalDate startDate, LocalDate endDate);
}
