package com.hjb.nice.server.mapper;

import com.hjb.nice.entity.Invoice;
import org.apache.ibatis.annotations.*;
import java.util.List;

// Read-only mapper: unions hjb_auto_invoice and hjb_home_invoice into the legacy Invoice DTO.
// Write operations are delegated to AutoInvoiceMapper / HomeInvoiceMapper in InvoiceServiceImpl.
@Mapper
public interface InvoiceMapper {

    @Select("SELECT I_ID, I_Date, Due, Amount, HJB_AUTOPOLICY_AP_ID, NULL AS HJB_HOMEPOLICY_HP_ID " +
            "FROM hjb_auto_invoice " +
            "UNION ALL " +
            "SELECT I_ID, I_Date, Due, Amount, NULL AS HJB_AUTOPOLICY_AP_ID, HJB_HOMEPOLICY_HP_ID " +
            "FROM hjb_home_invoice")
    List<Invoice> findAll();

    @Select("SELECT I_ID, I_Date, Due, Amount, HJB_AUTOPOLICY_AP_ID, NULL AS HJB_HOMEPOLICY_HP_ID " +
            "FROM hjb_auto_invoice WHERE I_ID = #{iId} " +
            "UNION ALL " +
            "SELECT I_ID, I_Date, Due, Amount, NULL AS HJB_AUTOPOLICY_AP_ID, HJB_HOMEPOLICY_HP_ID " +
            "FROM hjb_home_invoice WHERE I_ID = #{iId}")
    Invoice findById(Integer iId);

    @Select("SELECT I_ID, I_Date, Due, Amount, HJB_AUTOPOLICY_AP_ID, NULL AS HJB_HOMEPOLICY_HP_ID " +
            "FROM hjb_auto_invoice WHERE HJB_AUTOPOLICY_AP_ID = #{apId}")
    List<Invoice> findByAutoPolicyId(Integer apId);

    @Select("SELECT I_ID, I_Date, Due, Amount, NULL AS HJB_AUTOPOLICY_AP_ID, HJB_HOMEPOLICY_HP_ID " +
            "FROM hjb_home_invoice WHERE HJB_HOMEPOLICY_HP_ID = #{hpId}")
    List<Invoice> findByHomePolicyId(Integer hpId);
}
