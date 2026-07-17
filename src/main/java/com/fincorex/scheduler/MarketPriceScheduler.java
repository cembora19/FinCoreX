package com.fincorex.scheduler;

import com.fincorex.entity.Asset;
import com.fincorex.repository.AssetRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.math.RoundingMode;

@Component
public class MarketPriceScheduler {

    private static final Logger log = LoggerFactory.getLogger(MarketPriceScheduler.class);

    private final AssetRepository assetRepository;

    private final Random random = new Random();

    public MarketPriceScheduler(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Scheduled(fixedRateString = "${market.price-update-ms:15000}")
    @Transactional
    public void updatePrices() {

        List<Asset> assets = assetRepository.findAll();

        for (Asset asset : assets) {

            double changePercent = (random.nextDouble() * 10) - 5;

            BigDecimal currentPrice = asset.getPrice();

            BigDecimal change = currentPrice.multiply(
                    BigDecimal.valueOf(changePercent / 100));

            asset.setPrice(currentPrice.add(change)
                    .max(new BigDecimal("0.01"))
                    .setScale(2, RoundingMode.HALF_UP));
        }

        assetRepository.saveAll(assets);

        log.debug("Updated market prices for {} assets", assets.size());
    }
}
