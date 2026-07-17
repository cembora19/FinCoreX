package com.fincorex.service.impl;

import com.fincorex.dto.response.AuditLogResponse;
import com.fincorex.exception.ResourceNotFoundException;
import com.fincorex.repository.AuditLogRepository;
import com.fincorex.repository.WalletRepository;
import com.fincorex.service.AuditLogService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final WalletRepository walletRepository;

    public AuditLogServiceImpl(
            AuditLogRepository auditLogRepository,
            WalletRepository walletRepository) {
        this.auditLogRepository = auditLogRepository;
        this.walletRepository = walletRepository;
    }

    @Override
    public List<AuditLogResponse> getWalletAuditLogs(UUID walletId) {
        walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet", walletId));

        return auditLogRepository.findByWalletIdOrderByCreatedAtDesc(walletId)
                .stream()
                .map(auditLog -> new AuditLogResponse(
                        auditLog.getId(),
                        auditLog.getWalletId(),
                        auditLog.getAction(),
                        auditLog.getDetails(),
                        auditLog.getCreatedAt()))
                .toList();
    }
}
