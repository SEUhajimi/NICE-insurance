package com.hjb.nice.server.controller.admin;

import com.hjb.nice.result.Result;
import com.hjb.nice.server.mapper.StatsMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Admin - Statistics", description = "Dashboard chart data (requires EMPLOYEE Token)")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/admin/stats")
public class StatsController {

    @Autowired
    private StatsMapper statsMapper;

    @Operation(summary = "Get dashboard statistics")
    @GetMapping
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> data = new HashMap<>();
        data.put("policyByType",    statsMapper.policyByType());
        data.put("policyByStatus",  statsMapper.policyByStatus());
        data.put("customerByType",  statsMapper.customerByType());
        data.put("paymentByMethod", statsMapper.paymentByMethod());
        data.put("monthlyRevenue",  statsMapper.monthlyRevenue());
        return Result.success(data);
    }
}
