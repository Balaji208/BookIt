package com.bookit.backend.model;

import java.util.Arrays;

public enum Genre {
    ACTION,
    DRAMA,
    COMEDY,
    HORROR,
    THRILLER,
    ROMANCE,
    SCI_FI,
    ANIMATION,
    ADVENTURE,
    CRIME,
    MYSTERY;

    public static boolean contains(String test) {
        return Arrays.stream(Genre.values())
                .anyMatch(e -> e.name().equals(test) );
    }
}