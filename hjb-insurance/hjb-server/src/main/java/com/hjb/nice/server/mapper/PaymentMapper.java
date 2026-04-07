package com.hjb.nice.server.mapper;

import com.hjb.nice.entity.Payment;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface PaymentMapper {

    @Select("SELECT * FROM hjb_payment")
    List<Payment> findAll();

    @Select("SELECT * FROM hjb_payment WHERE P_ID = #{pId}")
    Payment findById(Integer pId);

    @Insert("INSERT INTO hjb_payment(P_ID, Method, HJB_INVOICE_I_ID, Pay_Amount, Pay_Date) " +
            "VALUES(#{pId}, #{method}, #{hjbInvoiceIId}, #{payAmount}, #{payDate})")
    void insert(Payment payment);

    @Update("UPDATE hjb_payment SET Method=#{method}, HJB_INVOICE_I_ID=#{hjbInvoiceIId}, " +
            "Pay_Amount=#{payAmount}, Pay_Date=#{payDate} WHERE P_ID=#{pId}")
    void update(Payment payment);

    @Delete("DELETE FROM hjb_payment WHERE P_ID = #{pId}")
    void deleteById(Integer pId);

    @Select("SELECT * FROM hjb_payment WHERE HJB_INVOICE_I_ID = #{invoiceId}")
    List<Payment> findByInvoiceId(Integer invoiceId);

    // 客户支付时使用，P_ID 由数据库 AUTO_INCREMENT 生成
    @Insert("INSERT INTO hjb_payment(Method, HJB_INVOICE_I_ID, Pay_Amount, Pay_Date) " +
            "VALUES(#{method}, #{hjbInvoiceIId}, #{payAmount}, #{payDate})")
    @Options(useGeneratedKeys = true, keyProperty = "pId")
    void insertAutoId(Payment payment);
}
