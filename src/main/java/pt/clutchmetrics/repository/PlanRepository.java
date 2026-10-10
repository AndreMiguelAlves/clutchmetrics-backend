package pt.clutchmetrics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.clutchmetrics.entity.Plan;
import pt.clutchmetrics.entity.PlanCode;

import java.util.Optional;

public interface PlanRepository
        extends JpaRepository<Plan, Long> {

    Optional<Plan> findByCode(PlanCode code);
}