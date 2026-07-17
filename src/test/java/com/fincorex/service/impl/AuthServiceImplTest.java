package com.fincorex.service.impl;

import com.fincorex.dto.request.LoginRequest;
import com.fincorex.dto.request.RegisterRequest;
import com.fincorex.dto.response.AuthResponse;
import com.fincorex.entity.Role;
import com.fincorex.entity.User;
import com.fincorex.event.UserCreatedEvent;
import com.fincorex.exception.EmailAlreadyUsedException;
import com.fincorex.repository.UserRepository;
import com.fincorex.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(
                userRepository,
                passwordEncoder,
                authenticationManager,
                jwtService,
                eventPublisher);
    }

    @Test
    void shouldRegisterUserWithEncodedPasswordAndUserRole() {
        RegisterRequest request = new RegisterRequest("Ada Lovelace", "ada@example.com", "password123");
        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setName(request.name());
        savedUser.setEmail(request.email());
        savedUser.setPasswordHash("encoded-password");
        savedUser.setRole(Role.USER);

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.password())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateToken(request.email(), Role.USER.name())).thenReturn("jwt-token");

        AuthResponse response = authService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals(request.name(), userCaptor.getValue().getName());
        assertEquals(request.email(), userCaptor.getValue().getEmail());
        assertEquals("encoded-password", userCaptor.getValue().getPasswordHash());
        assertEquals(Role.USER, userCaptor.getValue().getRole());
        assertEquals("jwt-token", response.token());
        assertEquals(request.email(), response.email());
        assertEquals("USER", response.role());
        verify(eventPublisher).publishEvent(any(UserCreatedEvent.class));
    }

    @Test
    void shouldRejectDuplicateEmailBeforeSaving() {
        RegisterRequest request = new RegisterRequest("Ada Lovelace", "ada@example.com", "password123");
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(new User()));

        assertThrows(EmailAlreadyUsedException.class, () -> authService.register(request));

        verifyNoInteractions(passwordEncoder, eventPublisher);
    }

    @Test
    void shouldAuthenticateAndReturnJwtOnLogin() {
        LoginRequest request = new LoginRequest("ada@example.com", "password123");
        User user = new User();
        user.setEmail(request.email());
        user.setRole(Role.USER);

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(request.email(), Role.USER.name())).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertEquals("jwt-token", response.token());
        assertEquals(request.email(), response.email());
        assertEquals("USER", response.role());
    }
}
