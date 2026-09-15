package com.example.event.api.domain.repository;

import com.example.event.api.domain.entity.EventCategory;

import java.util.Optional;

public interface EventCategoryRepository {
    Optional<EventCategory> findById(String eventCategoryId);
}
