package com.fincorex.controller;

import com.fincorex.dto.request.TradeRequest;
import com.fincorex.dto.response.ApiResponse;
import com.fincorex.dto.response.TradeResponse;
import com.fincorex.service.TradeService;
import com.fincorex.service.WalletAuthorizationService;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/trades")
public class TradeController {

    private final TradeService tradeService;
    private final WalletAuthorizationService walletAuthorizationService;

    public TradeController(TradeService tradeService, WalletAuthorizationService walletAuthorizationService) {
        this.tradeService = tradeService;
        this.walletAuthorizationService = walletAuthorizationService;
    }

    @PostMapping
    public ApiResponse<TradeResponse> execute(
            @Valid @RequestBody TradeRequest request,
            @AuthenticationPrincipal UserDetails user) {

        walletAuthorizationService.assertWalletOwner(request.walletId(), user.getUsername());
        TradeResponse response = tradeService.executeTrade(request);

        return ApiResponse.success(response, "Trade executed");
    }
}
