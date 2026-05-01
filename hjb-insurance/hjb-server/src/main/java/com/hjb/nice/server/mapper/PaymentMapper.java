package com.hjb.nice.server.mapper;

import com.hjb.nice.entity.Payment;
import org.apache.ibatis.annotations.*;
import java.util.List;

// Read-only mapper: unions hjb_auto_payment and hjb_home_payment into the legacy Payment DTO.
// The HJB_INVOICE_I_ID column maps to the respective invoice ID from each table.
// Write operations are delegated to AutoPaymentMapper / HomePaymentMapper in PaymentServiceImpl.
@Mapper
public interface PaymentMapper {

    @Select("SELECT P_ID, Method, HJB_AUTO_INVOICE_I_ID AS HJB_INVOICE_I_ID, Pay_Amount, Pay_Date " +
            "FROM hjb_auto_payment " +
            "UNION ALL " +
            "SELECT P_ID, Method, HJB_HOME_INVOICE_I_ID AS HJB_INVOICE_I_ID, Pay_Amount, Pay_Date " +
            "FROM hjb_home_payment")
    List<Payment> findAll();

    @Select("SELECT P_ID, Method, HJB_AUTO_INVOICE_I_ID AS HJB_INVOICE_I_ID, Pay_Amount, Pay_Date " +
            "FROM hjb_auto_payment WHERE P_ID = #{pId} " +
            "UNION ALL " +
            "SELECT P_ID, Method, HJB_HOME_INVOICE_I_ID AS HJB_INVOICE_I_ID, Pay_Amount, Pay_Date " +
            "FROM hjb_home_payment WHERE P_ID = #{pId}")
    Payment findById(Integer pId);

    @Select("SELECT P_ID, Method, HJB_AUTO_INVOICE_I_ID AS HJB_INVOICE_I_ID, Pay_Amount, Pay_Date " +
            "FROM hjb_auto_payment WHERE HJB_AUTO_INVOICE_I_ID = #{invoiceId} " +
            "UNION ALL " +
            "SELECT P_ID, Method, HJB_HOME_INVOICE_I_ID AS HJB_INVOICE_I_ID, Pay_Amount, Pay_Date " +
            "FROM hjb_home_payment WHERE HJB_HOME_INVOICE_I_ID = #{invoiceId}")
    List<Payment> findByInvoiceId(Integer invoiceId);

    @Select("SELECT P_ID, Method, HJB_AUTO_INVOICE_I_ID AS HJB_INVOICE_I_ID, Pay_Amount, Pay_Date " +
            "FROM hjb_auto_payment WHERE Method = #{method} " +
            "UNION ALL " +
            "SELECT P_ID, Method, HJB_HOME_INVOICE_I_ID AS HJB_INVOICE_I_ID, Pay_Amount, Pay_Date " +
            "FROM hjb_home_payment WHERE Method = #{method}")
    List<Payment> searchByMethod(@Param("method") String method);
}
