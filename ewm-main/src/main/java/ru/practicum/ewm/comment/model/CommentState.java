package ru.practicum.ewm.comment.model;

public enum CommentState {

    PENDING,

    PUBLISHED,

    CANCELED;

    public static CommentState from(String state) {
        for (CommentState value : CommentState.values()) {
            if (value.name().equals(state)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown comment state: " + state);
    }

}
