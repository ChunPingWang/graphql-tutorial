package com.poc.apistyles.domain.exception;

public sealed class DomainException extends RuntimeException
        permits EntityNotFoundException, InvalidStateTransitionException, InsufficientStockException, InvalidEntityException {

    protected DomainException(String message) {
        super(message);
    }

    protected DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
