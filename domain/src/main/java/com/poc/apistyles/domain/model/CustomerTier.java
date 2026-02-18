package com.poc.apistyles.domain.model;

public enum CustomerTier {
    BRONZE,
    SILVER,
    GOLD,
    PLATINUM;

    public double discountRate() {
        return switch (this) {
            case BRONZE -> 0.0;
            case SILVER -> 0.05;
            case GOLD -> 0.10;
            case PLATINUM -> 0.15;
        };
    }
}
