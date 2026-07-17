package com.fincorex.repository;

import com.fincorex.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {
    Optional<Wallet> findByUserId(UUID userId);

    boolean existsByIdAndUserEmail(UUID walletId, String email);

    boolean existsByUserIdAndUserEmail(UUID userId, String email);
}
