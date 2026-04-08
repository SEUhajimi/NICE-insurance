package com.hjb.nice.enums;

public enum CustomerType {
    AUTO("A"),
    HOME("H"),
    BOTH("B");

    private final String code;

    CustomerType(String code) { this.code = code; }

    public String getCode() { return code; }

    public static String calculate(boolean hasAuto, boolean hasHome) {
        if (hasAuto && hasHome) return BOTH.code;
        if (hasAuto) return AUTO.code;
        return HOME.code;
    }
}
