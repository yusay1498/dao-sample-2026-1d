package com.example.event.api.domain.entity.vo;

import java.util.Arrays;
import java.util.Objects;

public enum EventCategoryName {
    LIVE("ライブ"),
    FES("フェス"),
    STAGE("舞台"),
    COMEDY("コメディ"),
    TALK_EVENT("トークイベント");

    private final String displayName;

    EventCategoryName(String name) {
        this.displayName = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    // DB(event_category_name列)は表示名で保持されているため、列挙子名ではなく表示名から変換する
    public static EventCategoryName of(String displayName) {
        return Arrays.stream(values())
                .filter(eventCategoryName -> Objects.equals(eventCategoryName.displayName, displayName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown event category name: " + displayName));
    }
}
