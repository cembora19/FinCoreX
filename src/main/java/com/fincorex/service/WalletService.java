package com.fincorex.service;

import com.fincorex.dto.request.DepositRequest;
import com.fincorex.dto.request.WithdrawRequest;

public interface WalletService {
    void deposit(DepositRequest request);

    void withdraw(WithdrawRequest request);
}