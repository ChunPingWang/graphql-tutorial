package com.poc.apistyles.domain.exception;

public final class InvalidStateTransitionException extends DomainException {

    public InvalidStateTransitionException(String entityType, String currentState, String targetState) {
        super("Cannot transition %s from %s to %s".formatted(entityType, currentState, targetState));
    }
}
