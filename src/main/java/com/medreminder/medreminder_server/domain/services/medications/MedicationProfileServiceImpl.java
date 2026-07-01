package com.medreminder.medreminder_server.domain.services.medications;

import com.medreminder.medreminder_server.application.dtos.medication.*;
import com.medreminder.medreminder_server.application.exceptions.ResourceNotFoundException;
import com.medreminder.medreminder_server.domain.models.medication.*;
import com.medreminder.medreminder_server.domain.models.users.Profile;
import com.medreminder.medreminder_server.domain.services.users.ProfileRepository;
import com.medreminder.medreminder_server.infrastructure.entity.medications.*;
import com.medreminder.medreminder_server.infrastructure.entity.users.ProfileEntity;
import com.medreminder.medreminder_server.infrastructure.entity.users.UserMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

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
                true, cmd.getMedicationNote(), cmd.getMedicationReason());

        Medication medication = Helper.createMedication(cmd);

        MedicationSchedule medicationSchedule = Helper.createMedicationSchedule(cmd.getSchedule());

//        Create schedule events
        List<ScheduleEvent> scheduleEvents = scheduleEventService.createScheduleEvents(medicationSchedule);

//        Mapped all the schedule events.
        scheduleEvents.forEach(medicationSchedule::addScheduleEvent);

//        Put them all together in the medication profile.
        medicationProfile.addMedication(medication);
        medicationProfile.addMedicationSchedule(medicationSchedule);
        Helper.createMedicationPack(cmd.getMedicationPack()).ifPresent(medicationProfile::addMedicationPack);

//        Add the medication profile to user profile.
        domainProfile.addMedicationProfile(medicationProfile);

//        Sync the data
        Helper.syncMedicationProfiles(domainProfile.getMedicationProfiles(), managedProfileEntity);

        ProfileEntity savedProfileEntity = profileRepository.saveProfile(managedProfileEntity);

        MedicationProfileEntity smp = savedProfileEntity.getMedicationProfile().getLast();

        return Helper.getMedicationProfileResponse(smp, savedProfileEntity);
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

        cmd.getNote().ifPresent(note -> {
            if(note.isEmpty()){
                domainMedicationProfile.updateNote(null);
            } else {
                domainMedicationProfile.updateNote(note);
            }
        });

//        If user change their events schedules
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

//        If User update their dosage
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

        return Helper.getMedicationProfileResponse(managedMedicationProfile);
    }

    @Override
    public MedicationProfileResponse getMedicationProfile(String medicationProfileId) {
        MedicationProfileEntity managedMedicationProfile =
                medicationRepository.getMedicationProfileById(medicationProfileId);

        if (managedMedicationProfile == null) {
            throw new ResourceNotFoundException("Medication Profile not found!");
        }

        return Helper.getMedicationProfileResponse(managedMedicationProfile);
    }

    @Override
    public List<MedicationProfileResponse> getMedicationProfiles(String userId) {

        List<MedicationProfileEntity> medicationProfiles = medicationRepository
                .getAllMedicationProfilesByUserId(userId);

        return medicationProfiles
                .stream()
                .map(Helper::getMedicationProfileResponse)
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

        Helper.syncMedicationProfiles(domainProfile.getMedicationProfiles(),
                managedMedicationProfile.getProfile());

        profileRepository.saveProfile(managedMedicationProfile.getProfile());
    }

    @Override
    public MedicationPackResponse createMedicationPack(NewMedicationPackRequest newMedicationPackRequest) {
        MedicationProfileEntity managedMedicationProfile =
                medicationRepository.getMedicationProfileById(newMedicationPackRequest.medicationProfileId());

        if (managedMedicationProfile == null) {
            throw new ResourceNotFoundException("Medication Profile not found!");
        }

        MedicationPack pack = new MedicationPack(null,
                new BigDecimal(newMedicationPackRequest.totalQuantity()),
                new BigDecimal(newMedicationPackRequest.totalQuantity()),
                newMedicationPackRequest.reminderDays(),
                LocalDateTime.now(ZoneId.of(managedMedicationProfile.getMedicationSchedule().getTimeZone())),
                null,
                MedicationPackStatus.ACTIVE,
                false
        );

        managedMedicationProfile
                .getMedicationPacks()
                .add(medicationMapper.toEntity(pack, managedMedicationProfile));

        var newPack = medicationRepository
                .saveMedicationProfile(managedMedicationProfile)
                .getMedicationPacks().getLast();

        return Helper.getMedicationPackResponse(newPack);
    }

    @Override
    public MedicationPackResponse refillMedicationPack(RefillMedicationPackRequest refillMedicationPackRequest) {
        MedicationProfileEntity managedMedicationProfile =
                medicationRepository.getMedicationProfileById(refillMedicationPackRequest.medicationProfileId());

        if (managedMedicationProfile == null) {
            throw new ResourceNotFoundException("Medication Profile not found!");
        }

//        Get the pack we want to refill.
        MedicationPack existingPack = managedMedicationProfile
                .getMedicationPacks()
                .stream()
                .filter(packEntity ->
                        packEntity.getId().equals(refillMedicationPackRequest.medicationPackId()))
                .findFirst()
                .map(medicationMapper::toDomain)
                .orElse(null);

//        Throw error if this pack doesn't exist,
//        since we can't refill a pack that doesn't
//        exist.
        if (existingPack == null) {
            throw new ResourceNotFoundException("Medication Pack not found!, Can't be refilled");
        }

        boolean isExistingActive = existingPack.getStatus().equals(MedicationPackStatus.ACTIVE);

//       We start to create a new pack.
        MedicationPack newPack = new MedicationPack(
                null,
                new BigDecimal(refillMedicationPackRequest.totalQuantity()),
                new BigDecimal(refillMedicationPackRequest.totalQuantity()),
                refillMedicationPackRequest.reminderDays(),
                isExistingActive ? null : LocalDateTime.now(ZoneId.of(managedMedicationProfile.getMedicationSchedule().getTimeZone())),
                null,
                isExistingActive ? MedicationPackStatus.PENDING : MedicationPackStatus.ACTIVE,
                false
        );

//        Update the existing pack.
        existingPack.updateIsRefilled(true);
        managedMedicationProfile.getMedicationPacks()
                .stream()
                .filter(packEntity -> packEntity.getId().equals(existingPack.getId()))
                .findFirst()
                .ifPresent(medicationPack -> medicationPack.updateMedicationPack(existingPack));

        managedMedicationProfile
                .getMedicationPacks()
                .add(medicationMapper.toEntity(newPack, managedMedicationProfile));

        var refilledPack = medicationRepository
                .saveMedicationProfile(managedMedicationProfile)
                .getMedicationPacks().getLast();

        return Helper.getMedicationPackResponse(refilledPack);
    }

    @Override
    public List<MedicationPackResponse> getMedicationPacks(String userId) {
        List<MedicationPackEntity> packEntities = medicationRepository
                .getAllMedicationPacksByUserId(userId);

        if(!packEntities.isEmpty()){
            return packEntities
                    .stream()
                    .map(Helper::getMedicationPackResponse)
                    .toList();
        }

        return List.of();
    }
}