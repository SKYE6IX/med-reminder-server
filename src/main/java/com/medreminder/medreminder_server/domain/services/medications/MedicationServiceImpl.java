package com.medreminder.medreminder_server.domain.services.medications;

import com.medreminder.medreminder_server.application.dtos.medication.*;
import com.medreminder.medreminder_server.application.dtos.user.ProfileResponse;
import com.medreminder.medreminder_server.application.exceptions.BadRequestException;
import com.medreminder.medreminder_server.application.exceptions.ResourceNotFoundException;
import com.medreminder.medreminder_server.domain.models.medication.*;
import com.medreminder.medreminder_server.domain.models.users.Profile;
import com.medreminder.medreminder_server.domain.services.users.ProfileRepository;
import com.medreminder.medreminder_server.infrastructure.entity.medications.*;
import com.medreminder.medreminder_server.infrastructure.entity.users.ProfileEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserMapper;
import org.jspecify.annotations.NonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class MedicationServiceImpl implements MedicationService {

    private final MedicationRepository medicationRepository;
    private final ProfileRepository profileRepository;
    private final MedicationMapper medicationMapper;
    private final ScheduleEventService scheduleEventService;
    private final UserMapper userMapper;

    public MedicationServiceImpl(MedicationRepository medicationRepository,
                                 ProfileRepository profileRepository,
                                 MedicationMapper medicationMapper,
                                 ScheduleEventService scheduleEventService,
                                 UserMapper userMapper) {
        this.medicationRepository = medicationRepository;
        this.profileRepository = profileRepository;
        this.medicationMapper = medicationMapper;
        this.scheduleEventService = scheduleEventService;
        this.userMapper = userMapper;
    }

    @Override
    public MedicationProfileResponse createMedicationProfile(CreateMedicationCommand cmd) {

        ProfileEntity managedProfileEntity = profileRepository.findProfileById(cmd.getProfileId())
                .orElse(null);

//        Just in case profile isn't found. We need to set up an AOP that will
//        handle the exception to return clear message.
        if (managedProfileEntity == null) {
            return null;
        }

        Profile domainProfile = userMapper.toDomain(managedProfileEntity);

        MedicationProfile medicationProfile = new MedicationProfile(null,
                true, cmd.getMedicationNote());

        Medication medication = createMedication(cmd);
        MedicationSchedule medicationSchedule = createMedicationSchedule(cmd.getSchedule());

        scheduleEventService.createScheduleEvents(medicationSchedule);

        medicationProfile.addMedication(medication);
        medicationProfile.addMedicationSchedule(medicationSchedule);
        createMedicationPack(cmd.getMedicationPack()).ifPresent(medicationProfile::addMedicationPack);

        domainProfile.addMedicationProfile(medicationProfile);

//        Sync the data
        syncMedicationProfiles(domainProfile.getMedicationProfiles(), managedProfileEntity);

        ProfileEntity savedProfileEntity = profileRepository.saveProfile(managedProfileEntity);

        MedicationProfileEntity smp = savedProfileEntity.getMedicationProfile().getLast();

        return getResponse(smp, savedProfileEntity);
    }


    @Override
    public MedicationProfileResponse updateMedicationProfile(String medicationProfileId,
                                                             UpdateMedicationCommand cmd) {

        MedicationProfileEntity managedMedicationProfile =
                medicationRepository.getMedicationProfileById(medicationProfileId);

        if (managedMedicationProfile == null) {
          throw new ResourceNotFoundException("Medication Profile with id " + medicationProfileId + " not found");
        }

        MedicationProfile domainMedicationProfile = medicationMapper
                .toDomain(managedMedicationProfile);

        cmd.getStatus().ifPresent(domainMedicationProfile::updateActive);
        cmd.getNote().ifPresent(domainMedicationProfile::updateNote);

        cmd.getRecurrenceRule().ifPresent(newRules -> {
            domainMedicationProfile.getMedicationSchedule()
                    .updateRecurrenceRule(newRules);

            scheduleEventService.updateScheduleEventsRules(domainMedicationProfile.getMedicationSchedule());

            managedMedicationProfile.getMedicationSchedule()
                    .updateMedicationSchedule(domainMedicationProfile.getMedicationSchedule());

//            Sync the events
            Map<String, ScheduleEventEntity> existingScheduleEvents = managedMedicationProfile
                    .getMedicationSchedule()
                    .getScheduleEvents()
                    .stream()
                    .collect(Collectors.toMap(ScheduleEventEntity::getId,
                            event -> event));

            List<ScheduleEventEntity> syncedEvents = domainMedicationProfile
                    .getMedicationSchedule()
                    .getScheduleEvents()
                    .stream()
                    .map(event -> existingScheduleEvents
                            .getOrDefault(event.getId(),
                                    medicationMapper.toEntity(event, managedMedicationProfile.getMedicationSchedule())))
                    .toList();

            managedMedicationProfile.getMedicationSchedule().getScheduleEvents().clear();
            managedMedicationProfile.getMedicationSchedule().getScheduleEvents().addAll(syncedEvents);
        });

        cmd.getDoseQuantity().ifPresent(newDoseQuantity -> {
            domainMedicationProfile.getMedicationSchedule()
                    .updateDoseQuantity(newDoseQuantity);

            scheduleEventService
                    .updateScheduleEventsDosage(domainMedicationProfile.getMedicationSchedule());

            managedMedicationProfile.getMedicationSchedule()
                    .updateMedicationSchedule(domainMedicationProfile.getMedicationSchedule());

            Map<String, ScheduleEvent> updatedDomainEvents = domainMedicationProfile
                    .getMedicationSchedule()
                    .getScheduleEvents()
                    .stream()
                    .filter(event -> event.getStatus().equals("PENDING"))
                    .collect(Collectors.toMap(ScheduleEvent::getId, event -> event));

            managedMedicationProfile.getMedicationSchedule()
                    .getScheduleEvents()
                    .stream()
                    .filter(event -> updatedDomainEvents.containsKey(event.getId()))
                    .forEach(event -> event.updateScheduleEvent(updatedDomainEvents.get(event.getId())));
        });


        managedMedicationProfile.updateMedicationProfile(domainMedicationProfile);

        medicationRepository.saveMedicationProfile(managedMedicationProfile);

        return getResponse(managedMedicationProfile);
    }

    @Override
    public void deleteMedicationProfile(String medicationProfileId) {
        MedicationProfileEntity managedMedicationProfile =
                medicationRepository.getMedicationProfileById(medicationProfileId);

        if (managedMedicationProfile == null) {
            throw new ResourceNotFoundException("Medication Profile with id " + medicationProfileId + " not found");
        }

        Profile domainProfile = userMapper
                .toDomain(managedMedicationProfile.getProfile());

        List<MedicationProfile> allMedicationProfiles = managedMedicationProfile
                .getProfile()
                .getMedicationProfile()
                .stream()
                .map(medicationMapper::toDomain)
                .toList();

        allMedicationProfiles.forEach(domainProfile::addMedicationProfile);

        MedicationProfile toDelete = allMedicationProfiles
                .stream()
                .filter(mp -> mp.getId().equals(medicationProfileId))
                .findFirst()
                .orElse(null);

        domainProfile.removeMedicationProfile(toDelete);

        syncMedicationProfiles(domainProfile.getMedicationProfiles(), managedMedicationProfile.getProfile());

        profileRepository.saveProfile(managedMedicationProfile.getProfile());
    }

    @Override
    public List<ScheduleEventResponse> getMedicationScheduleEvents(String userId, String eventDate) {

        LocalDateTime startOfDay = LocalDate.parse(eventDate).atStartOfDay();
        LocalDateTime endOfDay = LocalDate.parse(eventDate).atTime(LocalTime.MAX);

        List<ScheduleEventEntity> scheduleEvents =
                medicationRepository.getScheduleEventsByUserAndDate(userId, startOfDay, endOfDay);

        return scheduleEvents.stream()
                .map(event -> {
                    ScheduleEventResponse ser = new ScheduleEventResponse(
                            event.getId(),
                            event.getStatus(),
                            event.getMedicationSchedule().getMedicationProfile().getMedication().getName(),
                            "",
                            event.getDosage(),
                            event.getMedicationSchedule().getMedicationProfile().getMedication().getMeasurementUnit().getSymbol(),
                            event.getScheduleAt().toString());

                    ProfileEntity profile = event.getMedicationSchedule().getMedicationProfile().getProfile();

                    ser.setProfile(new ProfileResponse(profile.getId(),
                            profile.getName(),
                            profile.getRelation(), profile.isSelf()));

                    if(event.getTakenAt() != null) {ser.setTakenAt(event.getTakenAt().toString());}

                    return ser;
                }).toList();
    }

    private Medication createMedication(CreateMedicationCommand cmd) {

        Medication medication = new Medication(null,
                cmd.getMedicationName(),
                Unit.valueOf(cmd.getMedicationUnit()));

        MeasurementUnit measurementUnit = new MeasurementUnit(null,
                Measurement.valueOf(cmd.getMedicationMeasurement()));
        medication.addMeasurementUnit(measurementUnit);

        return medication;
    }

    private MedicationSchedule createMedicationSchedule(CreateMedSchedule schedule) {
        DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        return new MedicationSchedule(null,
                schedule.dosage(),
                schedule.recurrenceRule(),
                LocalDateTime.parse(schedule.startDate(), dtf),
                schedule.timeZone());
    }

    private Optional<MedicationPack> createMedicationPack(CreateMedPack pack) {

        if( pack == null) {
            return Optional.empty();
        }

        MedicationPack medicationPack = new MedicationPack(null,
                pack.totalQuantity(),
                pack.totalQuantity(),
                pack.notifyRule(),
                LocalDateTime.now());

        return Optional.of(medicationPack);
    }

    private static @NonNull MedicationProfileResponse getResponse(MedicationProfileEntity smp) {
        return getResponse(smp, smp.getProfile());
    }

    private static @NonNull MedicationProfileResponse getResponse(MedicationProfileEntity smp,
                                                                  ProfileEntity profileEntity) {
        String status = smp.isActive() ? "active" : "in_active";
        String createdAt = smp.getCreatedAt().isPresent() ? smp.getCreatedAt().get().toString() : "";

//        Create a new Medication profile response object
        MedicationProfileResponse response = new MedicationProfileResponse(
                smp.getId(),
                smp.getMedication().getName(),
                smp.getMedication().getUnitType(),
                status, smp.getNote(),
                createdAt);

//        Attach the selected profile to the response
        response.setProfile(new ProfileResponse(
                profileEntity.getId(),
                profileEntity.getName(),
                profileEntity.getRelation(),
                profileEntity.isSelf()));

//        Acquire the schedule and create an object and attached it to the response
        MedicationScheduleEntity schedule = smp.getMedicationSchedule();

        response.setSchedule(new MedScheduleResponse(
                schedule.getId(),
                schedule.getDoseQuantity(),
                smp.getMedication().getMeasurementUnit().getSymbol(),
                schedule.getRecurrenceRule(),
                schedule.getStartTime().toString(),
                schedule.getStartDate().toString())
        );
        return response;
    }

    private void syncMedicationProfiles(List<MedicationProfile> domainMedicationProfiles,
                                        ProfileEntity managedProfile) {

        Map<String, MedicationProfileEntity> existingMedicationProfiles = new HashMap<>();
        List<MedicationProfileEntity> syncedMedicationProfiles = new ArrayList<>();

//        Only add med profiles if the profile has the medications
        if(!managedProfile.getMedicationProfile().isEmpty()){
            var toMaps = managedProfile.getMedicationProfile()
                    .stream()
                    .collect(Collectors.toMap(MedicationProfileEntity::getId,
                            emp -> emp));
            existingMedicationProfiles.putAll(toMaps);
        }

//        We check if the domain medication profile is empty, which mean user hasn't got
//        anymore medication profile.
        if(domainMedicationProfiles.isEmpty()) {
//            If the list is empty, we need to make sure that the managed also is.
            managedProfile.getMedicationProfile().clear();
        } else {
            var list = domainMedicationProfiles.stream()
                    .map(medProfile -> existingMedicationProfiles
                            .getOrDefault(medProfile.getId(),
                                    medicationMapper.toEntity(medProfile, managedProfile)))
                    .toList();
            syncedMedicationProfiles.addAll(list);

            managedProfile.getMedicationProfile().clear();
            managedProfile.getMedicationProfile().addAll(syncedMedicationProfiles);
        }
    }
}