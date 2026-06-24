package com.clubmanage.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clubmanage.common.Result;
import com.clubmanage.dto.fund.ApproveFundRequest;
import com.clubmanage.dto.fund.CreateFundRequest;
import com.clubmanage.entity.Fund;
import com.clubmanage.service.FundService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/funds")
@RequiredArgsConstructor
public class FundController {

    private final FundService fundService;

    @GetMapping
    public Result<Page<Fund>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long clubId,
            @RequestParam(required = false) Integer status) {
        return Result.ok(fundService.listFunds(page, size, clubId, status));
    }

    @PostMapping
    public Result<Fund> create(@Valid @RequestBody CreateFundRequest request) {
        return Result.ok(fundService.createFund(request));
    }

    @PutMapping("/{id}/approve")
    public Result<Fund> approve(@PathVariable Long id, @Valid @RequestBody ApproveFundRequest request) {
        return Result.ok(fundService.approveFund(id, request));
    }
}