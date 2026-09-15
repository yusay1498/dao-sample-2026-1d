package com.example.event.api.domain.exception;

public class EventCategoryNotFoundException extends RuntimeException {
    public EventCategoryNotFoundException(String eventCategoryId) {
        super("EventCategory not found : EventCategory ID = " + eventCategoryId);
    }
}
