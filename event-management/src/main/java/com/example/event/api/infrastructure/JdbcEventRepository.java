package com.example.event.api.infrastructure;

import com.example.event.api.domain.entity.Event;
import com.example.event.api.domain.repository.EventRepository;
import com.example.event.api.infrastructure.dao.EventDao;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcEventRepository implements EventRepository {

    private final EventDao eventDao;

    public JdbcEventRepository(EventDao eventDao) {
        this.eventDao = eventDao;
    }

    @Override
    public List<Event> findAll() {
        return eventDao.findAll();
    }

    @Override
    public Optional<Event> findById(String eventId) {
        return eventDao.findById(eventId);
    }

    @Override
    public Event save(Event event) {
        // eventIdが未採番の場合は新規登録とみなし、ここでID採番を行う
        if (event.eventId() == null) {
            Event eventToInsert = new Event(
                    UUID.randomUUID().toString(),
                    event.venueId(),
                    event.venueName(),
                    event.eventCategoryId(),
                    event.eventCategoryName(),
                    event.eventName(),
                    event.performer(),
                    event.description(),
                    event.startTime(),
                    event.endTime(),
                    event.availableSeats(),
                    event.reservedSeats()
            );
            eventDao.insert(eventToInsert);
            return eventDao.findById(eventToInsert.eventId()).orElseThrow();
        }

        eventDao.update(event);
        return eventDao.findById(event.eventId()).orElseThrow();
    }

    @Override
    public void deleteById(String eventId) {
        eventDao.deleteById(eventId);
    }
}
