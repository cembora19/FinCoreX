package com.fincorex.event;

import com.fincorex.dto.request.TradeType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TradeExecutedEvent(
        UUID walletId,
        TradeType type,
        String symbol,
        BigDecimal quantity,
        BigDecimal price,
        BigDecimal total,
        LocalDateTime executedAt) {
}
