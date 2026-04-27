package com.hjb.nice.server.mapper;

import com.hjb.nice.entity.AutoInvoice;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface AutoInvoiceMapper {

    @Select("SELECT * FROM hjb_auto_invoice")
    List<AutoInvoice> findAll();

    @Select("SELECT * FROM hjb_auto_invoice WHERE I_ID = #{iId}")
    AutoInvoice findById(Integer iId);

    @Insert("INSERT INTO hjb_auto_invoice(I_ID, I_Date, Due, Amount, HJB_AUTOPOLICY_AP_ID) " +
            "VALUES(#{iId}, #{iDate}, #{due}, #{amount}, #{hjbAutopolicyApId})")
    void insert(AutoInvoice invoice);

    @Update("UPDATE hjb_auto_invoice SET I_Date=#{iDate}, Due=#{due}, Amount=#{amount}, " +
            "HJB_AUTOPOLICY_AP_ID=#{hjbAutopolicyApId} WHERE I_ID=#{iId}")
    void update(AutoInvoice invoice);

    @Delete("DELETE FROM hjb_auto_invoice WHERE I_ID = #{iId}")
    void deleteById(Integer iId);

    @Select("SELECT * FROM hjb_auto_invoice WHERE HJB_AUTOPOLICY_AP_ID = #{apId}")
    List<AutoInvoice> findByAutoPolicyId(Integer apId);

    @Insert("INSERT INTO hjb_auto_invoice(I_Date, Due, Amount, HJB_AUTOPOLICY_AP_ID) " +
            "VALUES(#{iDate}, #{due}, #{amount}, #{hjbAutopolicyApId})")
    @Options(useGeneratedKeys = true, keyProperty = "iId")
    void insertAutoId(AutoInvoice invoice);
}
