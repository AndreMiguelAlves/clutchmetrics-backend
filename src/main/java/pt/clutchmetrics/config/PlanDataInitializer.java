package pt.clutchmetrics.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pt.clutchmetrics.entity.Plan;
import pt.clutchmetrics.entity.PlanCode;
import pt.clutchmetrics.repository.PlanRepository;

import java.math.BigDecimal;

@Configuration
public class PlanDataInitializer {

    @Bean
    CommandLineRunner initializePlans(
            PlanRepository planRepository) {

        return args -> {

            createPlanIfMissing(
                    planRepository,
                    PlanCode.FREE,
                    "Free",
                    new BigDecimal("0.00"),
                    false,
                    false,
                    true
            );

            createPlanIfMissing(
                    planRepository,
                    PlanCode.PREMIUM,
                    "Premium",
                    new BigDecimal("9.99"),
                    true,
                    true,
                    false
            );
        };
    }

    private void createPlanIfMissing(
            PlanRepository planRepository,
            PlanCode code,
            String name,
            BigDecimal monthlyPrice,
            boolean unlimitedAnalytics,
            boolean export4k,
            boolean watermarkEnabled) {

        if (planRepository.findByCode(code).isEmpty()) {

            Plan plan = new Plan(
                    code,
                    name,
                    monthlyPrice,
                    unlimitedAnalytics,
                    export4k,
                    watermarkEnabled
            );

            planRepository.save(plan);
        }
    }
}