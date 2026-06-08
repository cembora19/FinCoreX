package com.fincorex.service.impl;

import com.fincorex.dto.request.TradeRequest;
import com.fincorex.dto.request.TradeType;
import com.fincorex.dto.response.TradeResponse;
import com.fincorex.entity.*;
import com.fincorex.repository.*;
import com.fincorex.service.TradeService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TradeServiceImpl implements TradeService {

    private final WalletRepository walletRepository;
    private final AssetRepository assetRepository;
    private final WalletAssetRepository walletAssetRepository;
    private final TransactionRepository transactionRepository;

    public TradeServiceImpl(
            WalletRepository walletRepository,
            AssetRepository assetRepository,
            WalletAssetRepository walletAssetRepository,
            TransactionRepository transactionRepository) {

        this.walletRepository = walletRepository;
        this.assetRepository = assetRepository;
        this.walletAssetRepository = walletAssetRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public TradeResponse executeTrade(TradeRequest request) {

        // 1. Wallet
        Wallet wallet = walletRepository.findById(request.walletId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        // 2. Asset
        Asset asset = assetRepository.findBySymbol(request.assetSymbol())
                .orElseThrow(() -> new RuntimeException("Asset not found"));

        // 3. WalletAsset (PROFESYONEL QUERY)
        WalletAsset walletAsset = walletAssetRepository
                .findByWalletIdAndAssetSymbol(
                        request.walletId(),
                        asset.getSymbol())
                .orElse(null);

        BigDecimal tradeAmount;

        // ================= BUY =================
        if (request.type() == TradeType.BUY) {

            tradeAmount = asset.getPrice().multiply(request.quantity());

            if (wallet.getBalance().compareTo(tradeAmount) < 0) {
                throw new RuntimeException("Insufficient balance");
            }

            wallet.setBalance(wallet.getBalance().subtract(tradeAmount));

            // EDGE CASE FIX: first buy
            if (walletAsset == null) {
                walletAsset = new WalletAsset();
                walletAsset.setWallet(wallet);
                walletAsset.setAsset(asset);
                walletAsset.setQuantity(request.quantity());
            } else {
                walletAsset.setQuantity(
                        walletAsset.getQuantity().add(request.quantity()));
            }
        }

        // ================= SELL =================
        else {

            if (walletAsset == null ||
                    walletAsset.getQuantity().compareTo(request.quantity()) < 0) {
                throw new RuntimeException("Not enough asset");
            }

            tradeAmount = asset.getPrice().multiply(request.quantity());

            walletAsset.setQuantity(
                    walletAsset.getQuantity().subtract(request.quantity()));

            wallet.setBalance(wallet.getBalance().add(tradeAmount));
        }

        // 4. SAVE CHANGES
        walletAssetRepository.save(walletAsset);
        walletRepository.save(wallet);

        // 5. TRANSACTION LOG (ENHANCED)
        Transaction tx = new Transaction();
        tx.setWallet(wallet);
        tx.setAmount(tradeAmount);

        tx.setDescription(
                request.type()
                        + " "
                        + request.quantity()
                        + " "
                        + asset.getSymbol()
                        + " @ "
                        + asset.getPrice());

        tx.setCreatedAt(LocalDateTime.now());

        tx.setType(
                request.type() == TradeType.BUY
                        ? TransactionType.DEPOSIT
                        : TransactionType.WITHDRAW);

        transactionRepository.save(tx);

        return new TradeResponse(
                wallet.getId(),
                request.type().name(),
                asset.getSymbol(),
                request.quantity(),
                asset.getPrice(),
                tradeAmount,
                wallet.getBalance());
    }
}