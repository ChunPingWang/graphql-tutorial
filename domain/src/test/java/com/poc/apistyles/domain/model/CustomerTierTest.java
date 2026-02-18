package com.poc.apistyles.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerTierTest {

    @Test
    void bronze_shouldHaveZeroDiscount() {
        assertThat(CustomerTier.BRONZE.discountRate()).isEqualTo(0.0);
    }

    @Test
    void silver_shouldHave5PercentDiscount() {
        assertThat(CustomerTier.SILVER.discountRate()).isEqualTo(0.05);
    }

    @Test
    void gold_shouldHave10PercentDiscount() {
        assertThat(CustomerTier.GOLD.discountRate()).isEqualTo(0.10);
    }

    @Test
    void platinum_shouldHave15PercentDiscount() {
        assertThat(CustomerTier.PLATINUM.discountRate()).isEqualTo(0.15);
    }
}
