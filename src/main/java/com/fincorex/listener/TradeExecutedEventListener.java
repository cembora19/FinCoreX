package com.fincorex.listener;

import com.fincorex.event.TradeExecutedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TradeExecutedEventListener {

    private static final Logger log = LoggerFactory.getLogger(TradeExecutedEventListener.class);

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTradeExecuted(TradeExecutedEvent event) {
        log.info("Trade executed: walletId={}, type={}, symbol={}, quantity={}, total={}",
                event.walletId(),
                event.type(),
                event.symbol(),
                event.quantity(),
                event.total());
    }
}
