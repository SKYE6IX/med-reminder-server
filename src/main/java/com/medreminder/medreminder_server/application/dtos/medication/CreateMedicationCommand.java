package com.medreminder.medreminder_server.application.dtos.medication;

import com.medreminder.medreminder_server.application.exceptions.BadRequestException;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.HashSet;
import java.util.Set;

@JsonDeserialize(builder = CreateMedicationCommand.Builder.class)
public class CreateMedicationCommand {
    private String profileId;
    private String medicationName;
    private String medicationUnit;
    private String medicationMeasurement;
    private String medicationNote;
    private String medicationReason;
    private String timeZone;
    private CreateMedSchedule schedule;
    private CreateMedicationPack medicationPack;

    public CreateMedicationCommand() {
    }

    public CreateMedicationCommand(Builder builder) {
        this.profileId = builder.profileId;
        this.medicationName = builder.medicationName;
        this.medicationUnit = builder.medicationUnit;
        this.medicationMeasurement = builder.medicationMeasurement;
        this.medicationNote = builder.medicationNote;
        this.medicationReason = builder.medicationReason;
        this.schedule = builder.schedule;
        this.medicationPack = builder.medicationPack;
    }

    public String getProfileId() {
        return profileId;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public String getMedicationUnit() {
        return medicationUnit;
    }

    public String getMedicationMeasurement() {
        return medicationMeasurement;
    }

    public String getMedicationNote() {
        return medicationNote;
    }

    public String getMedicationReason() {
        return medicationReason;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public CreateMedSchedule getSchedule() {
        return schedule;
    }

    public CreateMedicationPack getMedicationPack() {
        return medicationPack;
    }

    @JsonPOJOBuilder(withPrefix = "" )
    public static class Builder {
        private String profileId;
        private String medicationName;
        private String medicationUnit;
        private String medicationMeasurement;
        private String medicationNote;
        private String medicationReason;
        private String timeZone;
        private CreateMedSchedule schedule;
        private CreateMedicationPack medicationPack;

        public Builder profileId(String profileId) {
            this.profileId = profileId;
            return this;
        }

        public Builder medicationName(String medicationName) {
            this.medicationName = medicationName;
            return this;
        }

        public Builder medicationUnit(String medicationUnit) {
            this.medicationUnit = medicationUnit;
            return this;
        }

        public Builder medicationMeasurement(String medicationMeasurement) {
            this.medicationMeasurement = medicationMeasurement;
            return this;
        }

        public Builder medicationNote(String medicationNote) {
            this.medicationNote = medicationNote;
            return this;
        }

        public Builder medicationReason(String medicationReason) {
            this.medicationReason = medicationReason;
            return this;
        }

        public Builder timeZone(String timeZone) {
            this.timeZone = timeZone;
            return this;
        }

        public Builder schedule(CreateMedSchedule schedule) {
            this.schedule = schedule;
            return this;
        }

        public Builder medicationPack(CreateMedicationPack medicationPack) {
            this.medicationPack = medicationPack;
            return this;
        }

        public CreateMedicationCommand build() {
            validate();
            return new CreateMedicationCommand(this);
        }

        private void validate() {
            Set<String> missing = new HashSet<>();

            if (profileId == null || profileId.isEmpty()) {
                missing.add("profileId");
            }

            if(medicationName == null || medicationName.isEmpty()) {
                missing.add("medicationName");
            }

            if(medicationUnit == null || medicationUnit.isEmpty()) {
                missing.add("medicationUnit");
            }

            if(medicationMeasurement == null || medicationMeasurement.isEmpty()) {
                missing.add("medicationMeasurement");
            }

            if(schedule == null) {
                missing.add("schedule");
            }

            if (!missing.isEmpty()) {
                throw new BadRequestException("Missing required parameters");
            }
        }
    }
}




