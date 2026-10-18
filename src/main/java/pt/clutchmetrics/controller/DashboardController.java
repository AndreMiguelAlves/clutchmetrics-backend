package pt.clutchmetrics.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pt.clutchmetrics.dto.DashboardEvolutionPointResponse;
import pt.clutchmetrics.dto.DashboardSummaryResponse;
import pt.clutchmetrics.entity.GameCode;
import pt.clutchmetrics.service.DashboardService;

import java.util.List;

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
            GameCode gameCode,

            @RequestParam(required = false)
            Integer days) {

        DashboardSummaryResponse response =
                dashboardService.getSummary(
                        authentication.getName(),
                        gameCode,
                        days
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/evolution")
    public ResponseEntity<List<DashboardEvolutionPointResponse>> getEvolution(
            Authentication authentication,

            @RequestParam(required = false)
            GameCode gameCode,

            @RequestParam(defaultValue = "10")
            int limit,

            @RequestParam(required = false)
            Integer days) {

        int safeLimit = Math.min(
                Math.max(limit, 1),
                50
        );

        List<DashboardEvolutionPointResponse> response =
                dashboardService.getEvolution(
                        authentication.getName(),
                        gameCode,
                        safeLimit,
                        days
                );

        return ResponseEntity.ok(response);
    }
}