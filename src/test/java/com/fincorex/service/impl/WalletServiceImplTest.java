package com.fincorex.service.impl;

import com.fincorex.dto.request.DepositRequest;
import com.fincorex.dto.request.WithdrawRequest;
import com.fincorex.entity.Asset;
import com.fincorex.entity.Transaction;
import com.fincorex.entity.TransactionType;
import com.fincorex.entity.Wallet;
import com.fincorex.entity.WalletAsset;
import com.fincorex.dto.response.PortfolioResponse;
import com.fincorex.dto.response.TransactionResponse;
import com.fincorex.exception.InsufficientBalanceException;
import com.fincorex.exception.ResourceNotFoundException;
import com.fincorex.repository.TransactionRepository;
import com.fincorex.repository.WalletAssetRepository;
import com.fincorex.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private WalletAssetRepository walletAssetRepository;

    private WalletServiceImpl walletService;

    @BeforeEach
    void setUp() {
        walletService = new WalletServiceImpl(
                walletRepository, transactionRepository, walletAssetRepository);
    }

    @Test
    void shouldDepositMoneyAndCreateDepositTransaction() {
        UUID userId = UUID.randomUUID();
        Wallet wallet = new Wallet();
        wallet.setBalance(new BigDecimal("100.00"));
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        walletService.deposit(new DepositRequest(userId, new BigDecimal("25.00")));

        assertEquals(0, wallet.getBalance().compareTo(new BigDecimal("125.00")));
        verify(walletRepository).save(wallet);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionCaptor.capture());
        assertEquals(TransactionType.DEPOSIT, transactionCaptor.getValue().getType());
        assertEquals(0, transactionCaptor.getValue().getAmount().compareTo(new BigDecimal("25.00")));
    }

    @Test
    void shouldWithdrawMoneyAndCreateWithdrawTransaction() {
        UUID userId = UUID.randomUUID();
        Wallet wallet = new Wallet();
        wallet.setBalance(new BigDecimal("100.00"));
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        walletService.withdraw(new WithdrawRequest(userId, new BigDecimal("40.00")));

        assertEquals(0, wallet.getBalance().compareTo(new BigDecimal("60.00")));
        verify(walletRepository).save(wallet);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionCaptor.capture());
        assertEquals(TransactionType.WITHDRAW, transactionCaptor.getValue().getType());
        assertEquals(0, transactionCaptor.getValue().getAmount().compareTo(new BigDecimal("40.00")));
    }

    @Test
    void shouldRejectWithdrawWhenWalletBalanceIsInsufficient() {
        UUID userId = UUID.randomUUID();
        Wallet wallet = new Wallet();
        wallet.setBalance(new BigDecimal("10.00"));
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        assertThrows(InsufficientBalanceException.class, () -> walletService.withdraw(
                new WithdrawRequest(userId, new BigDecimal("25.00"))));

        verifyNoInteractions(transactionRepository);
    }

    @Test
    void shouldRejectDepositWhenWalletDoesNotExist() {
        UUID userId = UUID.randomUUID();
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> walletService.deposit(
                new DepositRequest(userId, new BigDecimal("25.00"))));

        verifyNoInteractions(transactionRepository);
    }

    @Test
    void shouldReturnWalletTransactionHistoryAsResponseDtos() {
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet();
        wallet.setId(walletId);

        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setWallet(wallet);
        transaction.setType(TransactionType.BUY);
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setDescription("BUY 1 BTC @ 100");
        transaction.setCreatedAt(LocalDateTime.now());

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(transactionRepository.findByWalletIdOrderByCreatedAtDesc(walletId))
                .thenReturn(List.of(transaction));

        List<TransactionResponse> response = walletService.getTransactionHistory(walletId);

        assertEquals(1, response.size());
        assertEquals(transaction.getId(), response.getFirst().id());
        assertEquals(TransactionType.BUY, response.getFirst().type());
        assertEquals(0, response.getFirst().amount().compareTo(new BigDecimal("100.00")));
        assertEquals(transaction.getDescription(), response.getFirst().description());
    }

    @Test
    void shouldCalculatePortfolioValueAndProfitLoss() {
        UUID walletId = UUID.randomUUID();

        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setBalance(new BigDecimal("100.00"));

        Asset asset = new Asset();
        asset.setSymbol("BTC");
        asset.setPrice(new BigDecimal("15.00"));

        WalletAsset walletAsset = new WalletAsset();
        walletAsset.setWallet(wallet);
        walletAsset.setAsset(asset);
        walletAsset.setQuantity(new BigDecimal("2.0000"));
        walletAsset.setAverageBuyPrice(new BigDecimal("10.0000"));

        when(walletRepository.findById(walletId)).thenReturn(java.util.Optional.of(wallet));
        when(walletAssetRepository.findByWalletId(walletId)).thenReturn(List.of(walletAsset));

        PortfolioResponse response = walletService.getPortfolio(walletId);

        assertEquals(0, response.assetValue().compareTo(new BigDecimal("30.00")));
        assertEquals(0, response.totalValue().compareTo(new BigDecimal("130.00")));
        assertEquals(0, response.totalCost().compareTo(new BigDecimal("20.00")));
        assertEquals(0, response.profitLoss().compareTo(new BigDecimal("10.00")));
        assertEquals(0, response.profitLossPercentage().compareTo(new BigDecimal("50.00")));
        assertEquals(0, response.assets().getFirst().profitLoss().compareTo(new BigDecimal("10.00")));
    }
}
