package com.example.event.api.domain.repository;

import com.example.event.api.domain.entity.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository {
    List<Event> findAll();
    Optional<Event> findById(String eventId);
    Event save(Event event);
    void deleteById(String eventId);
}
