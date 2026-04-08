package com.hjb.nice.enums;

public enum PolicyStatus {
    CURRENT("C"),
    TERMINATED("T");

    private final String code;

    PolicyStatus(String code) { this.code = code; }

    public String getCode() { return code; }
}
