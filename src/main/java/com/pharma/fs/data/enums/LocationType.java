package com.pharma.fs.data.enums;

public enum LocationType {
    // DEPARTMENT("Département"),
    ETAGE("Etage"),
    SERVICE("Service"),
    LABEL("Label"),
    SALLE("Salle"),
    OTHER("Autre");

    private String label;
    private LocationType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
