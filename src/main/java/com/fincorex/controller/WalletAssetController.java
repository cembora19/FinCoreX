package com.fincorex.controller;

import com.fincorex.dto.response.ApiResponse;
import com.fincorex.entity.WalletAsset;
import com.fincorex.service.WalletAssetService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/portfolio")
public class WalletAssetController {

    private final WalletAssetService walletAssetService;

    public WalletAssetController(WalletAssetService walletAssetService) {
        this.walletAssetService = walletAssetService;
    }

    @GetMapping("/{walletId}")
    public ApiResponse<List<WalletAsset>> getPortfolio(@PathVariable UUID walletId) {
        return ApiResponse.success(walletAssetService.getPortfolio(walletId));
    }
}