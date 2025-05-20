package com.financePay.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RoleUser {
    ADMIN("Administrator"),
    USER("Regular User"),
    USER_GROUP("User Group"),
    ADMIN_GROUP("Administrator Group");

    // Campo imutável 
    private final String displayName;

    RoleUser(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Nomme amigável 
     */
    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @param value 
     * @return
     * @throws IllegalArgumentException 
     */
    @JsonCreator
    public static RoleUser fromDisplayName(String value) {
        for (RoleUser role : RoleUser.values()) {
            if (role.displayName.equalsIgnoreCase(value) || role.name().equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Função inválida: " + value);
    }
}
