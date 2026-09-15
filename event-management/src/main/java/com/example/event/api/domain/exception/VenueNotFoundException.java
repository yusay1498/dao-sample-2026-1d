package com.example.event.api.domain.exception;

public class VenueNotFoundException extends RuntimeException {
    public VenueNotFoundException(String venueId) {
        super("Venue not found : Venue ID = " + venueId);
    }
}
