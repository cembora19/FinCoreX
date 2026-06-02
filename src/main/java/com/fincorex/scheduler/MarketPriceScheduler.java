package com.fincorex.scheduler;

import com.fincorex.entity.Asset;
import com.fincorex.repository.AssetRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Component
public class MarketPriceScheduler {

    private final AssetRepository assetRepository;

    private final Random random = new Random();

    public MarketPriceScheduler(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Scheduled(fixedRate = 15000)
    public void updatePrices() {

        List<Asset> assets = assetRepository.findAll();

        for (Asset asset : assets) {

            double changePercent = (random.nextDouble() * 10) - 5;

            BigDecimal currentPrice = asset.getPrice();

            BigDecimal change = currentPrice.multiply(
                    BigDecimal.valueOf(changePercent / 100));

            asset.setPrice(
                    currentPrice.add(change));
        }

        assetRepository.saveAll(assets);

        System.out.println("Market prices updated.");
    }
}