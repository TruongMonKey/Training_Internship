package com.example.crudjob.entity.enums;

public enum ERole {
    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_MANAGER("ROLE_MANAGER"),
    ROLE_CUSTOMER("ROLE_CUSTOMER");

    private final String value;

    ERole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Get enum from string value
     */
    public static ERole fromValue(String value) {
        for (ERole role : ERole.values()) {
            if (role.value.equals(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid role: " + value);
    }
}
