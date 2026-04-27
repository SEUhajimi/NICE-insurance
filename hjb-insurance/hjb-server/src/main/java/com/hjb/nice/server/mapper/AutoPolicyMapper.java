package com.hjb.nice.server.mapper;

import com.hjb.nice.entity.AutoPolicy;
import org.apache.ibatis.annotations.*;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface AutoPolicyMapper {

    @Select("SELECT ap.AP_ID, p.SDATE, p.EDATE, p.Amount, p.Status, p.HJB_CUSTOMER_CUST_ID " +
            "FROM hjb_autopolicy ap JOIN hjb_policy p ON ap.AP_ID = p.POLICY_ID")
    List<AutoPolicy> findAll();

    @Select("SELECT ap.AP_ID, p.SDATE, p.EDATE, p.Amount, p.Status, p.HJB_CUSTOMER_CUST_ID " +
            "FROM hjb_autopolicy ap JOIN hjb_policy p ON ap.AP_ID = p.POLICY_ID " +
            "WHERE ap.AP_ID = #{apId}")
    AutoPolicy findById(Integer apId);

    @Select("SELECT ap.AP_ID, p.SDATE, p.EDATE, p.Amount, p.Status, p.HJB_CUSTOMER_CUST_ID " +
            "FROM hjb_autopolicy ap JOIN hjb_policy p ON ap.AP_ID = p.POLICY_ID " +
            "WHERE p.HJB_CUSTOMER_CUST_ID = #{custId}")
    List<AutoPolicy> findByCustomerId(Integer custId);

    @Select("SELECT ap.AP_ID, p.SDATE, p.EDATE, p.Amount, p.Status, p.HJB_CUSTOMER_CUST_ID " +
            "FROM hjb_autopolicy ap JOIN hjb_policy p ON ap.AP_ID = p.POLICY_ID " +
            "WHERE ap.AP_ID = #{apId} AND p.HJB_CUSTOMER_CUST_ID = #{custId}")
    AutoPolicy findByIdAndCustomerId(@Param("apId") Integer apId, @Param("custId") Integer custId);

    @Insert("INSERT INTO hjb_policy(SDATE, EDATE, Amount, Status, Policy_Type, HJB_CUSTOMER_CUST_ID) " +
            "VALUES(#{sdate}, #{edate}, #{amount}, #{status}, 'A', #{hjbCustomerCustId})")
    @Options(useGeneratedKeys = true, keyProperty = "apId")
    void insertPolicy(AutoPolicy autoPolicy);

    @Insert("INSERT INTO hjb_autopolicy(AP_ID) VALUES(#{apId})")
    void insertSubtype(AutoPolicy autoPolicy);

    @Insert("INSERT INTO hjb_policy(POLICY_ID, SDATE, EDATE, Amount, Status, Policy_Type, HJB_CUSTOMER_CUST_ID) " +
            "VALUES(#{apId}, #{sdate}, #{edate}, #{amount}, #{status}, 'A', #{hjbCustomerCustId})")
    void insertPolicyWithId(AutoPolicy autoPolicy);

    @Update("UPDATE hjb_policy SET SDATE=#{sdate}, EDATE=#{edate}, Amount=#{amount}, " +
            "Status=#{status}, HJB_CUSTOMER_CUST_ID=#{hjbCustomerCustId} WHERE POLICY_ID=#{apId}")
    void update(AutoPolicy autoPolicy);

    @Delete("DELETE FROM hjb_autopolicy WHERE AP_ID = #{apId}")
    void deleteSubtype(@Param("apId") Integer apId);

    @Delete("DELETE FROM hjb_policy WHERE POLICY_ID = #{policyId}")
    void deletePolicy(@Param("policyId") Integer policyId);

    @Update("UPDATE hjb_policy SET Status='E' WHERE Policy_Type='A' AND EDATE < #{today} AND Status='C'")
    int updateExpired(@Param("today") LocalDate today);
}
