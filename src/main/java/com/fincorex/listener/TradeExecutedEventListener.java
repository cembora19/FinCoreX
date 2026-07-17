package com.fincorex.listener;

import com.fincorex.entity.AuditLog;
import com.fincorex.event.TradeExecutedEvent;
import com.fincorex.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TradeExecutedEventListener {

    private static final Logger log = LoggerFactory.getLogger(TradeExecutedEventListener.class);

    private final AuditLogRepository auditLogRepository;

    public TradeExecutedEventListener(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @EventListener
    @Transactional
    public void handleTradeExecuted(TradeExecutedEvent event) {
        log.info("Trade executed: walletId={}, type={}, symbol={}, quantity={}, total={}",
                event.walletId(),
                event.type(),
                event.symbol(),
                event.quantity(),
                event.total());

        AuditLog auditLog = new AuditLog();
        auditLog.setWalletId(event.walletId());
        auditLog.setAction("TRADE_EXECUTED");
        auditLog.setDetails(event.type() + " " + event.quantity()
                + " " + event.symbol() + " @ " + event.price());
        auditLog.setCreatedAt(event.executedAt());

        auditLogRepository.save(auditLog);
    }
}
