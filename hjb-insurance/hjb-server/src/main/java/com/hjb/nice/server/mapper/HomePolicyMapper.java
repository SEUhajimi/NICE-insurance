package com.hjb.nice.server.mapper;

import com.hjb.nice.entity.HomePolicy;
import org.apache.ibatis.annotations.*;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface HomePolicyMapper {

    @Select("SELECT hp.HP_ID, p.SDATE, p.EDATE, p.Amount, p.Status, p.HJB_CUSTOMER_CUST_ID " +
            "FROM hjb_homepolicy hp JOIN hjb_policy p ON hp.HP_ID = p.POLICY_ID")
    List<HomePolicy> findAll();

    @Select("SELECT hp.HP_ID, p.SDATE, p.EDATE, p.Amount, p.Status, p.HJB_CUSTOMER_CUST_ID " +
            "FROM hjb_homepolicy hp JOIN hjb_policy p ON hp.HP_ID = p.POLICY_ID " +
            "WHERE hp.HP_ID = #{hpId}")
    HomePolicy findById(Integer hpId);

    @Select("SELECT hp.HP_ID, p.SDATE, p.EDATE, p.Amount, p.Status, p.HJB_CUSTOMER_CUST_ID " +
            "FROM hjb_homepolicy hp JOIN hjb_policy p ON hp.HP_ID = p.POLICY_ID " +
            "WHERE p.HJB_CUSTOMER_CUST_ID = #{custId}")
    List<HomePolicy> findByCustomerId(Integer custId);

    @Select("SELECT hp.HP_ID, p.SDATE, p.EDATE, p.Amount, p.Status, p.HJB_CUSTOMER_CUST_ID " +
            "FROM hjb_homepolicy hp JOIN hjb_policy p ON hp.HP_ID = p.POLICY_ID " +
            "WHERE hp.HP_ID = #{hpId} AND p.HJB_CUSTOMER_CUST_ID = #{custId}")
    HomePolicy findByIdAndCustomerId(@Param("hpId") Integer hpId, @Param("custId") Integer custId);

    @Insert("INSERT INTO hjb_policy(SDATE, EDATE, Amount, Status, Policy_Type, HJB_CUSTOMER_CUST_ID) " +
            "VALUES(#{sdate}, #{edate}, #{amount}, #{status}, 'H', #{hjbCustomerCustId})")
    @Options(useGeneratedKeys = true, keyProperty = "hpId")
    void insertPolicy(HomePolicy homePolicy);

    @Insert("INSERT INTO hjb_homepolicy(HP_ID) VALUES(#{hpId})")
    void insertSubtype(HomePolicy homePolicy);

    @Insert("INSERT INTO hjb_policy(POLICY_ID, SDATE, EDATE, Amount, Status, Policy_Type, HJB_CUSTOMER_CUST_ID) " +
            "VALUES(#{hpId}, #{sdate}, #{edate}, #{amount}, #{status}, 'H', #{hjbCustomerCustId})")
    void insertPolicyWithId(HomePolicy homePolicy);

    @Update("UPDATE hjb_policy SET SDATE=#{sdate}, EDATE=#{edate}, Amount=#{amount}, " +
            "Status=#{status}, HJB_CUSTOMER_CUST_ID=#{hjbCustomerCustId} WHERE POLICY_ID=#{hpId}")
    void update(HomePolicy homePolicy);

    @Delete("DELETE FROM hjb_homepolicy WHERE HP_ID = #{hpId}")
    void deleteSubtype(@Param("hpId") Integer hpId);

    @Delete("DELETE FROM hjb_policy WHERE POLICY_ID = #{policyId}")
    void deletePolicy(@Param("policyId") Integer policyId);

    @Update("UPDATE hjb_policy SET Status='E' WHERE Policy_Type='H' AND EDATE < #{today} AND Status='C'")
    int updateExpired(@Param("today") LocalDate today);
}
