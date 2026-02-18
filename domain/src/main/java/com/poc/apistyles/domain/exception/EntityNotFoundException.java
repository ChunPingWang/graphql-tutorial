package com.poc.apistyles.domain.exception;

import java.util.UUID;

public final class EntityNotFoundException extends DomainException {

    public EntityNotFoundException(String entityType, UUID id) {
        super("%s not found with id: %s".formatted(entityType, id));
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}
