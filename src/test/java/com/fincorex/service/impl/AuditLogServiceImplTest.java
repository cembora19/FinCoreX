package com.fincorex.service.impl;

import com.fincorex.entity.AuditLog;
import com.fincorex.entity.Wallet;
import com.fincorex.repository.AuditLogRepository;
import com.fincorex.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceImplTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private WalletRepository walletRepository;

    @Test
    void shouldReturnWalletAuditLogsAsResponseDtos() {
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet();
        wallet.setId(walletId);

        AuditLog auditLog = new AuditLog();
        auditLog.setId(UUID.randomUUID());
        auditLog.setWalletId(walletId);
        auditLog.setAction("TRADE_EXECUTED");
        auditLog.setDetails("BUY 1 BTC @ 100");
        auditLog.setCreatedAt(LocalDateTime.now());

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(auditLogRepository.findByWalletIdOrderByCreatedAtDesc(walletId))
                .thenReturn(List.of(auditLog));

        var response = new AuditLogServiceImpl(auditLogRepository, walletRepository)
                .getWalletAuditLogs(walletId);

        assertEquals(1, response.size());
        assertEquals(walletId, response.getFirst().walletId());
        assertEquals("TRADE_EXECUTED", response.getFirst().action());
        assertEquals("BUY 1 BTC @ 100", response.getFirst().details());
    }
}
