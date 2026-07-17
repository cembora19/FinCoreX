package com.fincorex.repository;

import com.fincorex.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.util.Optional;
import java.util.UUID;

public interface AssetRepository
        extends JpaRepository<Asset, UUID> {

    Optional<Asset> findBySymbol(String symbol);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Asset a where a.symbol = :symbol")
    Optional<Asset> findBySymbolForUpdate(@Param("symbol") String symbol);
}
