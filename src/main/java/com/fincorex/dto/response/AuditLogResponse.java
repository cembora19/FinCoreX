package com.fincorex.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record AuditLogResponse(
        UUID id,
        UUID walletId,
        String action,
        String details,
        LocalDateTime createdAt) {
}
