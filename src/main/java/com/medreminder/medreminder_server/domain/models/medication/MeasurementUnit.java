package com.medreminder.medreminder_server.domain.models.medication;

public class MeasurementUnit {

    private final String id;
    private final Measurement name;
    private final String symbol;

    public MeasurementUnit(String id,
                           Measurement name) {
        this.id = id;
        this.name = name;
        this.symbol = name.getSymbol();
    }

    public String getId() {
        return id;
    }

    public Measurement getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }
}
