package com.poc.apistyles.adapter.rest.controller;

import com.poc.apistyles.adapter.rest.dto.DashboardResponse;
import com.poc.apistyles.domain.port.inbound.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<DashboardResponse> getDashboard(@PathVariable UUID customerId) {
        var dashboard = dashboardService.getDashboard(customerId);
        return ResponseEntity.ok(DashboardResponse.from(dashboard));
    }
}
