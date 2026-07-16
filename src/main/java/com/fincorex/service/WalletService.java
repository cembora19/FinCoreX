package com.fincorex.service;

import java.util.UUID;
import java.util.List;

import com.fincorex.dto.request.DepositRequest;
import com.fincorex.dto.request.WithdrawRequest;
import com.fincorex.dto.response.PortfolioResponse;
import com.fincorex.dto.response.TransactionResponse;

public interface WalletService {
    void deposit(DepositRequest request);

    void withdraw(WithdrawRequest request);

    PortfolioResponse getPortfolio(UUID walletId);

    List<TransactionResponse> getTransactionHistory(UUID walletId);
}
