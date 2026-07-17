package com.medreminder.medreminder_server.application.batch_jobs.config;


import com.medreminder.medreminder_server.application.batch_jobs.listener.RenewPaidPlanJobListener;
import com.medreminder.medreminder_server.application.batch_jobs.processing.RenewPaidPlanProcessor;
import com.medreminder.medreminder_server.application.batch_jobs.processing.RenewPaidPlanResult;
import com.medreminder.medreminder_server.application.services.PaymentService;
import com.medreminder.medreminder_server.domain.services.users.UserRepository;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionPeriodEntity;
import com.medreminder.medreminder_server.infrastructure.repository.subscription.JpaSubscriptionRepository;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.JpaCursorItemReader;
import org.springframework.batch.infrastructure.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

@Configuration
@EnableBatchProcessing
public class RenewPaidPlanBatchConfig {
    @Bean
    public Job renewPaidPlanJob(JobRepository jobRepository,
                                Step renewPaidPlanStep,
                                RenewPaidPlanJobListener renewPaidPlanJobListener){
        return new JobBuilder("renew_paid_plan_job", jobRepository)
                .listener(renewPaidPlanJobListener)
                .start(renewPaidPlanStep)
                .build();

    }

    @Bean
    public Step renewPaidPlanStep(JobRepository jobRepository,
                                  PlatformTransactionManager transactionManager,
                                  JpaCursorItemReader<SubscriptionPeriodEntity> renewPaidPlanReader,
                                  RenewPaidPlanProcessor renewPaidPlanProcessor,
                                  ItemWriter<RenewPaidPlanResult> renewPaidPlanWriter){
        return new StepBuilder("renew_paid_plan_step", jobRepository)
                .<SubscriptionPeriodEntity, RenewPaidPlanResult> chunk(50)
                .transactionManager(transactionManager)
                .reader(renewPaidPlanReader)
                .processor(renewPaidPlanProcessor)
                .writer(renewPaidPlanWriter)
                .build();
    }

    @Bean
    public JpaCursorItemReader<SubscriptionPeriodEntity> renewPaidPlanReader(
            EntityManagerFactory entityManagerFactory) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Moscow"));

        return new JpaCursorItemReaderBuilder<SubscriptionPeriodEntity>()
                .name("renew_paid_plan_reader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("""
                    SELECT sp
                    FROM SUBSCRIPTION_PERIODS sp
                    JOIN sp.subscription s
                    WHERE s.status = 'ACTIVE'
                        AND sp.endTime <= :now
                        AND sp.status = 'ACTIVE'
                    """)
                .parameterValues(Map.of(
                        "now", now
                ))
                .build();
    }

    @Bean
    public RenewPaidPlanProcessor renewPaidPlanProcessor(PaymentService paymentService){
        return new RenewPaidPlanProcessor(paymentService);
    }

    @Bean
    public ItemWriter<RenewPaidPlanResult> renewPaidPlanWriter(JpaSubscriptionRepository subscriptionRepository,
                                                              UserRepository userRepository) {
        return items -> {
            for (RenewPaidPlanResult result : items.getItems()) {
                var updatedSubscription = subscriptionRepository
                        .save(result.subscriptionEntity());

//              Meaning: payment completed and a billing was created
//               so as new period for the billing to.
                if(result.newBillingEntity() != null){
                    var newPeriod = updatedSubscription.getPeriods().getLast();

                    var newBilling = result.newBillingEntity();
                    newBilling.setSubscriptionPeriod(newPeriod);

                    var userEntity = updatedSubscription.getUser();
                    userEntity.getBillings().add(newBilling);
                    userRepository.saveUser(userEntity);
                }
            }
        };
    }
}
