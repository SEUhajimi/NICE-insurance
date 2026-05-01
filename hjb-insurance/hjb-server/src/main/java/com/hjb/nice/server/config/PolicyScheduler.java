package com.hjb.nice.server.config;

import com.hjb.nice.server.mapper.AutoPolicyMapper;
import com.hjb.nice.server.mapper.HomePolicyMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Runs daily at 1:00 AM to check for expired policies and update their Status from 'C' to 'E'.
 */
@Component
public class PolicyScheduler {

    private static final Logger log = LoggerFactory.getLogger(PolicyScheduler.class);

    @Autowired private AutoPolicyMapper autoPolicyMapper;
    @Autowired private HomePolicyMapper homePolicyMapper;

    @Scheduled(cron = "0 0 1 * * *")
    public void expirePolicies() {
        LocalDate today = LocalDate.now();
        int auto = autoPolicyMapper.updateExpired(today);
        int home = homePolicyMapper.updateExpired(today);
        if (auto + home > 0) {
            log.info("Expired policy update complete: {} auto policies, {} home policies updated.", auto, home);
        }
    }
}
