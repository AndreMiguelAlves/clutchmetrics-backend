package pt.clutchmetrics.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "plans")
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 20)
    private PlanCode code;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(
            name = "monthly_price",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal monthlyPrice;

    @Column(name = "unlimited_analytics", nullable = false)
    private boolean unlimitedAnalytics;

    @Column(name = "export_4k", nullable = false)
    private boolean export4k;

    @Column(name = "watermark_enabled", nullable = false)
    private boolean watermarkEnabled;

    @Column(nullable = false)
    private boolean active = true;

    public Plan() {
    }

    public Plan(
            PlanCode code,
            String name,
            BigDecimal monthlyPrice,
            boolean unlimitedAnalytics,
            boolean export4k,
            boolean watermarkEnabled) {

        this.code = code;
        this.name = name;
        this.monthlyPrice = monthlyPrice;
        this.unlimitedAnalytics = unlimitedAnalytics;
        this.export4k = export4k;
        this.watermarkEnabled = watermarkEnabled;
    }

    public Long getId() {
        return id;
    }

    public PlanCode getCode() {
        return code;
    }

    public String getName() {
        return name;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}