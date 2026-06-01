package com.fincorex.event;

import java.util.Objects;
import java.util.UUID;

public record UserCreatedEvent(UUID userId) {

        public UserCreatedEvent {
                Objects.requireNonNull(userId);
        }
}