package com.example.event.api.infrastructure;

import com.example.event.api.TestcontainersConfiguration;
import com.example.event.api.domain.entity.Event;
import com.example.event.api.domain.entity.vo.EventCategoryName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
// schema.sqlは外部DB（Testcontainers）に対しては明示指定しないと適用されないため、テストでのみ有効化する
@TestPropertySource(properties = "spring.sql.init.mode=always")
@Sql(statements = {
        "INSERT INTO venues (venue_id, venue_name, capacity, address) VALUES ('11111111-1111-1111-1111-111111111111', 'テスト会場', 300, '東京都渋谷区')",
        "INSERT INTO event_categories (event_category_id, event_category_name) VALUES ('22222222-2222-2222-2222-222222222222', 'ライブ')"
})
class JdbcEventRepositoryTest {

    private static final String VENUE_ID = "11111111-1111-1111-1111-111111111111";
    private static final String EVENT_CATEGORY_ID = "22222222-2222-2222-2222-222222222222";

    @Autowired
    JdbcEventRepository jdbcEventRepository;

    @Autowired
    JdbcClient jdbcClient;

    @Test
    @DisplayName("eventIdが未設定のイベントをsaveすると、新規レコードとしてDBへ永続化される")
    void givenNullEventId_whenSave_thenInsertNewEvent() {
        Event savedEvent = jdbcEventRepository.save(createEvent(null));

        assertThat(savedEvent.eventId()).isNotBlank();
        assertThat(countEventsById(savedEvent.eventId())).isEqualTo(1L);
    }

    @Test
    @DisplayName("eventIdが設定済みのイベントをsaveすると、既存レコードが更新される")
    void givenExistingEventId_whenSave_thenUpdateEvent() {
        Event savedEvent = jdbcEventRepository.save(createEvent(null));
        Event changedEvent = new Event(
                savedEvent.eventId(),
                savedEvent.venueId(),
                savedEvent.venueName(),
                savedEvent.eventCategoryId(),
                savedEvent.eventCategoryName(),
                "更新後のイベント名",
                savedEvent.performer(),
                savedEvent.description(),
                savedEvent.startTime(),
                savedEvent.endTime(),
                savedEvent.availableSeats(),
                savedEvent.reservedSeats()
        );

        jdbcEventRepository.save(changedEvent);

        String eventName = jdbcClient.sql("SELECT event_name FROM events WHERE event_id = :eventId")
                .param("eventId", savedEvent.eventId())
                .query(String.class)
                .single();
        assertThat(eventName).isEqualTo("更新後のイベント名");
    }

    @Test
    @DisplayName("存在するeventIdを指定した場合、findByIdはそのイベントを返す")
    void givenExistingEventId_whenFindById_thenReturnEvent() {
        Event savedEvent = jdbcEventRepository.save(createEvent(null));

        Optional<Event> actual = jdbcEventRepository.findById(savedEvent.eventId());

        assertThat(actual).contains(savedEvent);
    }

    @Test
    @DisplayName("存在しないeventIdを指定した場合、findByIdは空を返す")
    void givenUnknownEventId_whenFindById_thenReturnEmpty() {
        Optional<Event> actual = jdbcEventRepository.findById("00000000-0000-0000-0000-000000000000");

        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("deleteByIdを実行すると、対象イベントがDBから削除される")
    void givenExistingEventId_whenDeleteById_thenRemoveEventFromDatabase() {
        Event savedEvent = jdbcEventRepository.save(createEvent(null));

        jdbcEventRepository.deleteById(savedEvent.eventId());

        assertThat(countEventsById(savedEvent.eventId())).isEqualTo(0L);
    }

    private long countEventsById(String eventId) {
        return jdbcClient.sql("SELECT COUNT(*) FROM events WHERE event_id = :eventId")
                .param("eventId", eventId)
                .query(Long.class)
                .single();
    }

    private Event createEvent(String eventId) {
        return new Event(
                eventId,
                VENUE_ID,
                "テスト会場",
                EVENT_CATEGORY_ID,
                EventCategoryName.LIVE,
                "サンプルライブ",
                "サンプルアーティスト",
                "説明文",
                OffsetDateTime.parse("2026-10-01T18:00:00+09:00"),
                OffsetDateTime.parse("2026-10-01T21:00:00+09:00"),
                100,
                0
        );
    }
}
