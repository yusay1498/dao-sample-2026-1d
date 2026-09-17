package com.example.event.api.presentation;

import com.example.event.api.application.EventApplicationService;
import com.example.event.api.domain.entity.Event;
import com.example.event.api.domain.exception.IllegalPropertyException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.hibernate.validator.constraints.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Objects;

@Validated
@RestController
@RequestMapping("/events")
public class EventRestController {

    private final EventApplicationService eventApplicationService;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    public EventRestController(
            EventApplicationService eventApplicationService,
            ObjectMapper objectMapper,
            Validator validator
    ) {
        this.eventApplicationService = eventApplicationService;
        this.objectMapper = objectMapper;
        this.validator = validator;
    }

    @GetMapping
    public ResponseEntity<List<Event>> get() {
        return ResponseEntity.ok(eventApplicationService.list());
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<Event> get(@PathVariable("eventId") @UUID String eventId) {
        return ResponseEntity.ok(eventApplicationService.lookup(eventId));
    }

    @PostMapping
    public ResponseEntity<Event> post(@RequestBody @Valid Event event) {
        Event createdEvent = eventApplicationService.create(event);

        return ResponseEntity
                .created(ServletUriComponentsBuilder.fromPath("/events/{eventId}")
                        .buildAndExpand(createdEvent.eventId())
                        .toUri())
                .body(createdEvent);
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<Event> put(
            @PathVariable("eventId") @UUID String eventId,
            @RequestBody @Valid Event event
    ) {
        if (!Objects.equals(eventId, event.eventId())) {
            throw new IllegalPropertyException("eventIdはURLパスと一致させてください。", "eventId", event.eventId());
        }

        return ResponseEntity.ok(eventApplicationService.update(eventId, event));
    }

    @PatchMapping(value = "/{eventId}", consumes = "application/merge-patch+json")
    public ResponseEntity<Event> patch(
            @PathVariable("eventId") @UUID String eventId,
            @RequestBody String patchJson
    ) {
        Event existedEvent = eventApplicationService.lookup(eventId);

        Event mergedEvent;
        try {
            mergedEvent = new ValidatableObjectReader(objectMapper.readerForUpdating(existedEvent), validator)
                    .readValue(patchJson);
        } catch (JacksonException e) {
            // patchJsonにはリクエスト本文全体（機密情報や巨大な値を含みうる）が入るため、valueには反映しない
            throw new IllegalPropertyException("不正なリクエストボディです。", e, "patchJson", null);
        }

        if (!Objects.equals(eventId, mergedEvent.eventId())) {
            throw new IllegalPropertyException("eventIdは変更できません。", "eventId", mergedEvent.eventId());
        }

        return ResponseEntity.ok(eventApplicationService.update(eventId, mergedEvent));
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> delete(@PathVariable("eventId") @UUID String eventId) {
        eventApplicationService.delete(eventId);
        return ResponseEntity.noContent().build();
    }
}
