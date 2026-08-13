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
    @Qualifier("purgeStaleTokenSchedulerFactoryBean")
    private Scheduler purgeStaleTokenScheduler;



    @Test
    void allNightlyJobsShouldBeRegistered() throws Exception {
        JobKey medScheduleEventSchedulerKey = JobKey.jobKey("medication_schedule_event_job_detail");
        JobKey purgeStaleTokenSchedulerKey = JobKey.jobKey("purge_stale_token_job_detail");


        assertThat(medScheduleEventScheduler.checkExists(medScheduleEventSchedulerKey)).isTrue();
        assertThat(purgeStaleTokenScheduler.checkExists(purgeStaleTokenSchedulerKey)).isTrue();
    }

    @Test
    void allTriggersShouldBeLinkedToCorrectJob() throws Exception {
        TriggerKey triggerKey = TriggerKey.triggerKey("medication_schedule_event_trigger");
        Trigger trigger = medScheduleEventScheduler.getTrigger(triggerKey);

        TriggerKey triggerKey5 = TriggerKey.triggerKey("purge_stale_token_trigger");
        Trigger trigger5 = purgeStaleTokenScheduler.getTrigger(triggerKey5);

        assertThat(trigger.getJobKey()).isEqualTo(JobKey.jobKey("medication_schedule_event_job_detail"));
        assertThat(trigger5.getJobKey()).isEqualTo(JobKey.jobKey("purge_stale_token_job_detail"));
    }

    @Test
    void medicationTriggerShouldFireAt1AM() throws Exception {
        TriggerKey triggerKey = TriggerKey.triggerKey("medication_schedule_event_trigger");
        CronTrigger trigger = (CronTrigger) medScheduleEventScheduler.getTrigger(triggerKey);

        assertThat(trigger).isNotNull();
        assertThat(trigger.getCronExpression()).isEqualTo("0 0 1 * * ?");
    }

    @Test
    void purgeStaleTokenTriggerShouldFireAt6AM() throws Exception {
        TriggerKey triggerKey = TriggerKey.triggerKey("purge_stale_token_trigger");
        CronTrigger trigger = (CronTrigger) purgeStaleTokenScheduler.getTrigger(triggerKey);

        assertThat(trigger).isNotNull();
        assertThat(trigger.getCronExpression()).isEqualTo("0 0 6 * * ?");
    }
}
