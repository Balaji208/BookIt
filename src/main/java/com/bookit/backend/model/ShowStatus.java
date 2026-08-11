package com.bookit.backend.model;

import java.util.Arrays;

public enum ShowStatus {
    SCHEDULED,
    CANCELLED,
    COMPLETED;
    public static boolean contains(String test) {
        return Arrays.stream(ShowStatus.values())
                .anyMatch(e -> e.name().equals(test));
    }
}
