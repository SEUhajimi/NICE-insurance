package com.hjb.nice.server.controller;

import com.hjb.nice.result.Result;
import com.hjb.nice.entity.Plan;
import com.hjb.nice.server.mapper.PlanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PlanController {

    @Autowired
    private PlanMapper planMapper;

    /** Public: active plans for landing page and customer portal */
    @GetMapping("/plans")
    public Result getActivePlans() {
        return Result.success(planMapper.findActive());
    }

    /** Admin: all plans (including inactive) */
    @GetMapping("/admin/plans")
    public Result getAllPlans() {
        return Result.success(planMapper.findAll());
    }

    @PostMapping("/admin/plans")
    public Result addPlan(@RequestBody Plan plan) {
        if (plan.getIsActive() == null) plan.setIsActive(true);
        planMapper.insert(plan);
        return Result.success(plan);
    }

    @PutMapping("/admin/plans")
    public Result updatePlan(@RequestBody Plan plan) {
        planMapper.update(plan);
        return Result.success();
    }

    @DeleteMapping("/admin/plans/{id}")
    public Result deletePlan(@PathVariable Integer id) {
        planMapper.deleteById(id);
        return Result.success();
    }
}
