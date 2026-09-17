package com.example.event.api.presentation;

import com.example.event.api.domain.entity.Event;
import com.example.event.api.domain.entity.vo.EventCategoryName;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValidatableObjectReaderTest {

    private static final String EVENT_ID = "33333333-3333-3333-3333-333333333333";
    private static final String VENUE_ID = "11111111-1111-1111-1111-111111111111";
    private static final String EVENT_CATEGORY_ID = "22222222-2222-2222-2222-222222222222";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    @DisplayName("マージ後の値が制約を満たす場合、readValueは反映済みのインスタンスを返す")
    void givenValidMergePatch_whenReadValue_thenReturnMergedInstance() {
        ValidatableObjectReader reader = createReader(createEvent());

        Event actual = reader.readValue("""
                {"eventName": "改訂版イベント名"}
                """);

        assertThat(actual.eventName()).isEqualTo("改訂版イベント名");
    }

    @Test
    @DisplayName("マージ後の値が制約に違反する場合、readValueはConstraintViolationExceptionをスローする")
    void givenInvalidMergePatch_whenReadValue_thenThrowConstraintViolationException() {
        ValidatableObjectReader reader = createReader(createEvent());

        assertThatThrownBy(() -> reader.readValue("""
                {"eventName": ""}
                """))
                .isInstanceOf(ConstraintViolationException.class);
    }

    private ValidatableObjectReader createReader(Event base) {
        ObjectReader baseReader = objectMapper.readerForUpdating(base);
        return new ValidatableObjectReader(baseReader, validator);
    }

    private Event createEvent() {
        return new Event(
                EVENT_ID,
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
