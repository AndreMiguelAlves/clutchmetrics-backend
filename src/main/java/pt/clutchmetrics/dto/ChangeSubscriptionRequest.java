package pt.clutchmetrics.dto;

import jakarta.validation.constraints.NotNull;
import pt.clutchmetrics.entity.PlanCode;

public class ChangeSubscriptionRequest {

    @NotNull(message = "O plano é obrigatório")
    private PlanCode planCode;

    public ChangeSubscriptionRequest() {
    }

    public PlanCode getPlanCode() {
        return planCode;
    }

    public void setPlanCode(PlanCode planCode) {
        this.planCode = planCode;
    }
}