package com.bookit.backend.model;

import java.util.Arrays;

public enum Language {
    ENGLISH,
    TAMIL,
    HINDI,
    TELUGU,
    MALAYALAM;

    public static boolean contains(String test) {
        return Arrays.stream(Language.values())
                .anyMatch(e -> e.name().equals(test) );
    }
}