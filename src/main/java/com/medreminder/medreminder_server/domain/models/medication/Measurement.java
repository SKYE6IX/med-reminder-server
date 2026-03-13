package com.medreminder.medreminder_server.domain.models.medication;

public enum Measurement {

    CAPSULE, TABLET, SPRAY, DROPS, MILLIMETERS, SPOON;

    public String getSymbol() {
        if(this == MILLIMETERS){
            return "ml";
        }
        return name().toLowerCase();
    }
}

