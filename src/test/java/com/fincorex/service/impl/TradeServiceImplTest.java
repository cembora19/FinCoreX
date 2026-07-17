package com.fincorex.service.impl;

import com.fincorex.dto.request.TradeRequest;
import com.fincorex.dto.request.TradeType;
import com.fincorex.entity.Asset;
import com.fincorex.entity.Transaction;
import com.fincorex.entity.TransactionType;
import com.fincorex.entity.Wallet;
import com.fincorex.entity.WalletAsset;
import com.fincorex.exception.InsufficientAssetException;
import com.fincorex.exception.InsufficientBalanceException;
import com.fincorex.repository.AssetRepository;
import com.fincorex.repository.TransactionRepository;
import com.fincorex.repository.WalletAssetRepository;
import com.fincorex.repository.WalletRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class TradeServiceImplTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private WalletAssetRepository walletAssetRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private TradeServiceImpl tradeService;
    private UUID walletId;
    private Wallet wallet;
    private Asset asset;

    @BeforeEach
    void setUp() {
        tradeService = new TradeServiceImpl(
                walletRepository,
                assetRepository,
                walletAssetRepository,
                transactionRepository,
                eventPublisher);

        walletId = UUID.randomUUID();
        wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setBalance(new BigDecimal("100.00"));

        asset = new Asset();
        asset.setSymbol("BTC");
        asset.setPrice(new BigDecimal("10.00"));

        when(walletRepository.findByIdForUpdate(walletId)).thenReturn(Optional.of(wallet));
        when(assetRepository.findBySymbolForUpdate("BTC")).thenReturn(Optional.of(asset));
    }

    @Test
    void shouldExecuteBuyAndUpdateAverageBuyPrice() {
        when(walletAssetRepository.findByWalletIdAndAssetSymbolForUpdate(walletId, "BTC"))
                .thenReturn(Optional.empty());

        tradeService.executeTrade(new TradeRequest(
                walletId, "BTC", new BigDecimal("2.0000"), TradeType.BUY));

        assertEquals(0, wallet.getBalance().compareTo(new BigDecimal("80.00")));

        ArgumentCaptor<WalletAsset> walletAssetCaptor = ArgumentCaptor.forClass(WalletAsset.class);
        verify(walletAssetRepository).save(walletAssetCaptor.capture());
        assertEquals(0, walletAssetCaptor.getValue().getQuantity()
                .compareTo(new BigDecimal("2.0000")));
        assertEquals(0, walletAssetCaptor.getValue().getAverageBuyPrice()
                .compareTo(new BigDecimal("10.00")));

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionCaptor.capture());
        assertEquals(TransactionType.BUY, transactionCaptor.getValue().getType());
        verify(eventPublisher).publishEvent(any(com.fincorex.event.TradeExecutedEvent.class));
    }

    @Test
    void shouldExecuteSellAndIncreaseWalletBalance() {
        WalletAsset walletAsset = new WalletAsset();
        walletAsset.setWallet(wallet);
        walletAsset.setAsset(asset);
        walletAsset.setQuantity(new BigDecimal("2.0000"));
        walletAsset.setAverageBuyPrice(new BigDecimal("8.0000"));

        when(walletAssetRepository.findByWalletIdAndAssetSymbolForUpdate(walletId, "BTC"))
                .thenReturn(Optional.of(walletAsset));

        wallet.setBalance(BigDecimal.ZERO);
        tradeService.executeTrade(new TradeRequest(
                walletId, "BTC", new BigDecimal("1.0000"), TradeType.SELL));

        assertEquals(0, wallet.getBalance().compareTo(new BigDecimal("10.00")));
        assertEquals(0, wallet.getRealizedProfitLoss().compareTo(new BigDecimal("2.00")));
        assertEquals(0, walletAsset.getQuantity().compareTo(new BigDecimal("1.0000")));
        assertEquals(0, walletAsset.getAverageBuyPrice().compareTo(new BigDecimal("8.0000")));

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionCaptor.capture());
        assertEquals(TransactionType.SELL, transactionCaptor.getValue().getType());
        verify(eventPublisher).publishEvent(any(com.fincorex.event.TradeExecutedEvent.class));
    }

    @Test
    void shouldRejectBuyWhenWalletBalanceIsInsufficient() {
        when(walletAssetRepository.findByWalletIdAndAssetSymbolForUpdate(walletId, "BTC"))
                .thenReturn(Optional.empty());

        assertThrows(InsufficientBalanceException.class, () -> tradeService.executeTrade(
                new TradeRequest(walletId, "BTC", new BigDecimal("11.0000"), TradeType.BUY)));

        verifyNoInteractions(transactionRepository);
    }

    @Test
    void shouldRejectSellWhenAssetQuantityIsInsufficient() {
        WalletAsset walletAsset = new WalletAsset();
        walletAsset.setQuantity(new BigDecimal("1.0000"));

        when(walletAssetRepository.findByWalletIdAndAssetSymbolForUpdate(walletId, "BTC"))
                .thenReturn(Optional.of(walletAsset));

        assertThrows(InsufficientAssetException.class, () -> tradeService.executeTrade(
                new TradeRequest(walletId, "BTC", new BigDecimal("2.0000"), TradeType.SELL)));

        verifyNoInteractions(transactionRepository);
    }
}
