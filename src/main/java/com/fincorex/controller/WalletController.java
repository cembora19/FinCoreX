package com.fincorex.controller;

import com.fincorex.dto.request.DepositRequest;
import com.fincorex.dto.request.WithdrawRequest;
import com.fincorex.dto.response.ApiResponse;
import com.fincorex.dto.response.PortfolioResponse;
import com.fincorex.dto.response.TransactionResponse;
import com.fincorex.dto.response.AuditLogResponse;
import com.fincorex.service.AuditLogService;
import com.fincorex.service.WalletAuthorizationService;
import com.fincorex.service.WalletService;

import jakarta.validation.Valid;

import java.util.UUID;
import java.util.List;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletService walletService;
    private final AuditLogService auditLogService;
    private final WalletAuthorizationService walletAuthorizationService;

    public WalletController(
            WalletService walletService,
            AuditLogService auditLogService,
            WalletAuthorizationService walletAuthorizationService) {
        this.walletService = walletService;
        this.auditLogService = auditLogService;
        this.walletAuthorizationService = walletAuthorizationService;
    }

    @PostMapping("/deposit")
    public ApiResponse<Void> deposit(
            @Valid @RequestBody DepositRequest request,
            @AuthenticationPrincipal UserDetails user) {

        walletAuthorizationService.assertUserWalletOwner(request.userId(), user.getUsername());
        walletService.deposit(request);

        return ApiResponse.success(
                null,
                "Deposit successful");
    }

    @PostMapping("/withdraw")
    public ApiResponse<Void> withdraw(
            @Valid @RequestBody WithdrawRequest request,
            @AuthenticationPrincipal UserDetails user) {

        walletAuthorizationService.assertUserWalletOwner(request.userId(), user.getUsername());
        walletService.withdraw(request);

        return ApiResponse.success(
                null,
                "Withdraw successful");
    }

    @GetMapping("/{walletId}/portfolio")
    public ApiResponse<PortfolioResponse> getPortfolio(
            @PathVariable UUID walletId,
            @AuthenticationPrincipal UserDetails user) {
        walletAuthorizationService.assertWalletOwner(walletId, user.getUsername());
        return ApiResponse.success(walletService.getPortfolio(walletId));
    }

    @GetMapping("/{walletId}/transactions")
    public ApiResponse<List<TransactionResponse>> getTransactionHistory(
            @PathVariable UUID walletId,
            @AuthenticationPrincipal UserDetails user) {
        walletAuthorizationService.assertWalletOwner(walletId, user.getUsername());
        return ApiResponse.success(walletService.getTransactionHistory(walletId));
    }

    @GetMapping("/{walletId}/audit-logs")
    public ApiResponse<List<AuditLogResponse>> getAuditLogs(
            @PathVariable UUID walletId,
            @AuthenticationPrincipal UserDetails user) {
        walletAuthorizationService.assertWalletOwner(walletId, user.getUsername());
        return ApiResponse.success(auditLogService.getWalletAuditLogs(walletId));
    }
}
