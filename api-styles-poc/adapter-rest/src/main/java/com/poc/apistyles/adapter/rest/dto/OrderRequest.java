package com.poc.apistyles.adapter.rest.dto;

import java.util.List;
import java.util.UUID;

public record OrderRequest(UUID customerId, List<OrderItemRequest> items) {}
