package com.hjb.nice.server.mapper;

import com.hjb.nice.entity.AutoPayment;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface AutoPaymentMapper {

    @Select("SELECT * FROM hjb_auto_payment")
    List<AutoPayment> findAll();

    @Select("SELECT * FROM hjb_auto_payment WHERE P_ID = #{pId}")
    AutoPayment findById(Integer pId);

    @Insert("INSERT INTO hjb_auto_payment(P_ID, Method, HJB_AUTO_INVOICE_I_ID, Pay_Amount, Pay_Date) " +
            "VALUES(#{pId}, #{method}, #{hjbAutoInvoiceIId}, #{payAmount}, #{payDate})")
    void insert(AutoPayment payment);

    @Update("UPDATE hjb_auto_payment SET Method=#{method}, HJB_AUTO_INVOICE_I_ID=#{hjbAutoInvoiceIId}, " +
            "Pay_Amount=#{payAmount}, Pay_Date=#{payDate} WHERE P_ID=#{pId}")
    void update(AutoPayment payment);

    @Delete("DELETE FROM hjb_auto_payment WHERE P_ID = #{pId}")
    void deleteById(Integer pId);

    @Select("SELECT * FROM hjb_auto_payment WHERE HJB_AUTO_INVOICE_I_ID = #{invoiceId}")
    List<AutoPayment> findByInvoiceId(Integer invoiceId);

    @Insert("INSERT INTO hjb_auto_payment(Method, HJB_AUTO_INVOICE_I_ID, Pay_Amount, Pay_Date) " +
            "VALUES(#{method}, #{hjbAutoInvoiceIId}, #{payAmount}, #{payDate})")
    @Options(useGeneratedKeys = true, keyProperty = "pId")
    void insertAutoId(AutoPayment payment);
}
