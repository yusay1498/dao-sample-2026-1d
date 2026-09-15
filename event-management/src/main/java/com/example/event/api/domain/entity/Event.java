package com.example.event.api.domain.entity;

import jakarta.validation.constraints.*;

import java.time.OffsetDateTime;

public record Event(
        String eventId,
        @NotBlank @Size(max = 36) String venueId,
        @NotBlank @Size(max = 36) String eventCategoryId,
        @NotBlank @Size(max = 100) String eventName,
        @NotBlank @Size(max = 100) String performer,
        @NotBlank @Size(max = 1500) String description,
        @NotNull OffsetDateTime startTime,
        @NotNull OffsetDateTime endTime,
        @NotNull @Positive Integer availableSeats,
        @NotNull @PositiveOrZero Integer reservedSeats
) {

    public Event {
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("終了日時は開始日時よりも前である必要があります。");
        }
        if (reservedSeats > availableSeats) {
            throw new IllegalArgumentException("購入済席数が販売可能席数を超過しています。");
        }
    }
}
