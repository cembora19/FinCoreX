package com.fincorex.controller;

import com.fincorex.dto.response.ApiResponse;
import com.fincorex.dto.response.AssetResponse;
import com.fincorex.service.AssetService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @GetMapping
    public ApiResponse<List<AssetResponse>> getAllAssets() {
        return ApiResponse.success(assetService.getAllAssets());
    }
}
