package com.medreminder.medreminder_server.infrastructure.entity.billing.mappers;

import com.medreminder.medreminder_server.domain.models.billing.Plan;
import com.medreminder.medreminder_server.domain.models.billing.PlanType;
import com.medreminder.medreminder_server.infrastructure.entity.billing.PlanEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserEntity;
import org.springframework.stereotype.Component;


@Component
public class PlanMapper {

    public PlanEntity toEntity(Plan plan, UserEntity user) {
        if (plan == null) {
            return null;
        }
        return new PlanEntity(
                plan.getId(),
                plan.getPlanType().toString(),
                plan.getMaxMedications(),
                plan.isManagedRelation(),
                plan.isRefillReminders(),
                plan.isReminderPreference(),
                user
        );
    }

    public Plan toDomain(PlanEntity planEntity) {
        if (planEntity == null) {
            return null;
        }
        return new Plan(
                planEntity.getId(),
                PlanType.valueOf(planEntity.getPlanType()),
                planEntity.getMaxMedications(),
                planEntity.isManagedRelation(),
                planEntity.isRefillReminders(),
                planEntity.isReminderPreference()
        );
    }
}
