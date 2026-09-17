package com.example.event.api.domain.entity;

import com.example.event.api.domain.entity.vo.EventCategoryName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventTest {

    private static final String VENUE_ID = "11111111-1111-1111-1111-111111111111";
    private static final String EVENT_CATEGORY_ID = "22222222-2222-2222-2222-222222222222";
    private static final OffsetDateTime START_TIME = OffsetDateTime.parse("2026-10-01T18:00:00+09:00");
    private static final OffsetDateTime END_TIME = OffsetDateTime.parse("2026-10-01T21:00:00+09:00");

    @Test
    @DisplayName("終了日時が開始日時より後の場合、コンストラクタは正常にインスタンスを生成する")
    void givenEndTimeAfterStartTime_whenConstruct_thenCreateInstance() {
        Event actual = createEvent(START_TIME, END_TIME, 100, 0);

        assertThat(actual.startTime()).isEqualTo(START_TIME);
        assertThat(actual.endTime()).isEqualTo(END_TIME);
    }

    @Test
    @DisplayName("終了日時が開始日時より前の場合、コンストラクタはIllegalArgumentExceptionをスローする")
    void givenEndTimeBeforeStartTime_whenConstruct_thenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> createEvent(START_TIME, START_TIME.minusMinutes(1), 100, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("購入済席数が販売可能席数と同数の場合、コンストラクタは正常にインスタンスを生成する")
    void givenReservedSeatsEqualToAvailableSeats_whenConstruct_thenCreateInstance() {
        Event actual = createEvent(START_TIME, END_TIME, 100, 100);

        assertThat(actual.reservedSeats()).isEqualTo(100);
    }

    @Test
    @DisplayName("購入済席数が販売可能席数を超過する場合、コンストラクタはIllegalArgumentExceptionをスローする")
    void givenReservedSeatsExceedingAvailableSeats_whenConstruct_thenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> createEvent(START_TIME, END_TIME, 100, 101))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private Event createEvent(OffsetDateTime startTime, OffsetDateTime endTime, int availableSeats, int reservedSeats) {
        return new Event(
                "33333333-3333-3333-3333-333333333333",
                VENUE_ID,
                "テスト会場",
                EVENT_CATEGORY_ID,
                EventCategoryName.LIVE,
                "サンプルライブ",
                "サンプルアーティスト",
                "説明文",
                startTime,
                endTime,
                availableSeats,
                reservedSeats
        );
    }
}
