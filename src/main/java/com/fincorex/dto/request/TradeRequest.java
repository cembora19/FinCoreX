package com.fincorex.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record TradeRequest(

        @NotNull UUID walletId,

        @NotNull String assetSymbol,

        @NotNull BigDecimal quantity,

        @NotNull TradeType type) {
}