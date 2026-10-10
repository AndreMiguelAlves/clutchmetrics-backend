package pt.clutchmetrics.dto;

import pt.clutchmetrics.entity.PlanCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SubscriptionResponse {

    private Long subscriptionId;
    private PlanCode planCode;
    private String planName;
    private BigDecimal monthlyPrice;
    private boolean unlimitedAnalytics;
    private boolean export4k;
    private boolean watermarkEnabled;
    private LocalDateTime startedAt;
    private LocalDateTime endsAt;
    private boolean autoRenew;
    private boolean active;

    public SubscriptionResponse(
            Long subscriptionId,
            PlanCode planCode,
            String planName,
            BigDecimal monthlyPrice,
            boolean unlimitedAnalytics,
            boolean export4k,
            boolean watermarkEnabled,
            LocalDateTime startedAt,
            LocalDateTime endsAt,
            boolean autoRenew,
            boolean active) {

        this.subscriptionId = subscriptionId;
        this.planCode = planCode;
        this.planName = planName;
        this.monthlyPrice = monthlyPrice;
        this.unlimitedAnalytics = unlimitedAnalytics;
        this.export4k = export4k;
        this.watermarkEnabled = watermarkEnabled;
        this.startedAt = startedAt;
        this.endsAt = endsAt;
        this.autoRenew = autoRenew;
        this.active = active;
    }

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public PlanCode getPlanCode() {
        return planCode;
    }

    public String getPlanName() {
        return planName;
    }

    public BigDecimal getMonthlyPrice() {
        return monthlyPrice;
    }

    public boolean isUnlimitedAnalytics() {
        return unlimitedAnalytics;
    }

    public boolean isExport4k() {
        return export4k;
    }

    public boolean isWatermarkEnabled() {
        return watermarkEnabled;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getEndsAt() {
        return endsAt;
    }

    public boolean isAutoRenew() {
        return autoRenew;
    }

    public boolean isActive() {
        return active;
    }
}