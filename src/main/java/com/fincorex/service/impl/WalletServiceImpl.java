package com.fincorex.service.impl;

import com.fincorex.dto.request.DepositRequest;
import com.fincorex.dto.request.WithdrawRequest;
import com.fincorex.entity.Wallet;
import com.fincorex.repository.WalletRepository;
import com.fincorex.service.WalletService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.fincorex.entity.Transaction;
import com.fincorex.entity.TransactionType;
import com.fincorex.repository.TransactionRepository;

import java.time.LocalDateTime;

@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public WalletServiceImpl(
            WalletRepository walletRepository,
            TransactionRepository transactionRepository) {

        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
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
}