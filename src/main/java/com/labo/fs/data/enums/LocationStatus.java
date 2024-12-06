package com.labo.fs.data.enums;

public enum LocationStatus {
    INAVAILABLE("red", "Inavailable"),
    AVAILABLE("green", "Available"),
    CLEANING("orange", "Cleaning"),
    //MAINTENANCE("orange", "Maintenance"),
    DEFAULT("black", "Default");

    private final String color;
    private final String label;

    private LocationStatus(String color, String label) {
        this.color = color;
        this.label = label;
    }

    public String getColor() {
        return color;
    }

    public String getLabel() {
        return label;
    }

    public static LocationStatus fromColor(String color) {
        for (LocationStatus status : LocationStatus.values()) {
            if (status.color.equals(color)) {
                return status;
            }
        }
        return DEFAULT;
    }

    public static LocationStatus fromLabel(String label) {
        for (LocationStatus status : LocationStatus.values()) {
            if (status.label.equals(label)) {
                return status;
            }
        }
        return DEFAULT;
    }

    public static LocationStatus fromValue(String value) {
        for (LocationStatus status : LocationStatus.values()) {
            if (status.name().equals(value)) {
                return status;
            }
        }
        return DEFAULT;
    }
}
