package pt.clutchmetrics.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.clutchmetrics.entity.Plan;
import pt.clutchmetrics.entity.PlanCode;
import pt.clutchmetrics.entity.Subscription;
import pt.clutchmetrics.entity.User;
import pt.clutchmetrics.repository.PlanRepository;
import pt.clutchmetrics.repository.SubscriptionRepository;
import pt.clutchmetrics.repository.UserRepository;
import pt.clutchmetrics.exception.SubscriptionNotFoundException;
import pt.clutchmetrics.exception.InvalidSubscriptionChangeException;
import java.time.LocalDateTime;


@Service
public class SubscriptionService {


    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;
    private final UserRepository userRepository;

    public SubscriptionService(
            SubscriptionRepository subscriptionRepository,
            PlanRepository planRepository,
            UserRepository userRepository) {

        this.subscriptionRepository = subscriptionRepository;
        this.planRepository = planRepository;
        this.userRepository = userRepository;


    }

    @Transactional
    public Subscription createFreeSubscription(User user) {

        Plan freePlan = planRepository
                .findByCode(PlanCode.FREE)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "O plano FREE não existe."
                        ));

        Subscription subscription =
                new Subscription(user, freePlan);

        subscription.setAutoRenew(false);
        subscription.setEndsAt(null);

        return subscriptionRepository.save(subscription);
    }
@Transactional(readOnly = true)
public Subscription getMySubscription(String email) {

    User user = userRepository
            .findByEmail(email)
            .orElseThrow(() ->
                    new SubscriptionNotFoundException(
                            "Utilizador não encontrado."
                    ));

    return subscriptionRepository
            .findByUser(user)
            .orElseThrow(() ->
                    new SubscriptionNotFoundException(
                            "Subscrição não encontrada."
                    ));
    }

    @Transactional
    public Subscription changePlan(
            String email,
            PlanCode newPlanCode) {

        Subscription subscription =
                getMySubscription(email);

        Plan currentPlan = subscription.getPlan();

        if (currentPlan.getCode() == newPlanCode) {
            throw new InvalidSubscriptionChangeException(
                    "Já tens este plano ativo."
            );
        }

        Plan newPlan = planRepository
                .findByCode(newPlanCode)
                .orElseThrow(() ->
                        new InvalidSubscriptionChangeException(
                                "O plano selecionado não existe."
                        ));

        if (!newPlan.isActive()) {
            throw new InvalidSubscriptionChangeException(
                    "O plano selecionado não está disponível."
            );
        }

        subscription.setPlan(newPlan);
        subscription.setStartedAt(LocalDateTime.now());
        subscription.setActive(true);

        if (newPlanCode == PlanCode.PREMIUM) {

            subscription.setAutoRenew(true);
            subscription.setEndsAt(
                    LocalDateTime.now().plusMonths(1)
            );

        } else {

            subscription.setAutoRenew(false);
            subscription.setEndsAt(null);
        }

        return subscriptionRepository.save(subscription);
    }

}