package com.fincorex.service;

import java.math.BigDecimal;
import java.util.UUID;

public interface WalletService {

    void deposit(UUID userId, BigDecimal amount);

    void withdraw(UUID userId, BigDecimal amount);
}