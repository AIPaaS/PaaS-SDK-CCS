package com.ai.paas.ipaas.ccs;

import com.ai.paas.ipaas.ccs.zookeeper.ConfigWatcher;

import java.util.List;

public interface IConfigClient {

    /**
     * @param configPath
     * @param value @
     */
    void add(String configPath, String value);

    /**
     * @param configPath
     * @param bytes @
     */
    void add(String configPath, byte[] bytes);

    /**
     * @param configPath
     * @return @
     */
    String get(String configPath);

    /**
     * @param configPath
     * @param watcher
     * @return @
     */
    String get(String configPath, ConfigWatcher watcher);

    /**
     * @param configPath
     * @return @
     */
    byte[] readBytes(String configPath);

    /**
     * @param configPath
     * @param watcher
     * @return @
     */
    byte[] readBytes(String configPath, ConfigWatcher watcher);

    /**
     * @param configPath
     * @param value @
     */
    void modify(String configPath, String value);

    /**
     * @param configPath
     * @param value @
     */
    void modify(String configPath, byte[] value);

    /**
     * @param configPath
     * @return @
     */
    boolean exists(String configPath);

    /**
     * @param configPath
     * @return @
     */
    boolean exists(String configPath, ConfigWatcher watcher);

    /**
     * @param configPath @
     */
    void remove(String configPath);

    /**
     * @param configPath @
     */
    List<String> listSubPath(String configPath);

    /**
     * @param configPath
     * @param watcher @
     */
    List<String> listSubPath(String configPath, ConfigWatcher watcher);

}
