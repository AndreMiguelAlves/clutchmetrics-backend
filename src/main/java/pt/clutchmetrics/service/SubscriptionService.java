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
import pt.clutchmetrics.audit.AuditAction;
import pt.clutchmetrics.audit.AuditService;


@Service
public class SubscriptionService {


    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    public SubscriptionService(
            SubscriptionRepository subscriptionRepository,
            PlanRepository planRepository,
            AuditService auditService,
            UserRepository userRepository) {

        this.subscriptionRepository = subscriptionRepository;
        this.planRepository = planRepository;
        this.userRepository = userRepository;
        this.auditService = auditService;


    }

    @Transactional
    public Subscription createFreeSubscription(User user) {

        Plan freePlan = planRepository
                .findByCode(PlanCode.FREE)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "The FREE plan does not exist."
                        ));

        Subscription subscription =
                new Subscription(user, freePlan);

        subscription.setAutoRenew(false);
        subscription.setEndsAt(null);

        return subscriptionRepository.save(subscription);
    }
    @Transactional
    public Subscription getMySubscription(String email) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new SubscriptionNotFoundException(
                                "User not found."
                        )
                );

        Subscription subscription = subscriptionRepository
                .findByUser(user)
                .orElse(null);

        if (subscription == null) {
            createFreeSubscription(user);

            subscription = subscriptionRepository
                    .findByUser(user)
                    .orElseThrow(() ->
                            new SubscriptionNotFoundException(
                                    "Could not create the subscription."
                            )
                    );
        }


        if (subscription.getPlan().getCode() == PlanCode.PREMIUM) {

            LocalDateTime endsAt = subscription.getEndsAt();

            boolean expired =
                    endsAt == null ||
                            !endsAt.isAfter(LocalDateTime.now());

            if (expired) {

                Plan freePlan = planRepository
                        .findByCode(PlanCode.FREE)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "The FREE plan does not exist."
                                )
                        );

                subscription.setPlan(freePlan);
                subscription.setStartedAt(LocalDateTime.now());
                subscription.setEndsAt(null);
                subscription.setAutoRenew(false);
                subscription.setActive(true);

                subscription = subscriptionRepository.save(subscription);

                auditService.log(
                        subscription.getUser(),
                        AuditAction.SUBSCRIPTION_CHANGED,
                        "Subscription",
                        subscription.getId(),
                        "Premium subscription expired. Automatically changed to FREE."
                );
            }
        }


        return subscription;
    }

    @Transactional
    public Subscription changePlan(
            String email,
            PlanCode newPlanCode) {

        Subscription subscription =
                getMySubscription(email);

        Plan currentPlan = subscription.getPlan();

        String oldPlan =
                currentPlan.getCode()
                        .name();

        if (currentPlan.getCode() == newPlanCode) {
            throw new InvalidSubscriptionChangeException(
                    "You already have this plan active."
            );
        }

        Plan newPlan = planRepository
                .findByCode(newPlanCode)
                .orElseThrow(() ->
                        new InvalidSubscriptionChangeException(
                                "The selected plan does not exist."
                        ));

        if (!newPlan.isActive()) {
            throw new InvalidSubscriptionChangeException(
                    "The selected plan is not available."
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

        Subscription savedSubscription =
                subscriptionRepository.save(subscription);

        auditService.log(
                savedSubscription.getUser(),
                AuditAction.SUBSCRIPTION_CHANGED,
                "Subscription",
                savedSubscription.getId(),

                "Subscription changed from "
                        + oldPlan
                        + " to "
                        + savedSubscription.getPlan()
                        .getCode()
                        .name()
                        + "."

        );

        return savedSubscription;

    }

}