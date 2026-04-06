package com.hjb.nice.entity;

import lombok.Data;
import java.time.LocalDate;

@Data
public class Driver {
    private String driverLicense;
    private String fname;
    private String lname;
    private LocalDate birthday;
}
