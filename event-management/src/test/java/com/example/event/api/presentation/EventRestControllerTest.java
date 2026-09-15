package com.example.event.api.presentation;

import com.example.event.api.application.EventApplicationService;
import com.example.event.api.domain.entity.Event;
import com.example.event.api.domain.entity.vo.EventCategoryName;
import com.example.event.api.domain.exception.EventNotFoundException;
import com.example.event.api.domain.exception.VenueNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventRestController.class)
class EventRestControllerTest {

    private static final String EVENT_ID = "33333333-3333-3333-3333-333333333333";
    private static final String VENUE_ID = "11111111-1111-1111-1111-111111111111";
    private static final String EVENT_CATEGORY_ID = "22222222-2222-2222-2222-222222222222";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    EventApplicationService eventApplicationService;

    @Test
    @DisplayName("存在するイベントIDを指定した場合、GET /events/{eventId}は200と本文を返す")
    void givenExistingEventId_whenGet_thenReturnOkWithEvent() throws Exception {
        when(eventApplicationService.lookup(EVENT_ID)).thenReturn(createEvent(EVENT_ID));

        mockMvc.perform(get("/events/{eventId}", EVENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value(EVENT_ID))
                .andExpect(jsonPath("$.venueName").value("テスト会場"));
    }

    @Test
    @DisplayName("存在しないイベントIDを指定した場合、GET /events/{eventId}は404を返す")
    void givenUnknownEventId_whenGet_thenReturnNotFound() throws Exception {
        when(eventApplicationService.lookup(EVENT_ID)).thenThrow(new EventNotFoundException(EVENT_ID));

        mockMvc.perform(get("/events/{eventId}", EVENT_ID))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.detail").exists());
    }

    @Test
    @DisplayName("有効なリクエストボディでPOST /eventsすると、201とLocationヘッダを返す")
    void givenValidRequestBody_whenPost_thenReturnCreatedWithLocationHeader() throws Exception {
        when(eventApplicationService.create(any())).thenReturn(createEvent(EVENT_ID));

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUnresolvedEvent())))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/events/" + EVENT_ID));
    }

    @Test
    @DisplayName("必須項目が欠けたリクエストボディでPOST /eventsすると、400を返す")
    void givenInvalidRequestBody_whenPost_thenReturnBadRequest() throws Exception {
        String invalidRequestBody = """
                {"venueId": "", "eventCategoryId": "%s"}
                """.formatted(EVENT_CATEGORY_ID);

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("存在しない会場を参照するPOST /eventsは400を返す")
    void givenUnknownVenueId_whenPost_thenReturnBadRequest() throws Exception {
        when(eventApplicationService.create(any())).thenThrow(new VenueNotFoundException(VENUE_ID));

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUnresolvedEvent())))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("application/merge-patch+jsonでPATCH /events/{eventId}すると、指定したフィールドのみが反映される")
    void givenMergePatchWithSingleField_whenPatch_thenUpdateOnlySpecifiedField() throws Exception {
        Event existedEvent = createEvent(EVENT_ID);
        Event updatedEvent = new Event(
                EVENT_ID, VENUE_ID, "テスト会場", EVENT_CATEGORY_ID, EventCategoryName.LIVE,
                "改訂版イベント名", existedEvent.performer(), existedEvent.description(),
                existedEvent.startTime(), existedEvent.endTime(),
                existedEvent.availableSeats(), existedEvent.reservedSeats()
        );
        when(eventApplicationService.lookup(EVENT_ID)).thenReturn(existedEvent);
        when(eventApplicationService.update(eq(EVENT_ID), any())).thenReturn(updatedEvent);

        mockMvc.perform(patch("/events/{eventId}", EVENT_ID)
                        .contentType("application/merge-patch+json")
                        .content("""
                                {"eventName": "改訂版イベント名"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventName").value("改訂版イベント名"));

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(eventApplicationService).update(eq(EVENT_ID), eventCaptor.capture());
        assertThat(eventCaptor.getValue().performer()).isEqualTo(existedEvent.performer());
    }

    @Test
    @DisplayName("マージ後の値が不正であるPATCH /events/{eventId}は400を返す")
    void givenInvalidMergedValue_whenPatch_thenReturnBadRequest() throws Exception {
        Event existedEvent = createEvent(EVENT_ID);
        when(eventApplicationService.lookup(EVENT_ID)).thenReturn(existedEvent);

        mockMvc.perform(patch("/events/{eventId}", EVENT_ID)
                        .contentType("application/merge-patch+json")
                        .content("""
                                {"reservedSeats": 9999}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /events/{eventId}を実行すると、204を返しdeleteが呼び出される")
    void givenExistingEventId_whenDelete_thenReturnNoContentAndCallDelete() throws Exception {
        mockMvc.perform(delete("/events/{eventId}", EVENT_ID))
                .andExpect(status().isNoContent());

        verify(eventApplicationService).delete(EVENT_ID);
    }

    private Event createUnresolvedEvent() {
        // Controllerに届く直前のリクエストボディ（会場名・区分名は未指定）を再現する
        return new Event(
                null,
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
