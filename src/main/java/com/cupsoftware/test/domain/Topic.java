package com.cupsoftware.test.domain;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Topic {

    BUSINESS("Business"), MEDIA("Media"), SCIENCE("Science");

    private final String name;

    Topic(final String name) {
        this.name = name;
    }

    @JsonValue
    @Override
    public String toString() {
        return name;
    }
}
