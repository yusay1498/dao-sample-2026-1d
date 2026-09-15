package com.example.event.api.infrastructure.dao;

import com.example.event.api.domain.entity.Event;
import com.example.event.api.domain.entity.vo.EventCategoryName;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class EventDao {

    private final JdbcClient jdbcClient;

    public EventDao(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    // venue_name/event_category_nameは常に最新値を返すため、events自身の列には持たずJOINで解決する
    public List<Event> findAll() {
        return jdbcClient.sql("""
                SELECT
                    e.event_id,
                    e.venue_id,
                    v.venue_name,
                    e.event_category_id,
                    ec.event_category_name,
                    e.event_name,
                    e.performer,
                    e.description,
                    e.start_time,
                    e.end_time,
                    e.available_seats,
                    e.reserved_seats
                FROM
                    events e
                    JOIN venues v ON e.venue_id = v.venue_id
                    JOIN event_categories ec ON e.event_category_id = ec.event_category_id
                ORDER BY
                    e.start_time
                """)
                .query(rs -> {
                    List<Event> events = new ArrayList<>();
                    while (rs.next()) {
                        events.add(new Event(
                                rs.getString("event_id"),
                                rs.getString("venue_id"),
                                rs.getString("venue_name"),
                                rs.getString("event_category_id"),
                                EventCategoryName.of(rs.getString("event_category_name")),
                                rs.getString("event_name"),
                                rs.getString("performer"),
                                rs.getString("description"),
                                rs.getObject("start_time", OffsetDateTime.class),
                                rs.getObject("end_time", OffsetDateTime.class),
                                rs.getInt("available_seats"),
                                rs.getInt("reserved_seats")
                        ));
                    }
                    return events;
                });
    }

    public Optional<Event> findById(String eventId) {
        return jdbcClient.sql("""
                SELECT
                    e.event_id,
                    e.venue_id,
                    v.venue_name,
                    e.event_category_id,
                    ec.event_category_name,
                    e.event_name,
                    e.performer,
                    e.description,
                    e.start_time,
                    e.end_time,
                    e.available_seats,
                    e.reserved_seats
                FROM
                    events e
                    JOIN venues v ON e.venue_id = v.venue_id
                    JOIN event_categories ec ON e.event_category_id = ec.event_category_id
                WHERE
                    e.event_id = :eventId
                """)
                .param("eventId", eventId)
                .query(rs -> {
                    if (!rs.next()) {
                        return Optional.empty();
                    }
                    return Optional.of(new Event(
                            rs.getString("event_id"),
                            rs.getString("venue_id"),
                            rs.getString("venue_name"),
                            rs.getString("event_category_id"),
                            EventCategoryName.of(rs.getString("event_category_name")),
                            rs.getString("event_name"),
                            rs.getString("performer"),
                            rs.getString("description"),
                            rs.getObject("start_time", OffsetDateTime.class),
                            rs.getObject("end_time", OffsetDateTime.class),
                            rs.getInt("available_seats"),
                            rs.getInt("reserved_seats")
                    ));
                });
    }

    public void insert(Event event) {
        jdbcClient.sql("""
                INSERT INTO events (
                    event_id,
                    venue_id,
                    event_category_id,
                    event_name,
                    performer,
                    description,
                    start_time,
                    end_time,
                    available_seats,
                    reserved_seats
                ) VALUES (
                    :eventId,
                    :venueId,
                    :eventCategoryId,
                    :eventName,
                    :performer,
                    :description,
                    :startTime,
                    :endTime,
                    :availableSeats,
                    :reservedSeats
                )
                """)
                .param("eventId", event.eventId())
                .param("venueId", event.venueId())
                .param("eventCategoryId", event.eventCategoryId())
                .param("eventName", event.eventName())
                .param("performer", event.performer())
                .param("description", event.description())
                .param("startTime", event.startTime())
                .param("endTime", event.endTime())
                .param("availableSeats", event.availableSeats())
                .param("reservedSeats", event.reservedSeats())
                .update();
    }

    public void update(Event event) {
        jdbcClient.sql("""
                UPDATE events SET
                    venue_id = :venueId,
                    event_category_id = :eventCategoryId,
                    event_name = :eventName,
                    performer = :performer,
                    description = :description,
                    start_time = :startTime,
                    end_time = :endTime,
                    available_seats = :availableSeats,
                    reserved_seats = :reservedSeats
                WHERE
                    event_id = :eventId
                """)
                .param("eventId", event.eventId())
                .param("venueId", event.venueId())
                .param("eventCategoryId", event.eventCategoryId())
                .param("eventName", event.eventName())
                .param("performer", event.performer())
                .param("description", event.description())
                .param("startTime", event.startTime())
                .param("endTime", event.endTime())
                .param("availableSeats", event.availableSeats())
                .param("reservedSeats", event.reservedSeats())
                .update();
    }

    public void deleteById(String eventId) {
        jdbcClient.sql("DELETE FROM events WHERE event_id = :eventId")
                .param("eventId", eventId)
                .update();
    }
}
