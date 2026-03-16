package com.medreminder.medreminder_server.application.dtos.medication;

import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = CreateMedicationCommand.Builder.class)
public class CreateMedicationCommand {
    private String profileId;
    private String medicationName;
    private String medicationUnit;
    private String medicationMeasurement;
    private String medicationNote;
    private CreateMedSchedule schedule;
    private CreateMedPack medicationPack;

    public CreateMedicationCommand() {
    }

    public CreateMedicationCommand(Builder builder) {
        this.profileId = builder.profileId;
        this.medicationName = builder.medicationName;
        this.medicationUnit = builder.medicationUnit;
        this.medicationMeasurement = builder.medicationMeasurement;
        this.medicationNote = builder.medicationNote;
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

    public CreateMedSchedule getSchedule() {
        return schedule;
    }

    public CreateMedPack getMedicationPack() {
        return medicationPack;
    }

    @JsonPOJOBuilder()
    public static class Builder {
        private String profileId;
        private String medicationName;
        private String medicationUnit;
        private String medicationMeasurement;
        private String medicationNote;
        private CreateMedSchedule schedule;
        private CreateMedPack medicationPack;

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
        public Builder schedule(CreateMedSchedule schedule) {
            this.schedule = schedule;
            return this;
        }

        public Builder medicationPack(CreateMedPack medicationPack) {
            this.medicationPack = medicationPack;
            return this;
        }

        public CreateMedicationCommand build() {
            return new CreateMedicationCommand(this);
        }
    }
}




