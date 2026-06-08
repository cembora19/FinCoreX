package com.fincorex.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record PortfolioResponse(
        UUID walletId,
        BigDecimal balance,
        List<AssetPosition> assets) {

    public record AssetPosition(
            String symbol,
            BigDecimal quantity,
            BigDecimal currentPrice,
            BigDecimal value) {
    }
}