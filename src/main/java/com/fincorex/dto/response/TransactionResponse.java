package com.fincorex.dto.response;

import com.fincorex.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        TransactionType type,
        BigDecimal amount,
        String description,
        LocalDateTime createdAt) {
}
