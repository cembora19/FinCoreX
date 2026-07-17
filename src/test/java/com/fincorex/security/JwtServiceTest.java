package com.fincorex.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService(
            "Zm9yLWRlbW8tb25seS1maW5jb3JleC1qd3Qtc2VjcmV0LWtleS0zMmJ5dGVz",
            86_400_000L);

    @Test
    void shouldGenerateTokenContainingUserEmail() {
        String token = jwtService.generateToken("ada@example.com", "USER");

        assertEquals("ada@example.com", jwtService.extractEmail(token));
    }
}
