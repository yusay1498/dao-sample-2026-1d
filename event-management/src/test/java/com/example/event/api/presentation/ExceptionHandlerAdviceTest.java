package com.example.event.api.presentation;

import com.example.event.api.domain.exception.EventCategoryNotFoundException;
import com.example.event.api.domain.exception.EventNotFoundException;
import com.example.event.api.domain.exception.IllegalPropertyException;
import com.example.event.api.domain.exception.VenueNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExceptionHandlerAdviceTest {

    private final ExceptionHandlerAdvice exceptionHandlerAdvice = new ExceptionHandlerAdvice();

    @Test
    @DisplayName("EventNotFoundExceptionを処理すると、404とメッセージを含むProblemDetailを返す")
    void givenEventNotFoundException_whenHandle_thenReturnNotFoundProblemDetail() {
        ResponseEntity<ProblemDetail> actual =
                exceptionHandlerAdvice.handleEventNotFoundException(new EventNotFoundException("event-1"));

        assertThat(actual.getStatusCode().value()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(actual.getBody().getDetail()).contains("event-1");
    }

    @Test
    @DisplayName("VenueNotFoundExceptionを処理すると、404ではなく400のProblemDetailを返す")
    void givenVenueNotFoundException_whenHandle_thenReturnBadRequestProblemDetail() {
        ResponseEntity<ProblemDetail> actual =
                exceptionHandlerAdvice.handleReferenceNotFoundException(new VenueNotFoundException("venue-1"));

        assertThat(actual.getStatusCode().value()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("EventCategoryNotFoundExceptionを処理すると、400のProblemDetailを返す")
    void givenEventCategoryNotFoundException_whenHandle_thenReturnBadRequestProblemDetail() {
        ResponseEntity<ProblemDetail> actual = exceptionHandlerAdvice
                .handleReferenceNotFoundException(new EventCategoryNotFoundException("category-1"));

        assertThat(actual.getStatusCode().value()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("valueを伴うIllegalPropertyExceptionを処理すると、name/valueを含む400のProblemDetailを返す")
    void givenIllegalPropertyExceptionWithValue_whenHandle_thenReturnBadRequestProblemDetailWithNameAndValue() {
        ResponseEntity<ProblemDetail> actual = exceptionHandlerAdvice.handleIllegalPropertyException(
                new IllegalPropertyException("eventIdはURLパスと一致させてください。", "eventId", "event-1"));

        assertThat(actual.getStatusCode().value()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(actual.getBody().getProperties()).containsEntry("name", "eventId");
        assertThat(actual.getBody().getProperties()).containsEntry("value", "event-1");
    }

    @Test
    @DisplayName("valueがnullのIllegalPropertyExceptionを処理すると、valueを含まない400のProblemDetailを返す")
    void givenIllegalPropertyExceptionWithNullValue_whenHandle_thenReturnBadRequestProblemDetailWithoutValue() {
        ResponseEntity<ProblemDetail> actual = exceptionHandlerAdvice.handleIllegalPropertyException(
                new IllegalPropertyException("不正なリクエストボディです。", "patchJson", null));

        assertThat(actual.getStatusCode().value()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(actual.getBody().getProperties()).containsEntry("name", "patchJson");
        assertThat(actual.getBody().getProperties()).doesNotContainKey("value");
    }

    @Test
    @DisplayName("IllegalArgumentExceptionを処理すると、400のProblemDetailを返す")
    void givenIllegalArgumentException_whenHandle_thenReturnBadRequestProblemDetail() {
        ResponseEntity<ProblemDetail> actual =
                exceptionHandlerAdvice.handleIllegalArgumentException(new IllegalArgumentException("不正な値です。"));

        assertThat(actual.getStatusCode().value()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(actual.getBody().getDetail()).isEqualTo("不正な値です。");
    }

    @Test
    @DisplayName("MethodArgumentNotValidExceptionを処理すると、フィールドエラー一覧を含む400のProblemDetailを返す")
    void givenMethodArgumentNotValidException_whenHandle_thenReturnBadRequestProblemDetailWithFieldErrors() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("event", "eventName", "空にできません。");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(exception.getMessage()).thenReturn("バリデーションエラー");

        ResponseEntity<ProblemDetail> actual = exceptionHandlerAdvice.handleMethodArgumentNotValidException(exception);

        assertThat(actual.getStatusCode().value()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat((List<String>) actual.getBody().getProperties().get("errors"))
                .containsExactly("eventName: 空にできません。");
    }

    @Test
    @DisplayName("ConstraintViolationExceptionを処理すると、違反内容一覧を含む400のProblemDetailを返す")
    void givenConstraintViolationException_whenHandle_thenReturnBadRequestProblemDetailWithViolations() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        jakarta.validation.Path path = mock(jakarta.validation.Path.class);
        when(path.toString()).thenReturn("eventId");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("形式が不正です。");
        ConstraintViolationException exception = new ConstraintViolationException(Set.of(violation));

        ResponseEntity<ProblemDetail> actual = exceptionHandlerAdvice.handleConstraintViolationException(exception);

        assertThat(actual.getStatusCode().value()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat((List<String>) actual.getBody().getProperties().get("errors"))
                .containsExactly("eventId: 形式が不正です。");
    }
}
