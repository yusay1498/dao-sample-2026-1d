package com.example.event.api.domain.exception;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(String eventId) {
        super("Event not found : Event ID = " + eventId);
    }
}
