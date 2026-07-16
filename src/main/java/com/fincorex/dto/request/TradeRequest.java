package com.fincorex.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record TradeRequest(

        @NotNull UUID walletId,

        @NotBlank String assetSymbol,

        @NotNull @Positive BigDecimal quantity,

        @NotNull TradeType type) {
}
