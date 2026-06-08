package com.fincorex.service;

import com.fincorex.dto.request.TradeRequest;
import com.fincorex.dto.response.TradeResponse;

public interface TradeService {

    TradeResponse executeTrade(TradeRequest request);
}