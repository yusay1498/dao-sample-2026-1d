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

    @Test
    @DisplayName("listは登録済みの全イベントを返す")
    void whenList_thenReturnAllEvents() {
        EventRepository eventRepository = mock(EventRepository.class);
        EventApplicationService eventApplicationService = new EventApplicationService(
                eventRepository, mock(VenueRepository.class), mock(EventCategoryRepository.class));
        List<Event> events = List.of(
                createEvent("event-1", "11111111-1111-1111-1111-111111111111", "22222222-2222-2222-2222-222222222222"),
                createEvent("event-2", "11111111-1111-1111-1111-111111111111", "22222222-2222-2222-2222-222222222222"));
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
        Event event = createEvent("event-1", "11111111-1111-1111-1111-111111111111", "22222222-2222-2222-2222-222222222222");
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
        String venueId = "11111111-1111-1111-1111-111111111111";
        String eventCategoryId = "22222222-2222-2222-2222-222222222222";
        Event requestedEvent = createUnresolvedEvent(null, venueId, eventCategoryId);
        Event savedEvent = createEvent("event-1", venueId, eventCategoryId);
        when(venueRepository.findById(venueId)).thenReturn(Optional.of(createVenue(venueId)));
        when(eventCategoryRepository.findById(eventCategoryId)).thenReturn(Optional.of(createEventCategory(eventCategoryId)));
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
        String venueId = "11111111-1111-1111-1111-111111111111";
        String eventCategoryId = "22222222-2222-2222-2222-222222222222";
        Event requestedEvent = createUnresolvedEvent(null, venueId, eventCategoryId);
        when(venueRepository.findById(venueId)).thenReturn(Optional.empty());

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
        String venueId = "11111111-1111-1111-1111-111111111111";
        String eventCategoryId = "22222222-2222-2222-2222-222222222222";
        Event requestedEvent = createUnresolvedEvent(null, venueId, eventCategoryId);
        when(venueRepository.findById(venueId)).thenReturn(Optional.of(createVenue(venueId)));
        when(eventCategoryRepository.findById(eventCategoryId)).thenReturn(Optional.empty());

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

        assertThatThrownBy(() -> eventApplicationService.update("unknown",
                createUnresolvedEvent(null, "11111111-1111-1111-1111-111111111111", "22222222-2222-2222-2222-222222222222")))
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
        String venueId = "11111111-1111-1111-1111-111111111111";
        String eventCategoryId = "22222222-2222-2222-2222-222222222222";
        Event existedEvent = createEvent("event-1", venueId, eventCategoryId);
        Event savedEvent = createEvent("event-1", venueId, eventCategoryId);
        when(eventRepository.findById("event-1")).thenReturn(Optional.of(existedEvent));
        when(venueRepository.findById(venueId)).thenReturn(Optional.of(createVenue(venueId)));
        when(eventCategoryRepository.findById(eventCategoryId)).thenReturn(Optional.of(createEventCategory(eventCategoryId)));
        when(eventRepository.save(any())).thenReturn(savedEvent);

        Event actual = eventApplicationService.update("event-1", createUnresolvedEvent("event-1", venueId, eventCategoryId));

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
        String venueId = "11111111-1111-1111-1111-111111111111";
        String eventCategoryId = "22222222-2222-2222-2222-222222222222";
        Event existedEvent = createEvent("event-1", venueId, eventCategoryId);
        when(eventRepository.findById("event-1")).thenReturn(Optional.of(existedEvent));
        when(venueRepository.findById(venueId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventApplicationService.update("event-1", createUnresolvedEvent("event-1", venueId, eventCategoryId)))
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
        String venueId = "11111111-1111-1111-1111-111111111111";
        String eventCategoryId = "22222222-2222-2222-2222-222222222222";
        Event existedEvent = createEvent("event-1", venueId, eventCategoryId);
        when(eventRepository.findById("event-1")).thenReturn(Optional.of(existedEvent));
        when(venueRepository.findById(venueId)).thenReturn(Optional.of(createVenue(venueId)));
        when(eventCategoryRepository.findById(eventCategoryId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventApplicationService.update("event-1", createUnresolvedEvent("event-1", venueId, eventCategoryId)))
                .isInstanceOf(EventCategoryNotFoundException.class);
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("イベントIDが存在する場合、deleteはイベントを削除する")
    void givenExistingEventId_whenDelete_thenRemoveEvent() {
        EventRepository eventRepository = mock(EventRepository.class);
        EventApplicationService eventApplicationService = new EventApplicationService(
                eventRepository, mock(VenueRepository.class), mock(EventCategoryRepository.class));
        Event existedEvent = createEvent("event-1", "11111111-1111-1111-1111-111111111111", "22222222-2222-2222-2222-222222222222");
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

    private Event createEvent(String eventId, String venueId, String eventCategoryId) {
        return new Event(
                eventId,
                venueId,
                "テスト会場",
                eventCategoryId,
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

    private Event createUnresolvedEvent(String eventId, String venueId, String eventCategoryId) {
        // Controllerがリクエストボディから組み立てた直後の状態（会場名・区分名は未解決）を再現する
        return new Event(
                eventId,
                venueId,
                null,
                eventCategoryId,
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

    private Venue createVenue(String venueId) {
        return new Venue(venueId, "テスト会場", 300, "東京都渋谷区");
    }

    private EventCategory createEventCategory(String eventCategoryId) {
        return new EventCategory(eventCategoryId, EventCategoryName.LIVE);
    }
}
