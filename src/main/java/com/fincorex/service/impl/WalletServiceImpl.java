package com.fincorex.service.impl;

import com.fincorex.dto.request.DepositRequest;
import com.fincorex.dto.request.WithdrawRequest;
import com.fincorex.dto.response.PortfolioResponse;
import com.fincorex.entity.Wallet;
import com.fincorex.entity.WalletAsset;
import com.fincorex.repository.WalletRepository;
import com.fincorex.service.WalletService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.fincorex.entity.Transaction;
import com.fincorex.entity.TransactionType;
import com.fincorex.repository.TransactionRepository;
import com.fincorex.repository.WalletAssetRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final WalletAssetRepository walletAssetRepository;

    public WalletServiceImpl(
            WalletRepository walletRepository,
            TransactionRepository transactionRepository,
            WalletAssetRepository walletAssetRepository) {

        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.walletAssetRepository = walletAssetRepository;
    }

    @Override
    @Transactional
    public void deposit(DepositRequest request) {

        Wallet wallet = walletRepository.findByUserId(request.userId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        wallet.setBalance(wallet.getBalance().add(request.amount()));

        walletRepository.save(wallet);

        Transaction transaction = new Transaction();

        transaction.setWallet(wallet);
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setAmount(request.amount());
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setDescription("Deposit");

        transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public void withdraw(WithdrawRequest request) {

        Wallet wallet = walletRepository.findByUserId(request.userId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        if (wallet.getBalance().compareTo(request.amount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        wallet.setBalance(wallet.getBalance().subtract(request.amount()));

        walletRepository.save(wallet);

        Transaction transaction = new Transaction();

        transaction.setWallet(wallet);
        transaction.setType(TransactionType.WITHDRAW);
        transaction.setAmount(request.amount());
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setDescription("Withdraw");

        transactionRepository.save(transaction);
    }

    @Override
    public PortfolioResponse getPortfolio(UUID walletId) {

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        List<WalletAsset> assets = walletAssetRepository.findByWalletId(walletId);

        List<PortfolioResponse.AssetPosition> positions = assets.stream().map(a -> {

            BigDecimal value = a.getQuantity().multiply(a.getAsset().getPrice());

            return new PortfolioResponse.AssetPosition(
                    a.getAsset().getSymbol(),
                    a.getQuantity(),
                    a.getAsset().getPrice(),
                    value);
        }).toList();

        return new PortfolioResponse(
                wallet.getId(),
                wallet.getBalance(),
                positions);
    }

}