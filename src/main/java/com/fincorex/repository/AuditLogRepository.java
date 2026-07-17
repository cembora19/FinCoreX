package com.fincorex.repository;

import com.fincorex.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    List<AuditLog> findByWalletIdOrderByCreatedAtDesc(UUID walletId);
}
