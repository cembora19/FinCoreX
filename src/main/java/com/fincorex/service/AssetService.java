package com.fincorex.service;

import com.fincorex.dto.response.AssetResponse;

import java.util.List;

public interface AssetService {

    List<AssetResponse> getAllAssets();
}
