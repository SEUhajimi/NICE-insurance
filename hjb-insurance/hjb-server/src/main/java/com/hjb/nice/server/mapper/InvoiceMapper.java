package com.hjb.nice.server.mapper;

import com.hjb.nice.entity.Invoice;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface InvoiceMapper {

    @Select("SELECT * FROM hjb_invoice")
    List<Invoice> findAll();

    @Select("SELECT * FROM hjb_invoice WHERE I_ID = #{iId}")
    Invoice findById(Integer iId);

    @Insert("INSERT INTO hjb_invoice(I_ID, I_Date, Due, Amount, HJB_HOMEPOLICY_HP_ID, HJB_AUTOPOLICY_AP_ID) " +
            "VALUES(#{iId}, #{iDate}, #{due}, #{amount}, #{hjbHomepolicyHpId}, #{hjbAutopolicyApId})")
    void insert(Invoice invoice);

    @Update("UPDATE hjb_invoice SET I_Date=#{iDate}, Due=#{due}, Amount=#{amount}, " +
            "HJB_HOMEPOLICY_HP_ID=#{hjbHomepolicyHpId}, HJB_AUTOPOLICY_AP_ID=#{hjbAutopolicyApId} WHERE I_ID=#{iId}")
    void update(Invoice invoice);

    @Delete("DELETE FROM hjb_invoice WHERE I_ID = #{iId}")
    void deleteById(Integer iId);

    @Select("SELECT * FROM hjb_invoice WHERE HJB_AUTOPOLICY_AP_ID = #{apId}")
    List<Invoice> findByAutoPolicyId(Integer apId);

    @Select("SELECT * FROM hjb_invoice WHERE HJB_HOMEPOLICY_HP_ID = #{hpId}")
    List<Invoice> findByHomePolicyId(Integer hpId);

    @Insert("INSERT INTO hjb_invoice(I_Date, Due, Amount, HJB_HOMEPOLICY_HP_ID, HJB_AUTOPOLICY_AP_ID) " +
            "VALUES(#{iDate}, #{due}, #{amount}, #{hjbHomepolicyHpId}, #{hjbAutopolicyApId})")
    @Options(useGeneratedKeys = true, keyProperty = "iId")
    void insertAutoId(Invoice invoice);
}
