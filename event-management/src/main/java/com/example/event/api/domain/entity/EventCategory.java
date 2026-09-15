package com.example.event.api.domain.entity;

import com.example.event.api.domain.entity.vo.EventCategoryName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;

public record EventCategory(
        @Id @NotBlank @Size(max = 36) String eventCategoryId,
        @NotNull EventCategoryName eventCategoryName
) {
}
