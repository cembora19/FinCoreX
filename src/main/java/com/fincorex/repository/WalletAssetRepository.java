package com.fincorex.repository;

import com.fincorex.entity.WalletAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WalletAssetRepository extends JpaRepository<WalletAsset, UUID> {

    Optional<WalletAsset> findByWalletIdAndAssetSymbol(UUID walletId, String symbol);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select wa from WalletAsset wa where wa.wallet.id = :walletId and wa.asset.symbol = :symbol")
    Optional<WalletAsset> findByWalletIdAndAssetSymbolForUpdate(
            @Param("walletId") UUID walletId,
            @Param("symbol") String symbol);

    List<WalletAsset> findByWalletId(UUID walletId);
}
