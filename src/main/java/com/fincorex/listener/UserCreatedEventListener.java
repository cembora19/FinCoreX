package com.fincorex.listener;

import com.fincorex.entity.User;
import com.fincorex.entity.Wallet;
import com.fincorex.event.UserCreatedEvent;
import com.fincorex.repository.UserRepository;
import com.fincorex.repository.WalletRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import com.fincorex.exception.ResourceNotFoundException;

import java.math.BigDecimal;

@Component
public class UserCreatedEventListener {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    public UserCreatedEventListener(UserRepository userRepository,
            WalletRepository walletRepository) {

        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
    }

    @EventListener
    public void handleUserCreated(UserCreatedEvent event) {

        User user = userRepository.findById(event.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User", event.userId()));

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(BigDecimal.ZERO);

        walletRepository.save(wallet);
    }
}
