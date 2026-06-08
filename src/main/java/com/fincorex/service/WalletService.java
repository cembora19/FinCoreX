package com.fincorex.service;

import java.util.UUID;

import com.fincorex.dto.request.DepositRequest;
import com.fincorex.dto.request.WithdrawRequest;
import com.fincorex.dto.response.PortfolioResponse;

public interface WalletService {
    void deposit(DepositRequest request);

    void withdraw(WithdrawRequest request);

    PortfolioResponse getPortfolio(UUID walletId);
}