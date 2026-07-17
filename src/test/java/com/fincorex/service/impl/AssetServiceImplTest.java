package com.fincorex.service.impl;

import com.fincorex.entity.Asset;
import com.fincorex.repository.AssetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssetServiceImplTest {

    @Mock
    private AssetRepository assetRepository;

    @Test
    void shouldReturnAssetsAsResponseDtos() {
        Asset btc = new Asset();
        btc.setSymbol("BTC");
        btc.setName("Bitcoin");
        btc.setPrice(new BigDecimal("60000.00"));

        Asset apple = new Asset();
        apple.setSymbol("AAPL");
        apple.setName("Apple");
        apple.setPrice(new BigDecimal("180.00"));

        when(assetRepository.findAll(any(org.springframework.data.domain.Sort.class)))
                .thenReturn(List.of(apple, btc));

        var response = new AssetServiceImpl(assetRepository).getAllAssets();

        assertEquals(2, response.size());
        assertEquals("AAPL", response.get(0).symbol());
        assertEquals("Apple", response.get(0).name());
        assertEquals(0, response.get(0).price().compareTo(new BigDecimal("180.00")));
        assertEquals("BTC", response.get(1).symbol());
    }
}
