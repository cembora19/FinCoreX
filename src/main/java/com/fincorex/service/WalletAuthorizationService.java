package com.fincorex.service;

import com.fincorex.exception.ResourceNotFoundException;
import com.fincorex.repository.WalletRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class WalletAuthorizationService {

    private final WalletRepository walletRepository;

    public WalletAuthorizationService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public void assertWalletOwner(UUID walletId, String email) {
        if (!walletRepository.existsByIdAndUserEmail(walletId, email)) {
            throw new ResourceNotFoundException("Wallet", walletId);
        }
    }

    public void assertUserWalletOwner(UUID userId, String email) {
        if (!walletRepository.existsByUserIdAndUserEmail(userId, email)) {
            throw new ResourceNotFoundException("Wallet", userId);
        }
    }
}
