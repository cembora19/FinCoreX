package com.fincorex.controller;

import com.fincorex.dto.response.ApiResponse;
import com.fincorex.service.WalletService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/deposit")
    public ApiResponse<Void> deposit(
            @RequestParam UUID userId,
            @RequestParam BigDecimal amount) {
        walletService.deposit(userId, amount);
        return ApiResponse.success(null, "Deposit successful");
    }

    @PostMapping("/withdraw")
    public ApiResponse<Void> withdraw(
            @RequestParam UUID userId,
            @RequestParam BigDecimal amount) {
        walletService.withdraw(userId, amount);
        return ApiResponse.success(null, "Withdraw successful");
    }
}