package com.fincorex.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record AssetResponse(
        UUID id,
        String symbol,
        String name,
        BigDecimal price) {
}
