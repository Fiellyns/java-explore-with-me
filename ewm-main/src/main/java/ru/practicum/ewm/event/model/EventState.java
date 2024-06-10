package ru.practicum.ewm.event.model;

public enum EventState {

    PENDING,

    PUBLISHED,

    CANCELED;

    public static EventState from(String state) {
        for (EventState value : EventState.values()) {
            if (value.name().equals(state)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown event state: " + state);
    }

}
