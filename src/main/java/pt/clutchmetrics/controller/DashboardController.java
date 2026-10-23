package pt.clutchmetrics.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.clutchmetrics.entity.MatchCategory;
import pt.clutchmetrics.dto.DashboardEvolutionPointResponse;
import pt.clutchmetrics.dto.DashboardSummaryResponse;
import pt.clutchmetrics.entity.GameCode;
import pt.clutchmetrics.service.DashboardService;
import pt.clutchmetrics.dto.DashboardHistoryResponse;
import pt.clutchmetrics.entity.MatchResult;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import pt.clutchmetrics.service.SubscriptionService;
import java.util.List;
import pt.clutchmetrics.dto.DashboardPerformanceInsightsResponse;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final SubscriptionService subscriptionService;

    public DashboardController(
            DashboardService dashboardService,
            SubscriptionService subscriptionService) {

        this.dashboardService = dashboardService;
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getSummary(
            Authentication authentication,

            @RequestParam(required = false)
            GameCode gameCode,

            @RequestParam(required = false)
            Integer days) {
        validateAnalyticsAccess(authentication.getName(), days);
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
        validateAnalyticsAccess(authentication.getName(), days);
        List<DashboardEvolutionPointResponse> response =
                dashboardService.getEvolution(
                        authentication.getName(),
                        gameCode,
                        safeLimit,
                        days
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<DashboardHistoryResponse> getHistory(
            Authentication authentication,

            @RequestParam(required = false)
            GameCode gameCode,

            @RequestParam(required = false)
            Integer days,

            @RequestParam(required = false)
            MatchResult result,

            @RequestParam(required = false)
            MatchCategory category,

            @RequestParam(required = false)
            Double minKda,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int pageSize) {
        validateAnalyticsAccess(authentication.getName(), days);
        DashboardHistoryResponse response =
                dashboardService.getHistory(
                        authentication.getName(),
                        gameCode,
                        days,
                        result,
                        category,
                        minKda,
                        page,
                        pageSize
                );

        return ResponseEntity.ok(response);
    }


    @GetMapping("/performance-insights")
    public ResponseEntity<DashboardPerformanceInsightsResponse>
    getPerformanceInsights(
            Authentication authentication,

            @RequestParam
            GameCode gameCode) {

        // Apenas Premium pode consultar esta funcionalidade
        validateAnalyticsAccess(
                authentication.getName(),
                null
        );

        DashboardPerformanceInsightsResponse response =
                dashboardService.getPerformanceInsights(
                        authentication.getName(),
                        gameCode
                );

        return ResponseEntity.ok(response);
    }


    private void validateAnalyticsAccess(
            String email,
            Integer days) {

        // O plano Free pode consultar até 30 dias
        if (days != null && days > 0 && days <= 30) {
            return;
        }

        // Para períodos superiores, verificar a subscrição
        boolean unlimitedAnalytics = subscriptionService
                .getMySubscription(email)
                .getPlan()
                .isUnlimitedAnalytics();

        if (!unlimitedAnalytics) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Premium subscription required for advanced analytics."
            );
        }
    }

}