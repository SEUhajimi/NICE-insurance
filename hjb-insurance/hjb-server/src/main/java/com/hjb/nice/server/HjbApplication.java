package com.hjb.nice.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.hjb.nice")
@MapperScan("com.hjb.nice.server.mapper")
@EnableScheduling
public class HjbApplication {
    public static void main(String[] args) {
        SpringApplication.run(HjbApplication.class, args);
    }
}
