package com.medreminder.medreminder_server.domain.services.medications;


import com.medreminder.medreminder_server.application.dtos.medication.ScheduleEventResponse;
import com.medreminder.medreminder_server.application.dtos.user.ProfileResponse;
import com.medreminder.medreminder_server.application.exceptions.ResourceNotFoundException;
import com.medreminder.medreminder_server.domain.models.medication.MedicationSchedule;
import com.medreminder.medreminder_server.domain.models.medication.ScheduleEvent;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationEntity;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationMapper;
import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationScheduleEntity;
import com.medreminder.medreminder_server.infrastructure.entity.medications.ScheduleEventEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.ProfileEntity;
import net.fortuna.ical4j.model.Recur;
import org.jspecify.annotations.NonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

public class ScheduleEventServiceImpl implements ScheduleEventService {

    private final MedicationRepository medicationRepository;
    private final MedicationMapper medicationMapper;

    public ScheduleEventServiceImpl(MedicationRepository medicationRepository,
                                    MedicationMapper medicationMapper) {
        this.medicationRepository = medicationRepository;
        this.medicationMapper = medicationMapper;
    }

    @Override
    public void createScheduleEvents(MedicationSchedule schedule) {

        LocalDateTime now = LocalDateTime.now(ZoneId.of(schedule.getTimeZone()));

        List<LocalDateTime> dateTimes =
                generateSchedulesDateTime(schedule, 7)
                .stream()
                .filter(dateTime -> dateTime.isAfter(now))
                .sorted()
                .toList();

        dateTimes.stream()
                .findFirst()
                .ifPresent(schedule::updateStartTime);

        dateTimes.stream()
                .map(date -> {
                    return new ScheduleEvent(null,
                            schedule.getDoseQuantity(),
                            date);
                })
                .forEach(schedule::addScheduleEvent);
    }

    @Override
    public void updateScheduleEventsRules(MedicationSchedule domainSchedule) {

//      Collect all the pending event into list for deletion.
        List<ScheduleEvent> pendingEvents = domainSchedule
                .getScheduleEvents()
                .stream()
                .filter(event -> event.getStatus().equals("PENDING"))
                .toList();

        if (pendingEvents.isEmpty()) {
            return;
        }

//        Passed all the above pending events to dates so it can be reused for the schedule date plus
//        either a new Time or Same as the previous Time. Depend on the User's new selection.
        List<LocalDate> pendingDates = pendingEvents
                .stream()
                .map(event -> event.getScheduleAt().toLocalDate())
                .distinct()
                .sorted()
                .toList();

//        Days left inclusive on the day user change the rules
        int daysLeft = (int) ChronoUnit.DAYS.between(pendingDates.getFirst(), pendingDates.getLast()) + 1;

//        Get the new dates base on the value user passed.
        List<LocalTime> newTimes = generateSchedulesDateTime(domainSchedule, daysLeft)
                .stream()
                .map(LocalDateTime::toLocalTime)
                .distinct()
                .sorted()
                .toList();

//        Always sync start time with the new updates of the "nextSchedules"
        domainSchedule.updateStartTime(LocalDateTime.of(pendingDates.getFirst(), newTimes.getFirst()));

//        Clear off the pending events from the domain. After this stage, only events in memory are
//        the one that either has TAKEN or MISSED status.
        domainSchedule.getScheduleEvents().removeAll(pendingEvents);

      pendingDates
                .stream()
                .flatMap(pendingDate -> newTimes.stream()
                        .map(time -> new ScheduleEvent(null,
                                domainSchedule.getDoseQuantity(),
                                LocalDateTime.of(pendingDate, time)))
                )
                .forEach(domainSchedule::addScheduleEvent);
    }

    @Override
    public void updateScheduleEventsDosage(MedicationSchedule domainSchedule) {

        domainSchedule.getScheduleEvents()
                .stream()
                .filter(event -> event.getStatus().equals("PENDING"))
                .forEach(event -> event.updateDosage(domainSchedule.getDoseQuantity()));

    }

    @Override
    public ScheduleEventResponse updateScheduleEvents(String scheduleEventId, Map<String, String> eventBody) {

        ScheduleEventEntity managedScheduleEvent = medicationRepository.getScheduleEventById(scheduleEventId);

        if(managedScheduleEvent == null) {
            throw new ResourceNotFoundException("Event with id " + scheduleEventId + " not found");
        }

        ScheduleEvent domainScheduleEvent = medicationMapper.toDomain(managedScheduleEvent);

        domainScheduleEvent.updateStatus(eventBody.get(("action")));

        if(domainScheduleEvent.getStatus().equals("TAKEN")){
            final LocalDateTime takenAt = LocalDateTime.now()
                    .atZone(ZoneId.of(managedScheduleEvent
                            .getMedicationSchedule()
                            .getTimeZone()))
                    .toLocalDateTime();

            domainScheduleEvent.updateTakenAt(takenAt);
        }

        managedScheduleEvent.updateScheduleEvent(domainScheduleEvent);

        medicationRepository.saveScheduleEvent(managedScheduleEvent);

        return getScheduleEventResponse(managedScheduleEvent);
    }

    private static @NonNull ScheduleEventResponse getScheduleEventResponse(ScheduleEventEntity managedScheduleEvent) {
        MedicationScheduleEntity medicationSchedule =
                managedScheduleEvent.getMedicationSchedule();

        MedicationEntity medication = medicationSchedule
                .getMedicationProfile().getMedication();

        ProfileEntity profile = medicationSchedule.getMedicationProfile().getProfile();

        ScheduleEventResponse response = new ScheduleEventResponse(managedScheduleEvent.getId(),
                managedScheduleEvent.getStatus(),
                medication.getName(),
                "",
                medicationSchedule.getDoseQuantity(),
                medication.getMeasurementUnit().getSymbol(),
                managedScheduleEvent.getScheduleAt().toString());

        response.setProfile(new ProfileResponse(profile.getId(),
                profile.getName(),
                profile.getRelation(), profile.isSelf()));

        if(managedScheduleEvent.getTakenAt() != null){
            response.setTakenAt(managedScheduleEvent.getTakenAt().toString());
        }

        return response;
    }

    private List<LocalDateTime> generateSchedulesDateTime(MedicationSchedule schedule,
                                                          int expansionWindowDays) {
        ZoneId zoneId = ZoneId.of(schedule.getTimeZone());

        LocalDateTime startDateTime = schedule.getStartDate()
                .atTime(7,0).atZone(zoneId).toLocalDateTime();

        LocalDateTime periodStart = schedule.getLastExpandedUntil() != null ?
                schedule.getLastExpandedUntil().atZone(zoneId).toLocalDateTime()
                : startDateTime;

        LocalDateTime windowStart = periodStart.toLocalDate().atStartOfDay();

        LocalDateTime windowEnd = windowStart.plusDays(expansionWindowDays - 1)
                .toLocalDate()
                .atTime(23, 59, 59);

        Recur<LocalDateTime> recur = new Recur<>(schedule.getRecurrenceRule());

        return recur.getDates(windowStart, windowStart, windowEnd);
    }
}
