package com.medreminder.medreminder_server.batch_jobs;


import com.medreminder.medreminder_server.TestConfig;
import com.medreminder.medreminder_server.application.batch_jobs.config.QuartzSchedulerConfig;
import org.junit.jupiter.api.Test;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import static org.assertj.core.api.Assertions.assertThat;


@SpringJUnitConfig(TestConfig.class)
@ActiveProfiles("test")
public class QuartzSchedulerTest {

    @Autowired
    private Scheduler scheduler;

    @Test
    void allNightlyJobsShouldBeRegistered() throws Exception {
        JobKey jobKey = JobKey.jobKey("medicationScheduleEventScheduler");

        assertThat(scheduler.checkExists(jobKey)).isTrue();
    }

    @Test
    void allTriggersShouldBeLinkedToCorrectJob() throws Exception {
        TriggerKey triggerKey = TriggerKey.triggerKey("medicationScheduleEventTrigger");
        Trigger trigger = scheduler.getTrigger(triggerKey);

        assertThat(trigger.getJobKey()).isEqualTo(JobKey.jobKey("medicationScheduleEventScheduler"));
    }

    @Test
    void medicationTriggerShouldFireAt1AM() throws Exception {
        TriggerKey triggerKey = TriggerKey.triggerKey("medicationScheduleEventTrigger");
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);

        assertThat(trigger).isNotNull();
        assertThat(trigger.getCronExpression()).isEqualTo("0 0 1 * * ?");
    }
}
