package com.example.event.api.domain.entity.vo;

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
}
