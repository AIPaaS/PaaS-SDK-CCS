package com.ai.paas.ipaas.ccs.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

/**
 * Created by astraea on 2015/4/28.
 */
public abstract class ConfigWatcher implements Watcher {
    public abstract void processEvent(ConfigWatcherEvent event);

    @Override
    public void process(WatchedEvent event) {
        processEvent(new ConfigWatcherEvent(event));
    }

    public interface Event {
        public enum EventType {
            None(-1),
            NodeCreated(1),
            NodeDeleted(2),
            NodeDataChanged(3),
            NodeChildrenChanged(4);

            private final int intValue;

            EventType(int intValue) {
                this.intValue = intValue;
            }

            public int getIntValue() {
                return intValue;
            }

            public static EventType fromInt(int intValue) {
                switch (intValue) {
                    case -1:
                        return EventType.None;
                    case 1:
                        return EventType.NodeCreated;
                    case 2:
                        return EventType.NodeDeleted;
                    case 3:
                        return EventType.NodeDataChanged;
                    case 4:
                        return EventType.NodeChildrenChanged;

                    default:
                        throw new RuntimeException("Invalid integer value for conversion to EventType");
                }
            }
        }

        public enum KeeperState {
            Disconnected(0), SyncConnected(3), AuthFailed(4), ConnectedReadOnly(5), SaslAuthenticated(6), Expired(-112), ;

            private final int intValue;

            KeeperState(int intValue) {
                this.intValue = intValue;
            }

            public int getIntValue() {
                return intValue;
            }

            public static KeeperState fromInt(int intValue) {
                switch (intValue) {
                    case 0:
                        return KeeperState.Disconnected;
                    case 3:
                        return KeeperState.SyncConnected;
                    case 4:
                        return KeeperState.AuthFailed;
                    case 5:
                        return KeeperState.ConnectedReadOnly;
                    case 6:
                        return KeeperState.SaslAuthenticated;
                    case -112:
                        return KeeperState.Expired;

                    default:
                        throw new RuntimeException("Invalid integer value for conversion to KeeperState");
                }
            }
        }
    }
}
