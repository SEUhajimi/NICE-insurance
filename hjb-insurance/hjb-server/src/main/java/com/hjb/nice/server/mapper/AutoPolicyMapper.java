package com.hjb.nice.server.mapper;

import com.hjb.nice.entity.AutoPolicy;
import org.apache.ibatis.annotations.*;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface AutoPolicyMapper {

    @Select("SELECT AP_ID, SDATE, EDATE, Amount, Status, HJB_CUSTOMER_CUST_ID FROM hjb_autopolicy")
    List<AutoPolicy> findAll();

    @Select("SELECT AP_ID, SDATE, EDATE, Amount, Status, HJB_CUSTOMER_CUST_ID FROM hjb_autopolicy WHERE AP_ID = #{apId}")
    AutoPolicy findById(Integer apId);

    @Select("SELECT AP_ID, SDATE, EDATE, Amount, Status, HJB_CUSTOMER_CUST_ID FROM hjb_autopolicy WHERE HJB_CUSTOMER_CUST_ID = #{custId}")
    List<AutoPolicy> findByCustomerId(Integer custId);

    @Select("SELECT AP_ID, SDATE, EDATE, Amount, Status, HJB_CUSTOMER_CUST_ID FROM hjb_autopolicy WHERE AP_ID = #{apId} AND HJB_CUSTOMER_CUST_ID = #{custId}")
    AutoPolicy findByIdAndCustomerId(@Param("apId") Integer apId, @Param("custId") Integer custId);

    @Insert("INSERT INTO hjb_autopolicy(SDATE, EDATE, Amount, Status, HJB_CUSTOMER_CUST_ID) VALUES(#{sdate}, #{edate}, #{amount}, #{status}, #{hjbCustomerCustId})")
    @Options(useGeneratedKeys = true, keyProperty = "apId")
    void insert(AutoPolicy autoPolicy);

    @Insert("INSERT INTO hjb_autopolicy(AP_ID, SDATE, EDATE, Amount, Status, HJB_CUSTOMER_CUST_ID) VALUES(#{apId}, #{sdate}, #{edate}, #{amount}, #{status}, #{hjbCustomerCustId})")
    void insertWithId(AutoPolicy autoPolicy);

    @Update("UPDATE hjb_autopolicy SET SDATE=#{sdate}, EDATE=#{edate}, Amount=#{amount}, Status=#{status}, HJB_CUSTOMER_CUST_ID=#{hjbCustomerCustId} WHERE AP_ID=#{apId}")
    void update(AutoPolicy autoPolicy);

    @Delete("DELETE FROM hjb_autopolicy WHERE AP_ID = #{apId}")
    void deleteById(@Param("apId") Integer apId);

    @Update("UPDATE hjb_autopolicy SET Status='E' WHERE EDATE < #{today} AND Status='C'")
    int updateExpired(@Param("today") LocalDate today);
}
