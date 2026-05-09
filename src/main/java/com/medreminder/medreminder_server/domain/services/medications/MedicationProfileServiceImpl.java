package com.medreminder.medreminder_server.domain.services.medications;

import com.medreminder.medreminder_server.application.dtos.medication.*;
import com.medreminder.medreminder_server.application.dtos.user.ProfileResponse;
import com.medreminder.medreminder_server.application.exceptions.ResourceNotFoundException;
import com.medreminder.medreminder_server.domain.models.medication.*;
import com.medreminder.medreminder_server.domain.models.users.Profile;
import com.medreminder.medreminder_server.domain.services.users.ProfileRepository;
import com.medreminder.medreminder_server.infrastructure.entity.medications.*;
import com.medreminder.medreminder_server.infrastructure.entity.users.ProfileEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class MedicationProfileServiceImpl implements MedicationProfileService {

    private final MedicationRepository medicationRepository;
    private final ProfileRepository profileRepository;
    private final MedicationMapper medicationMapper;
    private final ScheduleEventService scheduleEventService;
    private final UserMapper userMapper;

    public MedicationProfileServiceImpl(MedicationRepository medicationRepository,
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

        ProfileEntity managedProfileEntity = profileRepository
                .findProfileById(cmd.getProfileId())
                .orElse(null);

        if (managedProfileEntity == null) {
            throw new ResourceNotFoundException("Profile not found!");
        }

        List<MedicationProfileEntity> mpe = managedProfileEntity.getMedicationProfile();

//        Convert profile entity to domain, and add the medication profile into it
        Profile domainProfile = userMapper.toDomain(managedProfileEntity);
//        We populate the domainProfile with an existing medprofiles if
//        they exist
        if (!mpe.isEmpty()){
            mpe.stream()
                    .map(medicationMapper::toDomain)
                    .forEach(domainProfile::addMedicationProfile);
        }

//        Start Creating New Medication Profile
        MedicationProfile medicationProfile = new MedicationProfile(null,
                true, cmd.getMedicationNote());
        Medication medication = createMedication(cmd);
        MedicationSchedule medicationSchedule = createMedicationSchedule(cmd.getSchedule());

//        Create schedule events
        List<ScheduleEvent> scheduleEvents = scheduleEventService.createScheduleEvents(medicationSchedule);

//        Mapped all the schedule events.
        scheduleEvents.forEach(medicationSchedule::addScheduleEvent);

//        Put them all together in the medication profile.
        medicationProfile.addMedication(medication);
        medicationProfile.addMedicationSchedule(medicationSchedule);
        createMedicationPack(cmd.getMedicationPack()).ifPresent(medicationProfile::addMedicationPack);

//        Add the medication profile to user profile.
        domainProfile.addMedicationProfile(medicationProfile);

//        Sync the data
        syncMedicationProfiles(domainProfile.getMedicationProfiles(), managedProfileEntity);

        ProfileEntity savedProfileEntity = profileRepository.saveProfile(managedProfileEntity);

        MedicationProfileEntity smp = savedProfileEntity.getMedicationProfile().getLast();

        return getMedicationProfileResponse(smp, savedProfileEntity);
    }

    @Override
    public MedicationProfileResponse updateMedicationProfile(String medicationProfileId,
                                                             UpdateMedicationCommand cmd) {
        MedicationProfileEntity managedMedicationProfile =
                medicationRepository.getMedicationProfileById(medicationProfileId);

        if (managedMedicationProfile == null) {
          throw new ResourceNotFoundException("Medication Profile not found!");
        }

        MedicationProfile domainMedicationProfile = medicationMapper
                .toDomain(managedMedicationProfile);

        cmd.getStatus().ifPresent(domainMedicationProfile::updateActive);
        cmd.getNote().ifPresent(domainMedicationProfile::updateNote);

        cmd.getRecurrenceRule().ifPresent(newRules -> {
            MedicationSchedule medicationSchedule = domainMedicationProfile.getMedicationSchedule();
            medicationSchedule.updateRecurrenceRule(newRules);

            List<ScheduleEvent> updatedEvents = scheduleEventService
                    .updateScheduleEventsRule(medicationSchedule);

//            Update medication Schedules
            managedMedicationProfile
                    .getMedicationSchedule()
                    .updateMedicationSchedule(medicationSchedule);

//            Remove all the pending event and reapply the newly created events
//            with new rules.
            managedMedicationProfile
                    .getMedicationSchedule()
                    .getScheduleEvents()
                    .removeIf(event ->
                            event.getStatus().equals("PENDING"));

            managedMedicationProfile
                    .getMedicationSchedule()
                    .getScheduleEvents()
                    .addAll(
                            updatedEvents
                                    .stream()
                                    .map(event ->
                                            medicationMapper.toEntity(event,
                                            managedMedicationProfile.getMedicationSchedule())).toList()
                    );
        });

        cmd.getDoseQuantity().ifPresent(newDoseQuantity -> {
            MedicationSchedule medicationSchedule = domainMedicationProfile.getMedicationSchedule();
            medicationSchedule.updateDoseQuantity(new BigDecimal(newDoseQuantity));

//          Update medication Schedules.
            managedMedicationProfile
                    .getMedicationSchedule()
                    .updateMedicationSchedule(medicationSchedule);

//            Update the dosage for all the pending events.
            managedMedicationProfile
                    .getMedicationSchedule()
                    .getScheduleEvents()
                    .stream()
                    .filter(event -> event.getStatus().equals("PENDING"))
                    .forEach(event -> event.updateDosage(new BigDecimal(newDoseQuantity)));
        });

        managedMedicationProfile.updateMedicationProfile(domainMedicationProfile);
        medicationRepository.saveMedicationProfile(managedMedicationProfile);

        return getMedicationProfileResponse(managedMedicationProfile);
    }

    @Override
    public MedicationProfileResponse getMedicationProfile(String medicationProfileId) {
        MedicationProfileEntity managedMedicationProfile =
                medicationRepository.getMedicationProfileById(medicationProfileId);

        if (managedMedicationProfile == null) {
            throw new ResourceNotFoundException("Medication Profile not found!");
        }

        return getMedicationProfileResponse(managedMedicationProfile);
    }

    @Override
    public List<MedicationProfileResponse> getMedicationProfiles(String userId) {

        List<MedicationProfileEntity> medicationProfiles = medicationRepository
                .getAllMedicationProfilesByUserId(userId);

        return medicationProfiles
                .stream()
                .map(this::getMedicationProfileResponse)
                .toList();
    }

    @Override
    public void deleteMedicationProfile(String medicationProfileId) {
        MedicationProfileEntity managedMedicationProfile =
                medicationRepository.getMedicationProfileById(medicationProfileId);

        if (managedMedicationProfile == null) {
            throw new ResourceNotFoundException("Medication Profile not found!");
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


//    HELPER METHODS
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
        final Locale locale = Locale.of("ru-RU");
        final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                .localizedBy(locale);

        return new MedicationSchedule(null,
                new BigDecimal(schedule.dosage()),
                schedule.recurrenceRule(),
                LocalDate.parse(schedule.startDate(), dateFormatter),
                schedule.timeZone(),
                new BigDecimal("0"));
    }

    private Optional<MedicationPack> createMedicationPack(CreateMedPack pack) {

        if( pack == null) {
            return Optional.empty();
        }

        MedicationPack medicationPack = new MedicationPack(null,
                new BigDecimal(pack.totalQuantity()),
                new BigDecimal(pack.totalQuantity()),
                pack.notifyRule(),
                LocalDateTime.now());

        return Optional.of(medicationPack);
    }

    private MedicationProfileResponse getMedicationProfileResponse(MedicationProfileEntity smp) {
        return getMedicationProfileResponse(smp, smp.getProfile());
    }

    private MedicationProfileResponse getMedicationProfileResponse(MedicationProfileEntity smp,
                                                                   ProfileEntity profileEntity) {
        String status = smp.isActive() ? "active" : "in_active";

//        Create a new Medication profile response object
        MedicationProfileResponse response = new MedicationProfileResponse(
                smp.getId(),
                smp.getMedication().getName(),
                smp.getMedication().getUnitType(),
                status,
                smp.getNote());

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
                schedule.getDoseQuantity().stripTrailingZeros().toPlainString(),
                smp.getMedication().getMeasurementUnit().getSymbol(),
                schedule.getRecurrenceRule(),
                schedule.getStartTime().toString(),
                schedule.getStartDate().toString())
        );

        if(smp.getMedicationPack() != null) {
            response.setAmountInPack(smp.getMedicationPack().getTotalQuantity().toString());
        }

        return response;
    }

    private void syncMedicationProfiles(List<MedicationProfile> domainMedicationProfiles,
                                        ProfileEntity managedProfile) {

        Map<String, MedicationProfileEntity> existingMedicationProfiles = new HashMap<>();

        List<MedicationProfileEntity> syncedMedicationProfiles = new ArrayList<>();

//      We only add medicationProfiles into the existingMedicationProfiles
//      Only if user has an existing one.
        if(!managedProfile.getMedicationProfile().isEmpty()){
            var toMaps = managedProfile.getMedicationProfile()
                    .stream()
                    .collect(Collectors.toMap(MedicationProfileEntity::getId,
                            emp -> emp));
            existingMedicationProfiles.putAll(toMaps);
        }

//        We check if the domain medication profile is empty, which mean user hasn't got
//        anymore medication profile. This is the case for when user delete their medication
//        profile.
        if(domainMedicationProfiles.isEmpty()) {
//            If the list is empty, we need to make sure that the managed also is.
            managedProfile.getMedicationProfile().clear();
        } else {
            var list = domainMedicationProfiles
                    .stream()
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