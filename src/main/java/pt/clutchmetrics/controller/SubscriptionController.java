package pt.clutchmetrics.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.clutchmetrics.dto.SubscriptionResponse;
import pt.clutchmetrics.entity.Plan;
import pt.clutchmetrics.entity.Subscription;
import pt.clutchmetrics.service.SubscriptionService;
import jakarta.validation.Valid;
import pt.clutchmetrics.dto.ChangeSubscriptionRequest;

@RestController
@RequestMapping("/api/subscription")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(
            SubscriptionService subscriptionService) {

        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/me")
    public ResponseEntity<SubscriptionResponse> getMySubscription(
            Authentication authentication) {

        Subscription subscription =
                subscriptionService.getMySubscription(
                        authentication.getName()
                );

        return ResponseEntity.ok(
                toResponse(subscription)
        );
    }

    @PostMapping("/change-plan")
    public ResponseEntity<SubscriptionResponse> changePlan(
            Authentication authentication,
            @Valid @RequestBody ChangeSubscriptionRequest request) {

        Subscription subscription =
                subscriptionService.changePlan(
                        authentication.getName(),
                        request.getPlanCode()
                );

        return ResponseEntity.ok(
                toResponse(subscription)
        );
    }

    private SubscriptionResponse toResponse(
            Subscription subscription) {

        Plan plan = subscription.getPlan();

        return new SubscriptionResponse(
                subscription.getId(),
                plan.getCode(),
                plan.getName(),
                plan.getMonthlyPrice(),
                plan.isUnlimitedAnalytics(),
                plan.isExport4k(),
                plan.isWatermarkEnabled(),
                subscription.getStartedAt(),
                subscription.getEndsAt(),
                subscription.isAutoRenew(),
                subscription.isActive()
        );
    }
}