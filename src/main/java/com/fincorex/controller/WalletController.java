package com.fincorex.controller;

import com.fincorex.dto.request.DepositRequest;
import com.fincorex.dto.request.WithdrawRequest;
import com.fincorex.dto.response.ApiResponse;
import com.fincorex.dto.response.PortfolioResponse;
import com.fincorex.service.WalletService;

import jakarta.validation.Valid;

import java.util.UUID;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/deposit")
    public ApiResponse<Void> deposit(
            @Valid @RequestBody DepositRequest request) {

        walletService.deposit(request);

        return ApiResponse.success(
                null,
                "Deposit successful");
    }

    @PostMapping("/withdraw")
    public ApiResponse<Void> withdraw(
            @Valid @RequestBody WithdrawRequest request) {

        walletService.withdraw(request);

        return ApiResponse.success(
                null,
                "Withdraw successful");
    }

    @GetMapping("/{walletId}/portfolio")
    public ApiResponse<PortfolioResponse> getPortfolio(
            @PathVariable UUID walletId) {
        return ApiResponse.success(walletService.getPortfolio(walletId));
    }
}