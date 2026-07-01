package com.medreminder.medreminder_server.application.config;



import jakarta.persistence.EntityManagerFactory;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.interceptor.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableTransactionManagement
public class TransactionConfig {
    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    public TransactionInterceptor txInterceptor(PlatformTransactionManager transactionManager) {
        TransactionInterceptor interceptor = new TransactionInterceptor();
        interceptor.setTransactionManager(transactionManager);

        RuleBasedTransactionAttribute readOnly = new RuleBasedTransactionAttribute();
        readOnly.setReadOnly(true);
        readOnly.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        RuleBasedTransactionAttribute readWrite = new RuleBasedTransactionAttribute();
        readWrite.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        readWrite.setRollbackRules(List.of(new RollbackRuleAttribute(Exception.class)));

        Map<String, TransactionAttribute> attrMap = new HashMap<>();
        attrMap.put("get*", readOnly);
        attrMap.put("find*", readOnly);
        attrMap.put("*", readWrite);

        NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();
        source.setNameMap(attrMap);

        interceptor.setTransactionAttributeSource(source);
        return interceptor;
    }

    @Bean
    public Advisor txAdvisor(TransactionInterceptor txInterceptor) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(
                "execution(* com.medreminder.medreminder_server.domain.services..*Impl.*(..))"
        );
        return new DefaultPointcutAdvisor(pointcut, txInterceptor);
    }
}
