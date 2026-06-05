package com.medreminder.medreminder_server.batch_jobs;


import com.medreminder.medreminder_server.TestConfig;
import org.junit.jupiter.api.Test;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import static org.assertj.core.api.Assertions.assertThat;


@SpringJUnitConfig(TestConfig.class)
@ActiveProfiles("test")
public class QuartzSchedulersTest {

    @Autowired
    @Qualifier("medScheduleEventSchedulerFactoryBean")
    private Scheduler medScheduleEventScheduler;

    @Autowired
    @Qualifier("downgradePlanSchedulerFactoryBean")
    private Scheduler downgradePlanScheduler;

    @Test
    void allNightlyJobsShouldBeRegistered() throws Exception {
        JobKey medScheduleEventSchedulerKey = JobKey.jobKey("medication_schedule_event_job_detail");
        JobKey downgradePlanSchedulerKey = JobKey.jobKey("downgrade_plan_job_detail");

        assertThat(medScheduleEventScheduler.checkExists(medScheduleEventSchedulerKey)).isTrue();
        assertThat(downgradePlanScheduler.checkExists(downgradePlanSchedulerKey)).isTrue();
    }

    @Test
    void allTriggersShouldBeLinkedToCorrectJob() throws Exception {
        TriggerKey triggerKey = TriggerKey.triggerKey("medication_schedule_event_trigger");
        Trigger trigger = medScheduleEventScheduler.getTrigger(triggerKey);

        TriggerKey triggerKey2 = TriggerKey.triggerKey("downgrade_plan_trigger");
        Trigger trigger2 = downgradePlanScheduler.getTrigger(triggerKey2);

        assertThat(trigger.getJobKey()).isEqualTo(JobKey.jobKey("medication_schedule_event_job_detail"));
        assertThat(trigger2.getJobKey()).isEqualTo(JobKey.jobKey("downgrade_plan_job_detail"));
    }

    @Test
    void medicationTriggerShouldFireAt1AM() throws Exception {
        TriggerKey triggerKey = TriggerKey.triggerKey("medication_schedule_event_trigger");
        CronTrigger trigger = (CronTrigger) medScheduleEventScheduler.getTrigger(triggerKey);

        assertThat(trigger).isNotNull();
        assertThat(trigger.getCronExpression()).isEqualTo("0 0 1 * * ?");
    }

    @Test
    void downgradePlanTriggerShouldFireAt2AM() throws Exception {
        TriggerKey triggerKey = TriggerKey.triggerKey("downgrade_plan_trigger");
        CronTrigger trigger = (CronTrigger) downgradePlanScheduler.getTrigger(triggerKey);

        assertThat(trigger).isNotNull();
        assertThat(trigger.getCronExpression()).isEqualTo("0 0 2 * * ?");
    }
}
