package com.labo.fs.data.enums;

public enum OrganisationTypeEnum {
    SUPPLIER("Supplier"),
    LABORATORIE("laboratorie"),
    DEFAULT("");

    private String organisationType;

    private OrganisationTypeEnum(String type) {
        this.organisationType = type;
    }

    public String getOrganisationTypeEnum() {
        return organisationType;
    }
}
