package com.fincorex.repository;

import com.fincorex.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;

public interface TransactionRepository
        extends JpaRepository<Transaction, UUID> {

    List<Transaction> findByWalletIdOrderByCreatedAtDesc(UUID walletId);
}
