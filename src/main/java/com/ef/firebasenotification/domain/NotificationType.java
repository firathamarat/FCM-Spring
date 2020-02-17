package com.ef.firebasenotification.domain;

public enum NotificationType {
    TOPIC("TOPIC"),
    SPECIFIC("SPECIFIC"),
    GROUP("GROUP"),
    ALL("ALL");

    private final String value;

    private NotificationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
