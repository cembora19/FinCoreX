package com.fincorex.service.impl;

import com.fincorex.dto.request.DepositRequest;
import com.fincorex.dto.request.WithdrawRequest;
import com.fincorex.dto.response.PortfolioResponse;
import com.fincorex.dto.response.TransactionResponse;
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
import com.fincorex.exception.InsufficientBalanceException;
import com.fincorex.exception.ResourceNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.math.RoundingMode;

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
                .orElseThrow(() -> new ResourceNotFoundException("Wallet", request.userId()));

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
                .orElseThrow(() -> new ResourceNotFoundException("Wallet", request.userId()));

        if (wallet.getBalance().compareTo(request.amount()) < 0) {
            throw new InsufficientBalanceException();
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
                .orElseThrow(() -> new ResourceNotFoundException("Wallet", walletId));

        List<WalletAsset> assets = walletAssetRepository.findByWalletId(walletId);

        List<PortfolioResponse.AssetPosition> positions = assets.stream().map(a -> {

            BigDecimal value = a.getQuantity().multiply(a.getAsset().getPrice());
            BigDecimal cost = a.getQuantity().multiply(a.getAverageBuyPrice());
            BigDecimal profitLoss = value.subtract(cost);

            return new PortfolioResponse.AssetPosition(
                    a.getAsset().getSymbol(),
                    a.getQuantity(),
                    a.getAsset().getPrice(),
                    value,
                    a.getAverageBuyPrice(),
                    cost,
                    profitLoss,
                    percentage(profitLoss, cost));
        }).toList();

        BigDecimal assetValue = positions.stream()
                .map(PortfolioResponse.AssetPosition::value)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCost = positions.stream()
                .map(PortfolioResponse.AssetPosition::cost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal unrealizedProfitLoss = assetValue.subtract(totalCost);
        BigDecimal realizedProfitLoss = wallet.getRealizedProfitLoss();
        BigDecimal profitLoss = unrealizedProfitLoss.add(realizedProfitLoss);

        return new PortfolioResponse(
                wallet.getId(),
                wallet.getBalance(),
                assetValue,
                wallet.getBalance().add(assetValue),
                totalCost,
                unrealizedProfitLoss,
                realizedProfitLoss,
                profitLoss,
                percentage(profitLoss, totalCost),
                positions);
    }

    @Override
    public List<TransactionResponse> getTransactionHistory(UUID walletId) {
        walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet", walletId));

        return transactionRepository.findByWalletIdOrderByCreatedAtDesc(walletId)
                .stream()
                .map(transaction -> new TransactionResponse(
                        transaction.getId(),
                        transaction.getType(),
                        transaction.getAmount(),
                        transaction.getDescription(),
                        transaction.getCreatedAt()))
                .toList();
    }

    private BigDecimal percentage(BigDecimal value, BigDecimal base) {
        if (base.signum() == 0) {
            return BigDecimal.ZERO;
        }

        return value.multiply(BigDecimal.valueOf(100))
                .divide(base, 2, RoundingMode.HALF_UP);
    }

}
