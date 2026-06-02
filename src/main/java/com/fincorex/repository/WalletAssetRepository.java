package com.fincorex.repository;

import com.fincorex.entity.WalletAsset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WalletAssetRepository extends JpaRepository<WalletAsset, UUID> {

    Optional<WalletAsset> findByWalletIdAndAssetSymbol(UUID walletId, String symbol);

    List<WalletAsset> findByWalletId(UUID walletId);
}