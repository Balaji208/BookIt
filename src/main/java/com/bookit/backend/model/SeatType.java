package com.bookit.backend.model;

import java.util.Arrays;

public enum SeatType {
    REGULAR,
    PREMIUM,
    RECLINER,
    VIP;

    public static boolean contains(String test) {
        return Arrays.stream(SeatType.values())
                .anyMatch(e -> e.name().equals(test) );
    }
}
