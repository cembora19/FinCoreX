package com.fincorex.service;

import com.fincorex.entity.WalletAsset;

import java.util.List;
import java.util.UUID;

public interface WalletAssetService {

    List<WalletAsset> getPortfolio(UUID walletId);
}