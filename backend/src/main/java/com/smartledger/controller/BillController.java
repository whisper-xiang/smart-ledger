package com.smartledger.controller;

import com.smartledger.common.PageResult;
import com.smartledger.common.Result;
import com.smartledger.dto.BillDTO;
import com.smartledger.entity.Bill;
import com.smartledger.service.BillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;

    @PostMapping
    public Result<Void> addBill(@RequestAttribute("userId") Long userId, @Valid @RequestBody BillDTO billDTO) {
        billService.addBill(userId, billDTO);
        return Result.success("添加成功", null);
    }

    @PutMapping("/{id}")
    public Result<Void> updateBill(@RequestAttribute("userId") Long userId, @PathVariable Long id, @Valid @RequestBody BillDTO billDTO) {
        billDTO.setId(id);
        billService.updateBill(userId, billDTO);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteBill(@RequestAttribute("userId") Long userId, @PathVariable Long id) {
        billService.deleteBill(userId, id);
        return Result.success("删除成功", null);
    }

    @GetMapping("/{id}")
    public Result<Bill> getBill(@RequestAttribute("userId") Long userId, @PathVariable Long id) {
        Bill bill = billService.getBillById(userId, id);
        return Result.success(bill);
    }

    @GetMapping
    public Result<PageResult<Bill>> getBillList(
            @RequestAttribute("userId") Long userId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        PageResult<Bill> result = billService.getBillList(userId, type, category, startDate, endDate, page, size);
        return Result.success(result);
    }

    @GetMapping("/recent")
    public Result<List<Bill>> getRecentBills(@RequestAttribute("userId") Long userId, @RequestParam(defaultValue = "5") Integer limit) {
        List<Bill> bills = billService.getRecentBills(userId, limit);
        return Result.success(bills);
    }

    @GetMapping("/statistics/month")
    public Result<Map<String, Object>> getMonthStatistics(
            @RequestAttribute("userId") Long userId,
            @RequestParam Integer year,
            @RequestParam Integer month) {
        Map<String, Object> statistics = billService.getMonthStatistics(userId, year, month);
        return Result.success(statistics);
    }

    @GetMapping("/statistics/category")
    public Result<List<Map<String, Object>>> getCategoryStatistics(
            @RequestAttribute("userId") Long userId,
            @RequestParam String type,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<Map<String, Object>> statistics = billService.getCategoryStatistics(userId, type, startDate, endDate);
        return Result.success(statistics);
    }

    @GetMapping("/statistics/trend")
    public Result<List<Map<String, Object>>> getDailyTrend(
            @RequestAttribute("userId") Long userId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<Map<String, Object>> trend = billService.getDailyTrend(userId, startDate, endDate);
        return Result.success(trend);
    }
}
