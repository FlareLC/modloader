package dev.flarelc.api.events.impl;

import dev.flarelc.api.events.Event;

public class EventKeyType extends Event {
    private final int key;
    public EventKeyType(int key) {
        this.key = key;
    }
    public int getKey() {
        return key;
    }

}
