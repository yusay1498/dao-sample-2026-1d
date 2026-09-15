package com.example.event.api.application;

import com.example.event.api.domain.entity.Event;
import com.example.event.api.domain.entity.EventCategory;
import com.example.event.api.domain.entity.Venue;
import com.example.event.api.domain.exception.EventCategoryNotFoundException;
import com.example.event.api.domain.exception.EventNotFoundException;
import com.example.event.api.domain.exception.VenueNotFoundException;
import com.example.event.api.domain.repository.EventCategoryRepository;
import com.example.event.api.domain.repository.EventRepository;
import com.example.event.api.domain.repository.VenueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EventApplicationService {

    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
    private final EventCategoryRepository eventCategoryRepository;

    public EventApplicationService(
            EventRepository eventRepository,
            VenueRepository venueRepository,
            EventCategoryRepository eventCategoryRepository
    ) {
        this.eventRepository = eventRepository;
        this.venueRepository = venueRepository;
        this.eventCategoryRepository = eventCategoryRepository;
    }

    @Transactional(readOnly = true)
    public List<Event> list() {
        return eventRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Event lookup(String eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));
    }

    public Event create(Event event) {
        Venue venue = findVenueOrThrow(event.venueId());
        EventCategory eventCategory = findEventCategoryOrThrow(event.eventCategoryId());

        Event eventToCreate = new Event(
                null,
                event.venueId(),
                venue.venueName(),
                event.eventCategoryId(),
                eventCategory.eventCategoryName(),
                event.eventName(),
                event.performer(),
                event.description(),
                event.startTime(),
                event.endTime(),
                event.availableSeats(),
                event.reservedSeats()
        );
        return eventRepository.save(eventToCreate);
    }

    public Event update(String eventId, Event event) {
        eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));
        Venue venue = findVenueOrThrow(event.venueId());
        EventCategory eventCategory = findEventCategoryOrThrow(event.eventCategoryId());

        Event eventToUpdate = new Event(
                eventId,
                event.venueId(),
                venue.venueName(),
                event.eventCategoryId(),
                eventCategory.eventCategoryName(),
                event.eventName(),
                event.performer(),
                event.description(),
                event.startTime(),
                event.endTime(),
                event.availableSeats(),
                event.reservedSeats()
        );
        return eventRepository.save(eventToUpdate);
    }

    public void delete(String eventId) {
        eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));
        eventRepository.deleteById(eventId);
    }

    private Venue findVenueOrThrow(String venueId) {
        return venueRepository.findById(venueId)
                .orElseThrow(() -> new VenueNotFoundException(venueId));
    }

    private EventCategory findEventCategoryOrThrow(String eventCategoryId) {
        return eventCategoryRepository.findById(eventCategoryId)
                .orElseThrow(() -> new EventCategoryNotFoundException(eventCategoryId));
    }
}
