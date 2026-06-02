package com.fincorex.service.impl;

import com.fincorex.entity.WalletAsset;
import com.fincorex.repository.WalletAssetRepository;
import com.fincorex.service.WalletAssetService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class WalletAssetServiceImpl implements WalletAssetService {

    private final WalletAssetRepository walletAssetRepository;

    public WalletAssetServiceImpl(WalletAssetRepository walletAssetRepository) {
        this.walletAssetRepository = walletAssetRepository;
    }

    @Override
    public List<WalletAsset> getPortfolio(UUID walletId) {
        return walletAssetRepository.findByWalletId(walletId);
    }
}