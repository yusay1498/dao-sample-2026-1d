package com.example.event.api.domain.entity.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventCategoryNameTest {

    @Test
    @DisplayName("getDisplayNameは列挙子に対応する表示名を返す")
    void whenGetDisplayName_thenReturnCorrespondingDisplayName() {
        assertThat(EventCategoryName.LIVE.getDisplayName()).isEqualTo("ライブ");
    }

    @Test
    @DisplayName("DBに格納された表示名を指定した場合、ofは対応する列挙子を返す")
    void givenKnownDisplayName_whenOf_thenReturnMatchingEnumConstant() {
        EventCategoryName actual = EventCategoryName.of("ライブ");

        assertThat(actual).isEqualTo(EventCategoryName.LIVE);
    }

    @Test
    @DisplayName("未知の表示名を指定した場合、ofはIllegalArgumentExceptionをスローする")
    void givenUnknownDisplayName_whenOf_thenThrowIllegalArgumentException() {
        assertThatThrownBy(() -> EventCategoryName.of("存在しない区分"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
