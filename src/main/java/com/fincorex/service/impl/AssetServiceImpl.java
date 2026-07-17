package com.fincorex.service.impl;

import com.fincorex.dto.response.AssetResponse;
import com.fincorex.repository.AssetRepository;
import com.fincorex.service.AssetService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssetServiceImpl implements AssetService {

    private final AssetRepository assetRepository;

    public AssetServiceImpl(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Override
    public List<AssetResponse> getAllAssets() {
        return assetRepository.findAll(Sort.by(Sort.Direction.ASC, "symbol"))
                .stream()
                .map(asset -> new AssetResponse(
                        asset.getId(),
                        asset.getSymbol(),
                        asset.getName(),
                        asset.getPrice()))
                .toList();
    }
}
