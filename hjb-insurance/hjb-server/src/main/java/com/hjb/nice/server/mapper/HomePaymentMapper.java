package com.hjb.nice.server.mapper;

import com.hjb.nice.entity.HomePayment;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface HomePaymentMapper {

    @Select("SELECT * FROM hjb_home_payment")
    List<HomePayment> findAll();

    @Select("SELECT * FROM hjb_home_payment WHERE P_ID = #{pId}")
    HomePayment findById(Integer pId);

    @Insert("INSERT INTO hjb_home_payment(P_ID, Method, HJB_HOME_INVOICE_I_ID, Pay_Amount, Pay_Date) " +
            "VALUES(#{pId}, #{method}, #{hjbHomeInvoiceIId}, #{payAmount}, #{payDate})")
    void insert(HomePayment payment);

    @Update("UPDATE hjb_home_payment SET Method=#{method}, HJB_HOME_INVOICE_I_ID=#{hjbHomeInvoiceIId}, " +
            "Pay_Amount=#{payAmount}, Pay_Date=#{payDate} WHERE P_ID=#{pId}")
    void update(HomePayment payment);

    @Delete("DELETE FROM hjb_home_payment WHERE P_ID = #{pId}")
    void deleteById(Integer pId);

    @Select("SELECT * FROM hjb_home_payment WHERE HJB_HOME_INVOICE_I_ID = #{invoiceId}")
    List<HomePayment> findByInvoiceId(Integer invoiceId);

    @Delete("DELETE FROM hjb_home_payment WHERE HJB_HOME_INVOICE_I_ID = #{invoiceId}")
    void deleteByInvoiceId(@Param("invoiceId") Integer invoiceId);

    @Insert("INSERT INTO hjb_home_payment(Method, HJB_HOME_INVOICE_I_ID, Pay_Amount, Pay_Date) " +
            "VALUES(#{method}, #{hjbHomeInvoiceIId}, #{payAmount}, #{payDate})")
    @Options(useGeneratedKeys = true, keyProperty = "pId")
    void insertAutoId(HomePayment payment);
}
