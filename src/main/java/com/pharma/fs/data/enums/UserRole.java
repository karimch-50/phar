package com.pharma.fs.data.enums;


public enum UserRole {
    SUPER_ADMIN("SA"),
    ADMIN("A"),
    SCHEDULER("S"),
    USER("U"),
    DEFAULT("");

    private final String role;
    private UserRole(String type) {
        this.role = type;
    }
    public String getUserRole() {
        return role;
    }
}
