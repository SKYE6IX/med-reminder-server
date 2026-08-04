package com.medreminder.medreminder_server.domain.services.medications;


import com.medreminder.medreminder_server.application.dtos.medication.LogScheduleEventRequest;
import com.medreminder.medreminder_server.application.dtos.medication.ScheduleEventResponse;
import com.medreminder.medreminder_server.application.dtos.user.ProfileResponse;
import com.medreminder.medreminder_server.application.exceptions.ResourceNotFoundException;
import com.medreminder.medreminder_server.domain.models.medication.MedicationPack;
import com.medreminder.medreminder_server.domain.models.medication.MedicationPackStatus;
import com.medreminder.medreminder_server.domain.models.medication.MedicationSchedule;
import com.medreminder.medreminder_server.domain.models.medication.ScheduleEvent;
import com.medreminder.medreminder_server.infrastructure.entity.medications.*;
import com.medreminder.medreminder_server.infrastructure.entity.users.ProfileEntity;
import net.fortuna.ical4j.model.Recur;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class ScheduleEventServiceImpl implements ScheduleEventService {

    private final MedicationRepository medicationRepository;
    private final MedicationMapper medicationMapper;

    private static final int DEFAULT_EXPANSION_WINDOW = 14;

    public ScheduleEventServiceImpl(MedicationRepository medicationRepository,
                                    MedicationMapper medicationMapper) {
        this.medicationRepository = medicationRepository;
        this.medicationMapper = medicationMapper;
    }

    @Override
    public List<ScheduleEvent> createScheduleEvents(MedicationSchedule schedule, String timeZone) {
        ZoneId zoneId = ZoneId.of(timeZone);

        final long MAX_EXPANSION_DAY = schedule.getEndDate() != null ?
                ChronoUnit.DAYS.between(schedule.getStartDate(), schedule.getEndDate())
                : DEFAULT_EXPANSION_WINDOW;

//        When creating, we need to check if we are expanding or starting new.
        final LocalDateTime dateTimeFrom = schedule.getLastExpandedUntil()
                != null ? schedule.getLastExpandedUntil().toLocalDate().atStartOfDay()
                : schedule.getStartDate().atStartOfDay();

        return getScheduleEventsResult(schedule,dateTimeFrom,zoneId,MAX_EXPANSION_DAY);
    }

    @Override
    public List<ScheduleEvent> updateScheduleEventsRule(MedicationSchedule schedule, String timeZone) {
        ZoneId zoneId = ZoneId.of(timeZone);

//      Collect all the pending event into list for deletion.
        Set<ScheduleEvent> pendingEvents = schedule
                .getScheduleEvents()
                .stream()
                .filter(event-> event.getStatus().equals("PENDING"))
                .sorted(Comparator.comparing(ScheduleEvent::getScheduleAt))
                .collect(Collectors.toCollection(
                        () -> new TreeSet<>(Comparator.comparing(event -> event
                                .getScheduleAt().toLocalDate()))
                ));

        final int MAX_EXPANSION_DAY = pendingEvents.size();
        final LocalDateTime dateTimeFrom = LocalDate.now(zoneId).atStartOfDay();

        return getScheduleEventsResult(schedule, dateTimeFrom, zoneId, MAX_EXPANSION_DAY);
    }

    @Override
    public ScheduleEventResponse logScheduleEvent(String scheduleEventId, LogScheduleEventRequest eventBody) {

        ScheduleEventEntity scheduleEvent = medicationRepository
                .getScheduleEventById(scheduleEventId);

        if( scheduleEvent == null) {
            throw new ResourceNotFoundException("Event is not found!");
        }

        if(!scheduleEvent.getStatus().equals("PENDING")) {
            return null;
        }

        String timeZone = eventBody.timeZone() != null ? eventBody.timeZone() : "Europe/Moscow";
        ZoneId zoneId = ZoneId.of(timeZone);

        MedicationProfileEntity managedMedicationProfile = scheduleEvent
                .getMedicationSchedule().getMedicationProfile();

        ScheduleEvent domainScheduleEvent = medicationMapper.toDomain(scheduleEvent);
        domainScheduleEvent.updateStatus(eventBody.action());

        MedicationSchedule domainMedicationSchedule = medicationMapper
                .toDomain(managedMedicationProfile.getMedicationSchedule());

        if(domainScheduleEvent.getStatus().equals("TAKEN")){
//          Track the amount that get taken and update medication schedule.
            BigDecimal amountTaken = domainMedicationSchedule.getTakenQuantity()
                    .add(domainScheduleEvent.getDosage());
            domainMedicationSchedule.updateTakenQuantity(amountTaken);

            managedMedicationProfile.getMedicationSchedule()
                    .updateMedicationSchedule(domainMedicationSchedule);

//         Update the medication pack for the active one,
//            flip to in_active if the currentQuantity is lower than the
//            incoming dosage.
            if(!managedMedicationProfile.getMedicationPacks().isEmpty()){
                MedicationPack activeMedicationPack = Helper
                        .getMedicationPackByStatus(managedMedicationProfile,
                        MedicationPackStatus.ACTIVE.toString(), medicationMapper);

                if(activeMedicationPack != null){
                    BigDecimal availableQuantity = activeMedicationPack.getCurrentQuantity();
                    BigDecimal dosage = domainScheduleEvent.getDosage();
                    BigDecimal remainingQuantity = availableQuantity.subtract(dosage);

                    if(remainingQuantity.compareTo(BigDecimal.ZERO) <= 0){
//                      End the pack and start a new one if user have a refill;
                        BigDecimal overflow = dosage.subtract(availableQuantity);

                        activeMedicationPack.updateCurrentQuantity(BigDecimal.ZERO);
                        activeMedicationPack.updateStatus(MedicationPackStatus.COMPLETED);
                        activeMedicationPack.updateEndedAt(LocalDateTime.now(zoneId));
                        Helper.syncMedicationPack(managedMedicationProfile, activeMedicationPack);

//                        Get a pending pack from the list if available
                        MedicationPack pendingMedicationPack = Helper
                                .getMedicationPackByStatus(managedMedicationProfile,
                                MedicationPackStatus.PENDING.toString(), medicationMapper);

                        if( pendingMedicationPack != null){
                            BigDecimal pendingPackRemainingQty = pendingMedicationPack
                                    .getCurrentQuantity().subtract(overflow);
                            pendingMedicationPack.updateStatus(MedicationPackStatus.ACTIVE);
                            pendingMedicationPack.updateCurrentQuantity(pendingPackRemainingQty);
                            pendingMedicationPack.updateStartedAt(LocalDateTime.now(zoneId));
                            Helper.syncMedicationPack(managedMedicationProfile, pendingMedicationPack);
                        }
                    } else {
                        activeMedicationPack.updateCurrentQuantity(remainingQuantity);
                        Helper.syncMedicationPack(managedMedicationProfile, activeMedicationPack);
                    }
                }
            }

            final LocalDateTime takenAt = LocalDateTime.now(zoneId);
            domainScheduleEvent.updateTakenAt(takenAt);
        }

        scheduleEvent.updateScheduleEvent(domainScheduleEvent);
        medicationRepository.saveScheduleEvent(scheduleEvent);
        medicationRepository.saveMedicationProfile(managedMedicationProfile);

        return getScheduleEventResponse(scheduleEvent);
    }

    @Override
    public void logOverdueScheduleEvent(String userId, Map<String, String> eventBody) {
        String eventDateUntil = eventBody.get(("eventDateUntil"));
        if(eventDateUntil == null) {
            throw new IllegalArgumentException("Event date until cannot be empty!");
        }

        LocalDate eventDate = LocalDate.parse(eventDateUntil, DateTimeFormatter.BASIC_ISO_DATE);
        LocalDateTime until = eventDate.atStartOfDay();

        List<ScheduleEventEntity> eventEntities = medicationRepository
                .getOverdueScheduleEvents(userId, until);

        if(!eventEntities.isEmpty()) {
            eventEntities.forEach(event -> {
                event.updateStatus("MISSED");
            });

            medicationRepository.saveAllScheduledEvent(eventEntities);
        }
    }

    @Override
    public List<ScheduleEventResponse> getScheduleEvents(String userId, String eventDate) {
        LocalDate date = LocalDate.parse(eventDate, DateTimeFormatter.BASIC_ISO_DATE);

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<ScheduleEventEntity> scheduleEvents = medicationRepository
                .getScheduleEvents(userId, startOfDay, endOfDay);

        return scheduleEvents
                .stream()
                .map(this::getScheduleEventResponse)
                .toList();
    }

    @Override
    public List<ScheduleEventResponse> getUpcomingScheduleEvents(String userId,
                                                         String eventDateFrom,
                                                         int limit) {
        LocalDate date = LocalDate.parse(eventDateFrom, DateTimeFormatter.BASIC_ISO_DATE);
        LocalDateTime dateFrom = date.atStartOfDay();

        List<ScheduleEventEntity> upcoming = medicationRepository
                .getUpcomingScheduleEvents(userId, dateFrom, limit);

        return upcoming
                .stream()
                .map(this::getScheduleEventResponse)
                .toList();
    }

    private ScheduleEventResponse getScheduleEventResponse(ScheduleEventEntity managedScheduleEvent) {
        MedicationScheduleEntity medicationSchedule =
                managedScheduleEvent.getMedicationSchedule();

        MedicationProfileEntity mpe = medicationSchedule.getMedicationProfile();
        MedicationEntity medication = mpe.getMedication();
        ProfileEntity profile = mpe.getProfile();

        ScheduleEventResponse response = new ScheduleEventResponse(
                managedScheduleEvent.getId(),
                managedScheduleEvent.getStatus(),
                medication.getName(),
                "",
                mpe.getId(),
                managedScheduleEvent.getDosage().stripTrailingZeros().toPlainString(),
                medication.getMeasurement(),
                managedScheduleEvent.getScheduleAt().toString());

        response.setProfile(new ProfileResponse(
                profile.getId(),
                profile.getAvatarUrl(),
                profile.getName(),
                profile.getRelation(),
                profile.isSelf()));

        if(managedScheduleEvent.getTakenAt() != null){
            response.setTakenAt(managedScheduleEvent.getTakenAt().toString());
        }
        return response;
    }

    private List<LocalDateTime> generateSchedulesEventDateTime(String rrule,
                                                               LocalDateTime eventDateFrom,
                                                               ZoneId zoneId,
                                                               long expansionWindowDays) {
        LocalDateTime windowStart = eventDateFrom
                .toLocalDate().atStartOfDay().atZone(zoneId).toLocalDateTime();

        LocalDateTime windowEnd = windowStart.plusDays(expansionWindowDays)
                .toLocalDate()
                .atTime(23, 59, 59);

        Recur<LocalDateTime> recur = new Recur<>(rrule);

        return recur.getDates(windowStart, windowStart, windowEnd);
    }

    private List<ScheduleEvent> getScheduleEventsResult(MedicationSchedule schedule,
                                                  LocalDateTime dateTimeFrom,
                                                  ZoneId zoneId,
                                                  long windowExpansion){
        LocalDateTime now = LocalDateTime.now(zoneId);

        List<LocalDateTime> events = generateSchedulesEventDateTime(
                schedule.getRecurrenceRule(),
                dateTimeFrom,
                zoneId,
                windowExpansion
        ).stream().sorted().toList();

        //  Get the first event from the list and use it as the start time.
        schedule.updateStartTime(events.getFirst());

//        Get the last event from the list at the LastExpandedUntil.
        schedule.updateLastExpandedUntil(events.getLast());

        return events
                .stream()
                .filter(dateTime -> dateTime.isAfter(now))
                .map((date)-> new ScheduleEvent(null,
                        schedule.getDoseQuantity(),
                        date))
                .toList();
    }
}