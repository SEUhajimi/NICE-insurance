package com.hjb.nice.server.controller;

import com.hjb.nice.entity.Payment;
import com.hjb.nice.result.Result;
import com.hjb.nice.server.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "管理端 - Payment管理", description = "Payment的增删改查（需 EMPLOYEE Token）")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping
    public Result<List<Payment>> findAll() {
        return Result.success(paymentService.findAll());
    }

    @GetMapping("/{id}")
    public Result<Payment> findById(@PathVariable("id") Integer id) {
        return Result.success(paymentService.findById(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody Payment payment) {
        paymentService.add(payment);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Payment payment) {
        paymentService.update(payment);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteById(@PathVariable("id") Integer id) {
        paymentService.deleteById(id);
        return Result.success();
    }
}
