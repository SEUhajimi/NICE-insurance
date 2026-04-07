package com.hjb.nice.server.mapper;

import com.hjb.nice.entity.HomePolicy;
import org.apache.ibatis.annotations.*;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface HomePolicyMapper {

    @Select("SELECT * FROM hjb_homepolicy")
    List<HomePolicy> findAll();

    @Select("SELECT * FROM hjb_homepolicy WHERE HP_ID = #{hpId}")
    HomePolicy findById(Integer hpId);

    @Select("SELECT * FROM hjb_homepolicy WHERE HJB_CUSTOMER_CUST_ID = #{custId}")
    List<HomePolicy> findByCustomerId(Integer custId);

    @Insert("INSERT INTO hjb_homepolicy(HP_ID, SDATE, EDATE, Amount, Status, HJB_CUSTOMER_CUST_ID) " +
            "VALUES(#{hpId}, #{sdate}, #{edate}, #{amount}, #{status}, #{hjbCustomerCustId})")
    void insert(HomePolicy homePolicy);

    @Update("UPDATE hjb_homepolicy SET SDATE=#{sdate}, EDATE=#{edate}, Amount=#{amount}, " +
            "Status=#{status}, HJB_CUSTOMER_CUST_ID=#{hjbCustomerCustId} WHERE HP_ID=#{hpId}")
    void update(HomePolicy homePolicy);

    @Delete("DELETE FROM hjb_homepolicy WHERE HP_ID = #{hpId}")
    void deleteById(Integer hpId);

    @Update("UPDATE hjb_homepolicy SET Status='T' WHERE EDATE < #{today} AND Status='C'")
    int updateExpired(@Param("today") LocalDate today);

    @Insert("INSERT INTO hjb_homepolicy(SDATE, EDATE, Amount, Status, HJB_CUSTOMER_CUST_ID) " +
            "VALUES(#{sdate}, #{edate}, #{amount}, #{status}, #{hjbCustomerCustId})")
    @Options(useGeneratedKeys = true, keyProperty = "hpId")
    void insertAutoId(HomePolicy homePolicy);
}
