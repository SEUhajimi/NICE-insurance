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
 * 每天凌晨 1 点检查过期保单，将 Status 从 'C' 更新为 'T'
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
            log.info("过期保单处理完成：车险 {} 张，房险 {} 张", auto, home);
        }
    }
}
