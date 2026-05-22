package com.fincorex.service.impl;

import com.fincorex.dto.request.CreateUserRequest;
import com.fincorex.dto.response.UserResponse;
import com.fincorex.entity.User;
import com.fincorex.entity.Wallet;
import com.fincorex.repository.UserRepository;
import com.fincorex.repository.WalletRepository;
import com.fincorex.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    public UserServiceImpl(UserRepository userRepository,
            WalletRepository walletRepository) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
    }

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());

        User savedUser = userRepository.save(user);

        Wallet wallet = new Wallet();
        wallet.setUser(savedUser);
        wallet.setBalance(BigDecimal.ZERO);

        walletRepository.save(wallet);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail());
    }

    @Override
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail()))
                .toList();
    }
}