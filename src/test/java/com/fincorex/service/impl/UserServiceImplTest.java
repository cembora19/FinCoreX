package com.fincorex.service.impl;

import com.fincorex.dto.request.CreateUserRequest;
import com.fincorex.entity.User;
import com.fincorex.event.UserCreatedEvent;
import com.fincorex.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, eventPublisher, passwordEncoder);
    }

    @Test
    void shouldHashPasswordWhenCreatingUser() {
        CreateUserRequest request = new CreateUserRequest(
                "Ada Lovelace", "ada@example.com", "password123");
        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setName(request.name());
        savedUser.setEmail(request.email());
        savedUser.setPasswordHash("encoded-password");

        when(passwordEncoder.encode(request.password())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        var response = userService.createUser(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("encoded-password", userCaptor.getValue().getPasswordHash());
        assertEquals(savedUser.getId(), response.id());
        verify(eventPublisher).publishEvent(any(UserCreatedEvent.class));
    }
}
