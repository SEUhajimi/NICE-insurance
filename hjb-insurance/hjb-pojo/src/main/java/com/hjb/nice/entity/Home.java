package com.hjb.nice.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class Home {
    private Integer homeId;
    private LocalDate pdate;
    private BigDecimal pvalue;
    private Integer area;
    private String homeType;
    private Integer afn;
    private Integer hss;
    private String sp;
    private Integer basement;
    private Integer hjbHomepolicyHpId;
}
