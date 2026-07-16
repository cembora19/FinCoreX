package com.fincorex.service.impl;

import com.fincorex.entity.Asset;
import com.fincorex.entity.Wallet;
import com.fincorex.entity.WalletAsset;
import com.fincorex.dto.response.PortfolioResponse;
import com.fincorex.repository.TransactionRepository;
import com.fincorex.repository.WalletAssetRepository;
import com.fincorex.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private WalletAssetRepository walletAssetRepository;

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

        PortfolioResponse response = new WalletServiceImpl(
                walletRepository, transactionRepository, walletAssetRepository)
                .getPortfolio(walletId);

        assertEquals(0, response.assetValue().compareTo(new BigDecimal("30.00")));
        assertEquals(0, response.totalValue().compareTo(new BigDecimal("130.00")));
        assertEquals(0, response.totalCost().compareTo(new BigDecimal("20.00")));
        assertEquals(0, response.profitLoss().compareTo(new BigDecimal("10.00")));
        assertEquals(0, response.profitLossPercentage().compareTo(new BigDecimal("50.00")));
        assertEquals(0, response.assets().getFirst().profitLoss().compareTo(new BigDecimal("10.00")));
    }
}
