package com.hjb.nice.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class Plan {
    private Integer planId;
    private String planName;
    private String planType;   // AUTO | HOME
    private BigDecimal amount;
    private String features;   // comma-separated list
    private Boolean isActive;
}
