package com.fincorex.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record TradeResponse(
        UUID walletId,
        String type,
        String symbol,
        BigDecimal quantity,
        BigDecimal price,
        BigDecimal total,
        BigDecimal newBalance) {
}