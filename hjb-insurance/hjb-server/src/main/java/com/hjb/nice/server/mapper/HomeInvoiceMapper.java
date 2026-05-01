package com.hjb.nice.server.mapper;

import com.hjb.nice.entity.HomeInvoice;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface HomeInvoiceMapper {

    @Select("SELECT * FROM hjb_home_invoice")
    List<HomeInvoice> findAll();

    @Select("SELECT * FROM hjb_home_invoice WHERE I_ID = #{iId}")
    HomeInvoice findById(Integer iId);

    @Insert("INSERT INTO hjb_home_invoice(I_ID, I_Date, Due, Amount, HJB_HOMEPOLICY_HP_ID) " +
            "VALUES(#{iId}, #{iDate}, #{due}, #{amount}, #{hjbHomepolicyHpId})")
    void insert(HomeInvoice invoice);

    @Update("UPDATE hjb_home_invoice SET I_Date=#{iDate}, Due=#{due}, Amount=#{amount}, " +
            "HJB_HOMEPOLICY_HP_ID=#{hjbHomepolicyHpId} WHERE I_ID=#{iId}")
    void update(HomeInvoice invoice);

    @Delete("DELETE FROM hjb_home_invoice WHERE I_ID = #{iId}")
    void deleteById(Integer iId);

    @Select("SELECT * FROM hjb_home_invoice WHERE HJB_HOMEPOLICY_HP_ID = #{hpId}")
    List<HomeInvoice> findByHomePolicyId(Integer hpId);

    @Delete("DELETE FROM hjb_home_invoice WHERE HJB_HOMEPOLICY_HP_ID = #{hpId}")
    void deleteByHomePolicyId(@Param("hpId") Integer hpId);

    @Insert("INSERT INTO hjb_home_invoice(I_Date, Due, Amount, HJB_HOMEPOLICY_HP_ID) " +
            "VALUES(#{iDate}, #{due}, #{amount}, #{hjbHomepolicyHpId})")
    @Options(useGeneratedKeys = true, keyProperty = "iId")
    void insertAutoId(HomeInvoice invoice);
}
