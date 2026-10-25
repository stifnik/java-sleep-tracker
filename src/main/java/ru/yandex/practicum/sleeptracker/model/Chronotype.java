package ru.yandex.practicum.sleeptracker.model;

public enum Chronotype {
    OWL("сова"),
    LARK("жаворонок"),
    PIGEON("голубь");

    private final String description;

    Chronotype(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
