package com.poc.apistyles.adapter.rest.dto;

import com.poc.apistyles.domain.model.CustomerTier;

public record CustomerRequest(String name, String email, CustomerTier tier) {}
