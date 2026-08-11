package com.bookit.backend.model;

import java.util.Arrays;

public enum ScreenType {
    STANDARD,
    IMAX,
    IMAX_3D,
    DOLBY_ATMOS,
    VIP;
    public static boolean contains(String test) {
        return Arrays.stream(ScreenType.values())
                .anyMatch(e -> e.name().equals(test) );
    }
}
