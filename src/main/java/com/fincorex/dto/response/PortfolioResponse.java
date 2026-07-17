package com.fincorex.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record PortfolioResponse(
        UUID walletId,
        BigDecimal balance,
        BigDecimal assetValue,
        BigDecimal totalValue,
        BigDecimal totalCost,
        BigDecimal unrealizedProfitLoss,
        BigDecimal realizedProfitLoss,
        BigDecimal profitLoss,
        BigDecimal profitLossPercentage,
        List<AssetPosition> assets) {

    public record AssetPosition(
            String symbol,
            BigDecimal quantity,
            BigDecimal currentPrice,
            BigDecimal value,
            BigDecimal averageBuyPrice,
            BigDecimal cost,
            BigDecimal profitLoss,
            BigDecimal profitLossPercentage) {
    }
}
