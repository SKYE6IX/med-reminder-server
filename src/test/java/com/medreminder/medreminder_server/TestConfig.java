package com.medreminder.medreminder_server;


import com.medreminder.medreminder_server.application.batch_jobs.listener.DowngradePlanJobListener;
import com.medreminder.medreminder_server.application.batch_jobs.listener.MarkMissedDosageJobListener;
import com.medreminder.medreminder_server.application.batch_jobs.listener.MedicationScheduleEventJobListener;
import com.medreminder.medreminder_server.application.batch_jobs.listener.RenewPaidPlanJobListener;
import com.medreminder.medreminder_server.application.services.PaymentService;
import com.medreminder.medreminder_server.batch_jobs.config.DowngradePlanSchedulerTestConfig;
import com.medreminder.medreminder_server.batch_jobs.config.MarkMissedDosageSchedulerTestConfig;
import com.medreminder.medreminder_server.batch_jobs.config.MedScheduleEventSchedulerTestConfig;
import com.medreminder.medreminder_server.batch_jobs.config.RenewPaidPlanSchedulerTestConfig;
import com.medreminder.medreminder_server.domain.services.medications.MedicationRepository;
import com.medreminder.medreminder_server.domain.services.medications.ScheduleEventService;
import com.medreminder.medreminder_server.domain.services.medications.ScheduleEventServiceImpl;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationMapper;
import com.medreminder.medreminder_server.infrastructure.entity.subscription.SubscriptionMapper;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserMapper;
import jakarta.persistence.EntityManagerFactory;
import org.mockito.Mockito;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration()
@EnableJpaRepositories("com.medreminder.medreminder_server.infrastructure.repository")
@ComponentScan("com.medreminder.medreminder_server.infrastructure.repository")
@Import({
        MedScheduleEventSchedulerTestConfig.class,
        DowngradePlanSchedulerTestConfig.class,
        RenewPaidPlanSchedulerTestConfig.class,
        MarkMissedDosageSchedulerTestConfig.class,
})
@ActiveProfiles("test")
public class TestConfig {

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.HSQL)
                .addScript("classpath:org/springframework/batch/core/schema-hsqldb.sql")
                .addScript("classpath:org/quartz/impl/jdbcjobstore/tables_hsqldb.sql")
                .build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.medreminder.medreminder_server.infrastructure.entity");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Properties props = new Properties();
        props.put("hibernate.hbm2ddl.auto", "create-drop");
        props.put("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
        em.setJpaProperties(props);
        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    public ScheduleEventService scheduleEventService(MedicationRepository medicationRepository,
                                                      MedicationMapper  medicationMapper) {
        return new ScheduleEventServiceImpl(medicationRepository, medicationMapper);
    }

    @Bean
    @Primary
    public PaymentService paymentService() {
        return Mockito.mock(PaymentService.class);
    }

    @Bean
    public MedicationScheduleEventJobListener listener(){
        return new MedicationScheduleEventJobListener();
    }

    @Bean
    public DowngradePlanJobListener downgradePlanListener(){
        return new DowngradePlanJobListener();
    }

    @Bean
    public RenewPaidPlanJobListener renewPaidPlanListener(){
        return new RenewPaidPlanJobListener();
    }

    @Bean
    public MarkMissedDosageJobListener markMissedDosageListener(){
        return new MarkMissedDosageJobListener();
    }

//    HELPER BEANS
    @Bean
    public MedicationMapper medicationMapper() {
        return new MedicationMapper();
    }

    @Bean
    public UserMapper userMapper() {
        return new UserMapper();
    }

    @Bean
    public SubscriptionMapper subscriptionMapper() {
        return new SubscriptionMapper();
    }

}