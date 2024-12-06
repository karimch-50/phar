package com.pharma.fs.data.enums;

public enum OrganisationTypeEnum {
    SUPPLIER("Supplier"),
    pharmaRATORIE("pharmaratorie"),
    DEFAULT("");

    private String organisationType;

    private OrganisationTypeEnum(String type) {
        this.organisationType = type;
    }

    public String getOrganisationTypeEnum() {
        return organisationType;
    }
}
