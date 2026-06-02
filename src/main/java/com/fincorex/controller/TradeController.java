package com.fincorex.controller;

import com.fincorex.dto.request.TradeRequest;
import com.fincorex.dto.response.ApiResponse;
import com.fincorex.service.TradeService;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/trades")
public class TradeController {

    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @PostMapping
    public ApiResponse<Void> execute(@Valid @RequestBody TradeRequest request) {

        tradeService.executeTrade(request);

        return ApiResponse.success(null, "Trade executed");
    }
}