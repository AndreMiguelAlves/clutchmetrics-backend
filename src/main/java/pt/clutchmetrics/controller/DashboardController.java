package pt.clutchmetrics.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.clutchmetrics.dto.DashboardSummaryResponse;
import pt.clutchmetrics.entity.GameCode;
import pt.clutchmetrics.service.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(
            DashboardService dashboardService) {

        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getSummary(
            Authentication authentication,

            @RequestParam(required = false)
            GameCode gameCode) {

        DashboardSummaryResponse response =
                dashboardService.getSummary(
                        authentication.getName(),
                        gameCode
                );

        return ResponseEntity.ok(response);
    }
}