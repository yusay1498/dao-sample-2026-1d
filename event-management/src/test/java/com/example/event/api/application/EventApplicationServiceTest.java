package com.example.event.api.application;

import com.example.event.api.domain.entity.Event;
import com.example.event.api.domain.entity.EventCategory;
import com.example.event.api.domain.entity.Venue;
import com.example.event.api.domain.entity.vo.EventCategoryName;
import com.example.event.api.domain.exception.EventCategoryNotFoundException;
import com.example.event.api.domain.exception.EventNotFoundException;
import com.example.event.api.domain.exception.VenueNotFoundException;
import com.example.event.api.domain.repository.EventCategoryRepository;
import com.example.event.api.domain.repository.EventRepository;
import com.example.event.api.domain.repository.VenueRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EventApplicationServiceTest {

    private static final String VENUE_ID = "11111111-1111-1111-1111-111111111111";
    private static final String EVENT_CATEGORY_ID = "22222222-2222-2222-2222-222222222222";

    @Test
    @DisplayName("listは登録済みの全イベントを返す")
    void whenList_thenReturnAllEvents() {
        EventRepository eventRepository = mock(EventRepository.class);
        EventApplicationService eventApplicationService = new EventApplicationService(
                eventRepository, mock(VenueRepository.class), mock(EventCategoryRepository.class));
        List<Event> events = List.of(createEvent("event-1"), createEvent("event-2"));
        when(eventRepository.findAll()).thenReturn(events);

        List<Event> actual = eventApplicationService.list();

        assertThat(actual).isEqualTo(events);
    }

    @Test
    @DisplayName("イベントIDが存在する場合、lookupはそのイベントを返す")
    void givenExistingEventId_whenLookup_thenReturnEvent() {
        EventRepository eventRepository = mock(EventRepository.class);
        EventApplicationService eventApplicationService = new EventApplicationService(
                eventRepository, mock(VenueRepository.class), mock(EventCategoryRepository.class));
        Event event = createEvent("event-1");
        when(eventRepository.findById("event-1")).thenReturn(Optional.of(event));

        Event actual = eventApplicationService.lookup("event-1");

        assertThat(actual).isEqualTo(event);
    }

    @Test
    @DisplayName("イベントIDが存在しない場合、lookupはEventNotFoundExceptionをスローする")
    void givenUnknownEventId_whenLookup_thenThrowEventNotFoundException() {
        EventRepository eventRepository = mock(EventRepository.class);
        EventApplicationService eventApplicationService = new EventApplicationService(
                eventRepository, mock(VenueRepository.class), mock(EventCategoryRepository.class));
        when(eventRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventApplicationService.lookup("unknown"))
                .isInstanceOf(EventNotFoundException.class);
    }

    @Test
    @DisplayName("参照する会場・イベント区分がともに存在する場合、createは解決した会場名・区分名を付与して保存する")
    void givenExistingVenueAndEventCategory_whenCreate_thenSaveEventWithResolvedNames() {
        EventRepository eventRepository = mock(EventRepository.class);
        VenueRepository venueRepository = mock(VenueRepository.class);
        EventCategoryRepository eventCategoryRepository = mock(EventCategoryRepository.class);
        EventApplicationService eventApplicationService = new EventApplicationService(
                eventRepository, venueRepository, eventCategoryRepository);
        Event requestedEvent = createUnresolvedEvent(null);
        Event savedEvent = createEvent("event-1");
        when(venueRepository.findById(VENUE_ID)).thenReturn(Optional.of(createVenue()));
        when(eventCategoryRepository.findById(EVENT_CATEGORY_ID)).thenReturn(Optional.of(createEventCategory()));
        when(eventRepository.save(any())).thenReturn(savedEvent);

        Event actual = eventApplicationService.create(requestedEvent);

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(eventRepository).save(eventCaptor.capture());
        assertThat(eventCaptor.getValue().venueName()).isEqualTo("テスト会場");
        assertThat(eventCaptor.getValue().eventCategoryName()).isEqualTo(EventCategoryName.LIVE);
        assertThat(actual).isEqualTo(savedEvent);
    }

    @Test
    @DisplayName("会場が存在しない場合、createはVenueNotFoundExceptionをスローしsaveを呼び出さない")
    void givenUnknownVenueId_whenCreate_thenThrowVenueNotFoundExceptionAndNotSave() {
        EventRepository eventRepository = mock(EventRepository.class);
        VenueRepository venueRepository = mock(VenueRepository.class);
        EventApplicationService eventApplicationService = new EventApplicationService(
                eventRepository, venueRepository, mock(EventCategoryRepository.class));
        Event requestedEvent = createUnresolvedEvent(null);
        when(venueRepository.findById(VENUE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventApplicationService.create(requestedEvent))
                .isInstanceOf(VenueNotFoundException.class);
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("イベント区分が存在しない場合、createはEventCategoryNotFoundExceptionをスローしsaveを呼び出さない")
    void givenUnknownEventCategoryId_whenCreate_thenThrowEventCategoryNotFoundExceptionAndNotSave() {
        EventRepository eventRepository = mock(EventRepository.class);
        VenueRepository venueRepository = mock(VenueRepository.class);
        EventCategoryRepository eventCategoryRepository = mock(EventCategoryRepository.class);
        EventApplicationService eventApplicationService = new EventApplicationService(
                eventRepository, venueRepository, eventCategoryRepository);
        Event requestedEvent = createUnresolvedEvent(null);
        when(venueRepository.findById(VENUE_ID)).thenReturn(Optional.of(createVenue()));
        when(eventCategoryRepository.findById(EVENT_CATEGORY_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventApplicationService.create(requestedEvent))
                .isInstanceOf(EventCategoryNotFoundException.class);
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("存在しないイベントIDに対するupdateはEventNotFoundExceptionをスローする")
    void givenUnknownEventId_whenUpdate_thenThrowEventNotFoundException() {
        EventRepository eventRepository = mock(EventRepository.class);
        EventApplicationService eventApplicationService = new EventApplicationService(
                eventRepository, mock(VenueRepository.class), mock(EventCategoryRepository.class));
        when(eventRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventApplicationService.update("unknown", createUnresolvedEvent(null)))
                .isInstanceOf(EventNotFoundException.class);
    }

    @Test
    @DisplayName("参照する会場・イベント区分がともに存在する場合、updateは解決した会場名・区分名を付与して保存する")
    void givenExistingVenueAndEventCategory_whenUpdate_thenSaveEventWithResolvedNames() {
        EventRepository eventRepository = mock(EventRepository.class);
        VenueRepository venueRepository = mock(VenueRepository.class);
        EventCategoryRepository eventCategoryRepository = mock(EventCategoryRepository.class);
        EventApplicationService eventApplicationService = new EventApplicationService(
                eventRepository, venueRepository, eventCategoryRepository);
        Event existedEvent = createEvent("event-1");
        Event savedEvent = createEvent("event-1");
        when(eventRepository.findById("event-1")).thenReturn(Optional.of(existedEvent));
        when(venueRepository.findById(VENUE_ID)).thenReturn(Optional.of(createVenue()));
        when(eventCategoryRepository.findById(EVENT_CATEGORY_ID)).thenReturn(Optional.of(createEventCategory()));
        when(eventRepository.save(any())).thenReturn(savedEvent);

        Event actual = eventApplicationService.update("event-1", createUnresolvedEvent("event-1"));

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(eventRepository).save(eventCaptor.capture());
        assertThat(eventCaptor.getValue().venueName()).isEqualTo("テスト会場");
        assertThat(eventCaptor.getValue().eventCategoryName()).isEqualTo(EventCategoryName.LIVE);
        assertThat(actual).isEqualTo(savedEvent);
    }

    @Test
    @DisplayName("会場が存在しない場合、updateはVenueNotFoundExceptionをスローしsaveを呼び出さない")
    void givenUnknownVenueId_whenUpdate_thenThrowVenueNotFoundExceptionAndNotSave() {
        EventRepository eventRepository = mock(EventRepository.class);
        VenueRepository venueRepository = mock(VenueRepository.class);
        EventApplicationService eventApplicationService = new EventApplicationService(
                eventRepository, venueRepository, mock(EventCategoryRepository.class));
        Event existedEvent = createEvent("event-1");
        when(eventRepository.findById("event-1")).thenReturn(Optional.of(existedEvent));
        when(venueRepository.findById(VENUE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventApplicationService.update("event-1", createUnresolvedEvent("event-1")))
                .isInstanceOf(VenueNotFoundException.class);
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("イベント区分が存在しない場合、updateはEventCategoryNotFoundExceptionをスローしsaveを呼び出さない")
    void givenUnknownEventCategoryId_whenUpdate_thenThrowEventCategoryNotFoundExceptionAndNotSave() {
        EventRepository eventRepository = mock(EventRepository.class);
        VenueRepository venueRepository = mock(VenueRepository.class);
        EventCategoryRepository eventCategoryRepository = mock(EventCategoryRepository.class);
        EventApplicationService eventApplicationService = new EventApplicationService(
                eventRepository, venueRepository, eventCategoryRepository);
        Event existedEvent = createEvent("event-1");
        when(eventRepository.findById("event-1")).thenReturn(Optional.of(existedEvent));
        when(venueRepository.findById(VENUE_ID)).thenReturn(Optional.of(createVenue()));
        when(eventCategoryRepository.findById(EVENT_CATEGORY_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventApplicationService.update("event-1", createUnresolvedEvent("event-1")))
                .isInstanceOf(EventCategoryNotFoundException.class);
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("イベントIDが存在する場合、deleteはイベントを削除する")
    void givenExistingEventId_whenDelete_thenRemoveEvent() {
        EventRepository eventRepository = mock(EventRepository.class);
        EventApplicationService eventApplicationService = new EventApplicationService(
                eventRepository, mock(VenueRepository.class), mock(EventCategoryRepository.class));
        Event existedEvent = createEvent("event-1");
        when(eventRepository.findById("event-1")).thenReturn(Optional.of(existedEvent));

        eventApplicationService.delete("event-1");

        verify(eventRepository).deleteById("event-1");
    }

    @Test
    @DisplayName("存在しないイベントIDに対するdeleteはEventNotFoundExceptionをスローしdeleteByIdを呼び出さない")
    void givenUnknownEventId_whenDelete_thenThrowEventNotFoundExceptionAndNotDelete() {
        EventRepository eventRepository = mock(EventRepository.class);
        EventApplicationService eventApplicationService = new EventApplicationService(
                eventRepository, mock(VenueRepository.class), mock(EventCategoryRepository.class));
        when(eventRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventApplicationService.delete("unknown"))
                .isInstanceOf(EventNotFoundException.class);
        verify(eventRepository, never()).deleteById(any());
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

    private Event createUnresolvedEvent(String eventId) {
        // Controllerがリクエストボディから組み立てた直後の状態（会場名・区分名は未解決）を再現する
        return new Event(
                eventId,
                VENUE_ID,
                null,
                EVENT_CATEGORY_ID,
                null,
                "サンプルライブ",
                "サンプルアーティスト",
                "説明文",
                OffsetDateTime.parse("2026-10-01T18:00:00+09:00"),
                OffsetDateTime.parse("2026-10-01T21:00:00+09:00"),
                100,
                0
        );
    }

    private Venue createVenue() {
        return new Venue(VENUE_ID, "テスト会場", 300, "東京都渋谷区");
    }

    private EventCategory createEventCategory() {
        return new EventCategory(EVENT_CATEGORY_ID, EventCategoryName.LIVE);
    }
}
