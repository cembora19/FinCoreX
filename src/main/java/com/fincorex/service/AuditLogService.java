package com.fincorex.service;

import com.fincorex.dto.response.AuditLogResponse;

import java.util.List;
import java.util.UUID;

public interface AuditLogService {

    List<AuditLogResponse> getWalletAuditLogs(UUID walletId);
}
