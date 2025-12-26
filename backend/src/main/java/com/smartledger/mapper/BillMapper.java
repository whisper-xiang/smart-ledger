package com.smartledger.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartledger.entity.Bill;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface BillMapper extends BaseMapper<Bill> {
    
    @Select("SELECT COALESCE(SUM(amount), 0) FROM bill WHERE user_id = #{userId} AND type = #{type} AND bill_date BETWEEN #{startDate} AND #{endDate} AND deleted = 0")
    BigDecimal sumAmountByTypeAndDateRange(@Param("userId") Long userId, @Param("type") String type, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Select("SELECT category, SUM(amount) as amount FROM bill WHERE user_id = #{userId} AND type = #{type} AND bill_date BETWEEN #{startDate} AND #{endDate} AND deleted = 0 GROUP BY category")
    List<Map<String, Object>> getCategoryStatistics(@Param("userId") Long userId, @Param("type") String type, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Select("SELECT bill_date as date, type, SUM(amount) as amount FROM bill WHERE user_id = #{userId} AND bill_date BETWEEN #{startDate} AND #{endDate} AND deleted = 0 GROUP BY bill_date, type ORDER BY bill_date")
    List<Map<String, Object>> getDailyTrend(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
