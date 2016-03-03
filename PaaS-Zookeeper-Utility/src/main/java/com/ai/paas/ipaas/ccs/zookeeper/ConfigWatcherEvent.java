package com.ai.paas.ipaas.ccs.zookeeper;

import org.apache.zookeeper.WatchedEvent;

import com.ai.paas.ipaas.ccs.zookeeper.ConfigWatcher.Event.EventType;

/**
 * Created by astraea on 2015/4/28.
 */
public class ConfigWatcherEvent {
    private ConfigWatcher.Event.KeeperState keeperState;
    private ConfigWatcher.Event.EventType eventType;
    private String path;
    private WatchedEvent event;

    public ConfigWatcherEvent(ConfigWatcher.Event.EventType eventType, ConfigWatcher.Event.KeeperState keeperState, String path) {
        this.keeperState = keeperState;
        this.eventType = eventType;
        this.path = path;
    }

    public ConfigWatcherEvent(WatchedEvent event) {
        this.event = event;
        this.keeperState = ConfigWatcher.Event.KeeperState.fromInt(event.getState().getIntValue());
        this.eventType = ConfigWatcher.Event.EventType.fromInt(event.getType().getIntValue());
        this.path = event.getPath();
    }

	public ConfigWatcher.Event.KeeperState getState() {
        return ConfigWatcher.Event.KeeperState.fromInt(event.getState().getIntValue());
    }

    public ConfigWatcher.Event.EventType getType() {
        return ConfigWatcher.Event.EventType.fromInt(event.getType().getIntValue());
    }

    public String getPath() {
        return event.getPath();
    }

    public ConfigWatcherEvent getWrapper() {
        return new ConfigWatcherEvent(ConfigWatcher.Event.EventType.fromInt(event.getType().getIntValue()),
                ConfigWatcher.Event.KeeperState.fromInt(event.getState().getIntValue()), event.getPath());
    }
}
