package com.hjb.nice.server.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface StatsMapper {

    @Select("SELECT Policy_Type AS label, COUNT(*) AS value FROM hjb_policy GROUP BY Policy_Type")
    List<Map<String, Object>> policyByType();

    @Select("SELECT Status AS label, COUNT(*) AS value FROM hjb_policy GROUP BY Status")
    List<Map<String, Object>> policyByStatus();

    @Select("SELECT Cust_Type AS label, COUNT(*) AS value FROM hjb_customer GROUP BY Cust_Type")
    List<Map<String, Object>> customerByType();

    @Select("""
            SELECT method AS label, SUM(cnt) AS value
            FROM (
              SELECT Method AS method, COUNT(*) AS cnt FROM hjb_auto_payment GROUP BY Method
              UNION ALL
              SELECT Method AS method, COUNT(*) AS cnt FROM hjb_home_payment GROUP BY Method
            ) t
            GROUP BY label
            """)
    List<Map<String, Object>> paymentByMethod();

    @Select("""
            SELECT DATE_FORMAT(I_Date, '%Y-%m') AS month, ROUND(SUM(Amount), 2) AS revenue
            FROM (
              SELECT I_Date, Amount FROM hjb_auto_invoice
              UNION ALL
              SELECT I_Date, Amount FROM hjb_home_invoice
            ) t
            WHERE I_Date >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH)
            GROUP BY month
            ORDER BY month
            """)
    List<Map<String, Object>> monthlyRevenue();
}
