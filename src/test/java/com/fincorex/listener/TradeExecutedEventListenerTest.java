package com.fincorex.listener;

import com.fincorex.dto.request.TradeType;
import com.fincorex.entity.AuditLog;
import com.fincorex.event.TradeExecutedEvent;
import com.fincorex.repository.AuditLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TradeExecutedEventListenerTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @Test
    void shouldPersistAuditLogForExecutedTrade() {
        UUID walletId = UUID.randomUUID();
        LocalDateTime executedAt = LocalDateTime.now();
        TradeExecutedEvent event = new TradeExecutedEvent(
                walletId,
                TradeType.BUY,
                "BTC",
                new BigDecimal("2.0000"),
                new BigDecimal("100.00"),
                new BigDecimal("200.00"),
                executedAt);

        new TradeExecutedEventListener(auditLogRepository).handleTradeExecuted(event);

        ArgumentCaptor<AuditLog> auditLogCaptor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(auditLogCaptor.capture());

        AuditLog auditLog = auditLogCaptor.getValue();
        assertEquals(walletId, auditLog.getWalletId());
        assertEquals("TRADE_EXECUTED", auditLog.getAction());
        assertEquals("BUY 2.0000 BTC @ 100.00", auditLog.getDetails());
        assertEquals(executedAt, auditLog.getCreatedAt());
    }
}
