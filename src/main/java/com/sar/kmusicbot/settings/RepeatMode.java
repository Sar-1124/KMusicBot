package com.sar.kmusicbot.settings;

public enum RepeatMode {
    TRACK("track"),
    QUEUE("queue"),
    OFF("off");

    private final String name;

    RepeatMode(String name) {
        this.name = name();
    }

    public String getName() {
        return name;
    }
}
