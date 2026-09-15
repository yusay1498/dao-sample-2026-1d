package com.example.event.api.domain.repository;

import com.example.event.api.domain.entity.Venue;

import java.util.Optional;

public interface VenueRepository {
    Optional<Venue> findById(String venueId);
}
