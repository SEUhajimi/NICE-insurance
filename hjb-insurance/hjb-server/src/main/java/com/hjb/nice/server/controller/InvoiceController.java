package com.hjb.nice.server.controller;

import com.hjb.nice.entity.Invoice;
import com.hjb.nice.result.Result;
import com.hjb.nice.server.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "管理端 - Invoice管理", description = "Invoice的增删改查（需 EMPLOYEE Token）")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @GetMapping
    public Result<List<Invoice>> findAll() {
        return Result.success(invoiceService.findAll());
    }

    @GetMapping("/{id}")
    public Result<Invoice> findById(@PathVariable("id") Integer id) {
        return Result.success(invoiceService.findById(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody Invoice invoice) {
        invoiceService.add(invoice);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Invoice invoice) {
        invoiceService.update(invoice);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteById(@PathVariable("id") Integer id) {
        invoiceService.deleteById(id);
        return Result.success();
    }
}
