package com.example.event.api.infrastructure;

import com.example.event.api.domain.entity.Event;
import com.example.event.api.domain.entity.vo.EventCategoryName;
import com.example.event.api.infrastructure.dao.EventDao;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

// EventDaoをモックせず、実DBに対してJdbcEventRepository自身を直接検証する（@InjectMocksは使用しない）
@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@Sql(statements = {
        "INSERT INTO venues (venue_id, venue_name, capacity, address) VALUES ('11111111-1111-1111-1111-111111111111', 'テスト会場', 300, '東京都渋谷区')",
        "INSERT INTO event_categories (event_category_id, event_category_name) VALUES ('22222222-2222-2222-2222-222222222222', 'ライブ')"
})
class JdbcEventRepositoryTest {

    private static final String VENUE_ID = "11111111-1111-1111-1111-111111111111";
    private static final String EVENT_CATEGORY_ID = "22222222-2222-2222-2222-222222222222";

    @Container
    static PostgreSQLContainer postgresContainer = new PostgreSQLContainer(DockerImageName.parse("postgres:latest"));

    @Autowired
    JdbcClient jdbcClient;

    @BeforeAll
    static void startContainers() {
        postgresContainer.start();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);

        // schema.sqlを適用するために必要
        registry.add("spring.sql.init.mode", () -> "always");
    }

    @Test
    @DisplayName("eventIdが未設定のイベントをsaveすると、ID採番したうえでDBへ永続化される")
    void givenNullEventId_whenSave_thenInsertWithGeneratedId() {
        JdbcEventRepository eventRepository = new JdbcEventRepository(new EventDao(jdbcClient));
        Event event = createEvent(null);

        Event actual = eventRepository.save(event);

        assertThat(actual.eventId()).isNotBlank();
        assertThat(actual.eventName()).isEqualTo(event.eventName());
        assertThat(selectEventName(actual.eventId())).contains(event.eventName());
    }

    @Test
    @DisplayName("eventIdが設定済みのイベントをsaveすると、既存レコードが更新される")
    void givenExistingEventId_whenSave_thenUpdateEvent() {
        JdbcEventRepository eventRepository = new JdbcEventRepository(new EventDao(jdbcClient));
        Event savedEvent = eventRepository.save(createEvent(null));
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

        Event actual = eventRepository.save(changedEvent);

        assertThat(actual.eventName()).isEqualTo("更新後のイベント名");
        assertThat(selectEventName(savedEvent.eventId())).contains("更新後のイベント名");
    }

    @Test
    @DisplayName("複数件登録されている場合、findAllは開始日時の昇順で全イベントを返す")
    void givenMultipleEvents_whenFindAll_thenReturnAllEventsOrderedByStartTime() {
        JdbcEventRepository eventRepository = new JdbcEventRepository(new EventDao(jdbcClient));
        Event laterEvent = eventRepository.save(createEvent(null, OffsetDateTime.parse("2026-11-01T18:00:00+09:00")));
        Event earlierEvent = eventRepository.save(createEvent(null, OffsetDateTime.parse("2026-09-01T18:00:00+09:00")));

        List<Event> actual = eventRepository.findAll();

        assertThat(actual).extracting(Event::eventId)
                .containsSubsequence(earlierEvent.eventId(), laterEvent.eventId());
    }

    @Test
    @DisplayName("存在しないeventIdを指定した場合、findByIdは空を返す")
    void givenUnknownEventId_whenFindById_thenReturnEmpty() {
        JdbcEventRepository eventRepository = new JdbcEventRepository(new EventDao(jdbcClient));

        Optional<Event> actual = eventRepository.findById("00000000-0000-0000-0000-000000000000");

        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("deleteByIdを実行すると、対象イベントがDBから削除される")
    void givenExistingEventId_whenDeleteById_thenRemoveEventFromDatabase() {
        JdbcEventRepository eventRepository = new JdbcEventRepository(new EventDao(jdbcClient));
        Event savedEvent = eventRepository.save(createEvent(null));
        assertThat(countEventsById(savedEvent.eventId())).isEqualTo(1);

        eventRepository.deleteById(savedEvent.eventId());

        assertThat(countEventsById(savedEvent.eventId())).isEqualTo(0);
    }

    private Optional<String> selectEventName(String eventId) {
        return jdbcClient.sql("SELECT event_name FROM events WHERE event_id = :eventId")
                .param("eventId", eventId)
                .query(String.class)
                .optional();
    }

    private int countEventsById(String eventId) {
        return jdbcClient.sql("SELECT COUNT(*) FROM events WHERE event_id = :eventId")
                .param("eventId", eventId)
                .query(Integer.class)
                .single();
    }

    private Event createEvent(String eventId) {
        return createEvent(eventId, OffsetDateTime.parse("2026-10-01T18:00:00+09:00"));
    }

    private Event createEvent(String eventId, OffsetDateTime startTime) {
        return new Event(
                eventId,
                VENUE_ID,
                "テスト会場",
                EVENT_CATEGORY_ID,
                EventCategoryName.LIVE,
                "サンプルライブ",
                "サンプルアーティスト",
                "説明文",
                startTime,
                startTime.plusHours(3),
                100,
                0
        );
    }
}
