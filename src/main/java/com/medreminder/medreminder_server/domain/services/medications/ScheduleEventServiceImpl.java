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
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class ScheduleEventServiceImpl implements ScheduleEventService {

    private final MedicationRepository medicationRepository;
    private final MedicationMapper medicationMapper;

    public ScheduleEventServiceImpl(MedicationRepository medicationRepository,
                                    MedicationMapper medicationMapper) {
        this.medicationRepository = medicationRepository;
        this.medicationMapper = medicationMapper;
    }

    @Override
    public List<ScheduleEvent> createScheduleEvents(MedicationSchedule schedule) {

        LocalDateTime now = LocalDateTime.now(ZoneId.of(schedule.getTimeZone()));

        return generateSchedulesDateTime(schedule, 7)
        .stream()
        .filter(dateTime -> dateTime.isAfter(now))
        .sorted()
                .map((date)-> new ScheduleEvent(null,
                        schedule.getDoseQuantity(),
                        date))
        .toList();
    }

    @Override
    public List<ScheduleEvent> updateScheduleEventsRule(MedicationSchedule schedule) {

        LocalDateTime now = LocalDateTime.now(ZoneId.of(schedule.getTimeZone()));

//      Collect all the pending event into list for deletion.
        Set<ScheduleEvent> pendingEvents = schedule
                .getScheduleEvents()
                .stream()
                .filter(event-> event.getStatus().equals("PENDING"))
                .collect(Collectors.toCollection(
                        ()-> new TreeSet<>(Comparator.comparing(event -> event
                                .getScheduleAt().toLocalDate()))
                ));

        return generateSchedulesDateTime(schedule, pendingEvents.size())
                .stream()
                .filter(dateTime -> dateTime.isAfter(now))
                .sorted()
                .map((date)-> new ScheduleEvent(null,
                        schedule.getDoseQuantity(),
                        date))
                .toList();
    }

    @Override
    public ScheduleEventResponse updateScheduleEvent(String scheduleEventId, Map<String, String> eventBody) {

        ScheduleEventEntity managedScheduleEvent = medicationRepository.getScheduleEventById(scheduleEventId);

        if(managedScheduleEvent == null) {
            throw new ResourceNotFoundException("Event is not found!");
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

    @Override
    public List<ScheduleEventResponse> getScheduleEvents(String userId, String eventDate) {
        final Locale locale = Locale.of("ru-RU");
        final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                .localizedBy(locale);

        LocalDateTime startOfDay = LocalDate.parse(eventDate, dateFormatter).atStartOfDay();
        LocalDateTime endOfDay = LocalDate.parse(eventDate, dateFormatter).atTime(LocalTime.MAX);

        List<ScheduleEventEntity> scheduleEvents = medicationRepository
                .getScheduleEventsByUserIdAndDates(userId, startOfDay, endOfDay);

        return scheduleEvents
                .stream()
                .map(this::getScheduleEventResponse)
                .toList();
    }

    private ScheduleEventResponse getScheduleEventResponse(ScheduleEventEntity managedScheduleEvent) {
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
                .atStartOfDay().atZone(zoneId).toLocalDateTime();

        LocalDateTime windowStart = schedule.getLastExpandedUntil() != null ?
                schedule.getLastExpandedUntil().toLocalDate().atStartOfDay().atZone(zoneId).toLocalDateTime()
                : startDateTime;

        LocalDateTime windowEnd = windowStart.plusDays(expansionWindowDays - 1)
                .toLocalDate()
                .atTime(23, 59, 59);

        Recur<LocalDateTime> recur = new Recur<>(schedule.getRecurrenceRule());

        return recur.getDates(windowStart, windowStart, windowEnd);
    }
}
