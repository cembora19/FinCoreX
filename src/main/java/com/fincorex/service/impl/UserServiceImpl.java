package com.fincorex.service.impl;

import com.fincorex.dto.request.CreateUserRequest;
import com.fincorex.dto.response.UserResponse;
import com.fincorex.entity.User;
import com.fincorex.repository.UserRepository;
import com.fincorex.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.fincorex.event.UserCreatedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserRepository userRepository,
            ApplicationEventPublisher eventPublisher,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));

        User savedUser = userRepository.save(user);

        eventPublisher.publishEvent(
                new UserCreatedEvent(savedUser.getId()));

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
