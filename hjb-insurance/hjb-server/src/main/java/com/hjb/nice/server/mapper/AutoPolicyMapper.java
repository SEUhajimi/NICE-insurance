package com.hjb.nice.server.mapper;

import com.hjb.nice.entity.AutoPolicy;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface AutoPolicyMapper {

    @Select("SELECT * FROM hjb_autopolicy")
    List<AutoPolicy> findAll();

    @Select("SELECT * FROM hjb_autopolicy WHERE AP_ID = #{apId}")
    AutoPolicy findById(Integer apId);

    @Select("SELECT * FROM hjb_autopolicy WHERE HJB_CUSTOMER_CUST_ID = #{custId}")
    List<AutoPolicy> findByCustomerId(Integer custId);

    @Insert("INSERT INTO hjb_autopolicy(AP_ID, SDATE, EDATE, Amount, Status, HJB_CUSTOMER_CUST_ID) " +
            "VALUES(#{apId}, #{sdate}, #{edate}, #{amount}, #{status}, #{hjbCustomerCustId})")
    void insert(AutoPolicy autoPolicy);

    @Update("UPDATE hjb_autopolicy SET SDATE=#{sdate}, EDATE=#{edate}, Amount=#{amount}, " +
            "Status=#{status}, HJB_CUSTOMER_CUST_ID=#{hjbCustomerCustId} WHERE AP_ID=#{apId}")
    void update(AutoPolicy autoPolicy);

    @Delete("DELETE FROM hjb_autopolicy WHERE AP_ID = #{apId}")
    void deleteById(Integer apId);
}
