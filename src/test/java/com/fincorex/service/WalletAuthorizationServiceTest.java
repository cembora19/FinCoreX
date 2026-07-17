package com.fincorex.service;

import com.fincorex.exception.ResourceNotFoundException;
import com.fincorex.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletAuthorizationServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Test
    void shouldAllowWalletOwner() {
        UUID walletId = UUID.randomUUID();
        when(walletRepository.existsByIdAndUserEmail(walletId, "owner@example.com")).thenReturn(true);

        WalletAuthorizationService service = new WalletAuthorizationService(walletRepository);

        assertDoesNotThrow(() -> service.assertWalletOwner(walletId, "owner@example.com"));
    }

    @Test
    void shouldRejectWalletOwnedByAnotherUser() {
        UUID walletId = UUID.randomUUID();
        when(walletRepository.existsByIdAndUserEmail(walletId, "other@example.com")).thenReturn(false);

        WalletAuthorizationService service = new WalletAuthorizationService(walletRepository);

        assertThrows(ResourceNotFoundException.class,
                () -> service.assertWalletOwner(walletId, "other@example.com"));
    }

    @Test
    void shouldRejectUserIdThatDoesNotBelongToAuthenticatedUser() {
        UUID userId = UUID.randomUUID();
        when(walletRepository.existsByUserIdAndUserEmail(userId, "other@example.com")).thenReturn(false);

        WalletAuthorizationService service = new WalletAuthorizationService(walletRepository);

        assertThrows(ResourceNotFoundException.class,
                () -> service.assertUserWalletOwner(userId, "other@example.com"));
    }
}
