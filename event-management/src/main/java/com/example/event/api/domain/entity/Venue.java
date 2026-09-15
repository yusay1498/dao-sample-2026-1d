package com.example.event.api.domain.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;

public record Venue(
        @Id @NotBlank @Size(max = 36) String venueId,
        @NotBlank @Size(max = 50) String venueName,
        @NotNull @Positive Integer capacity,
        @NotBlank @Size(max = 100) String address
) {
}
