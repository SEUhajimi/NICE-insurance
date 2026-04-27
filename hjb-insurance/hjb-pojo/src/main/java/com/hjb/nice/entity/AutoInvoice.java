package com.hjb.nice.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AutoInvoice {
    @JsonProperty("iId")
    private Integer iId;
    @JsonProperty("iDate")
    private LocalDate iDate;
    private LocalDate due;
    private BigDecimal amount;
    private Integer hjbAutopolicyApId;
}
