package com.smartledger.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartledger.common.PageResult;
import com.smartledger.dto.BillDTO;
import com.smartledger.entity.Bill;
import com.smartledger.mapper.BillMapper;
import com.smartledger.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BillServiceImpl implements BillService {

    private final BillMapper billMapper;

    @Override
    public void addBill(Long userId, BillDTO billDTO) {
        Bill bill = new Bill();
        bill.setUserId(userId);
        bill.setType(billDTO.getType());
        bill.setCategory(billDTO.getCategory());
        bill.setAmount(billDTO.getAmount());
        bill.setDescription(billDTO.getDescription());
        bill.setBillDate(billDTO.getBillDate());
        billMapper.insert(bill);
    }

    @Override
    public void updateBill(Long userId, BillDTO billDTO) {
        Bill bill = billMapper.selectById(billDTO.getId());
        if (bill == null || !bill.getUserId().equals(userId)) {
            throw new RuntimeException("账单不存在");
        }
        bill.setType(billDTO.getType());
        bill.setCategory(billDTO.getCategory());
        bill.setAmount(billDTO.getAmount());
        bill.setDescription(billDTO.getDescription());
        bill.setBillDate(billDTO.getBillDate());
        billMapper.updateById(bill);
    }

    @Override
    public void deleteBill(Long userId, Long billId) {
        Bill bill = billMapper.selectById(billId);
        if (bill == null || !bill.getUserId().equals(userId)) {
            throw new RuntimeException("账单不存在");
        }
        billMapper.deleteById(billId);
    }

    @Override
    public Bill getBillById(Long userId, Long billId) {
        Bill bill = billMapper.selectById(billId);
        if (bill == null || !bill.getUserId().equals(userId)) {
            throw new RuntimeException("账单不存在");
        }
        return bill;
    }

    @Override
    public PageResult<Bill> getBillList(Long userId, String type, String category, LocalDate startDate, LocalDate endDate, Integer page, Integer size) {
        LambdaQueryWrapper<Bill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Bill::getUserId, userId);
        
        if (type != null && !type.isEmpty()) {
            wrapper.eq(Bill::getType, type);
        }
        if (category != null && !category.isEmpty()) {
            wrapper.eq(Bill::getCategory, category);
        }
        if (startDate != null) {
            wrapper.ge(Bill::getBillDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(Bill::getBillDate, endDate);
        }
        
        wrapper.orderByDesc(Bill::getBillDate).orderByDesc(Bill::getId);
        
        Page<Bill> pageResult = billMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.of(pageResult.getTotal(), pageResult.getRecords());
    }

    @Override
    public List<Bill> getRecentBills(Long userId, Integer limit) {
        LambdaQueryWrapper<Bill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Bill::getUserId, userId)
                .orderByDesc(Bill::getBillDate)
                .orderByDesc(Bill::getId)
                .last("LIMIT " + limit);
        return billMapper.selectList(wrapper);
    }

    @Override
    public Map<String, Object> getMonthStatistics(Long userId, Integer year, Integer month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        
        BigDecimal income = billMapper.sumAmountByTypeAndDateRange(userId, "income", startDate, endDate);
        BigDecimal expense = billMapper.sumAmountByTypeAndDateRange(userId, "expense", startDate, endDate);
        
        Map<String, Object> result = new HashMap<>();
        result.put("income", income != null ? income : BigDecimal.ZERO);
        result.put("expense", expense != null ? expense : BigDecimal.ZERO);
        result.put("balance", (income != null ? income : BigDecimal.ZERO).subtract(expense != null ? expense : BigDecimal.ZERO));
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getCategoryStatistics(Long userId, String type, LocalDate startDate, LocalDate endDate) {
        return billMapper.getCategoryStatistics(userId, type, startDate, endDate);
    }
}
