package com.fincorex.service;

import com.fincorex.dto.request.TradeRequest;

public interface TradeService {

    void executeTrade(TradeRequest request);
}