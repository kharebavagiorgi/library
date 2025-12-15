package com.solvd.library.domain;

public enum Genre {
    // Assign an ID to each constant that matches the IDs in your database's 'genre' table.
    FICTION(1L),
    NON_FICTION(2L),
    SCIENCE(3L),
    HISTORY(4L),
    ART(5L),
    EDUCATION(6L);

    private final Long id;

    Genre(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public static Genre fromId(Long id) {
        for (Genre genre : values()) {
            if (genre.id.equals(id)) {
                return genre;
            }
        }
        throw new IllegalArgumentException("Unknown genre ID: " + id);
    }
}