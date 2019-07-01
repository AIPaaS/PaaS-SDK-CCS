package com.ai.paas.ipaas.ccs.inner;

import com.ai.paas.ipaas.ccs.constants.AddMode;
import com.ai.paas.ipaas.ccs.inner.constants.ConfigPathMode;
import com.ai.paas.ipaas.ccs.zookeeper.ConfigWatcher;
import com.ai.paas.ipaas.ccs.zookeeper.MutexLock;

import java.util.List;

public interface ICCSComponent {

    /**
     * @param path
     * @return @
     */
    void add(String path, String value);

    /**
     * @param path
     * @param value
     * @param addMode
     * @return @
     */
    void add(String path, String value, AddMode addMode);

    /**
     * @param path
     * @param value
     * @return @
     */
    void add(String path, byte[] value);

    /**
     * @param path
     * @param value
     * @param addMode
     * @return @
     */
    void add(String path, byte[] value, AddMode addMode);

    /**
     * @param path
     * @param watcher
     * @return @
     */
    String get(String path, ConfigWatcher watcher);

    /**
     * @param path
     * @param pathMode
     * @param watcher
     * @return @
     */
    String get(String path, ConfigPathMode pathMode, ConfigWatcher watcher);

    /**
     * @param path
     * @return
     * @throws PaasRuntimeException
     */
    MutexLock getMutexLock(String path);

    /**
     * @param path @
     */
    void modify(String path, String value);

    /**
     * @param path
     * @param value @
     */
    void modify(String path, byte[] value);

    /**
     * @param path
     * @param pathMode
     * @param watcher
     * @return @
     */
    List<String> listSubPath(String path, ConfigPathMode pathMode, ConfigWatcher watcher);

    /**
     * @param path
     * @param pathMode
     * @return @
     */
    List<String> listSubPath(String path, ConfigPathMode pathMode);

    /**
     * @param path
     * @return
     */
    List<String> listSubPath(String path);

    /**
     * @param path
     * @param watcher
     * @return @
     */
    List<String> listSubPath(String path, ConfigWatcher watcher);

    /**
     * @param path
     * @throws PaasRuntimeException
     */
    void remove(String path);

    /**
     * @param path
     * @throws PaasException
     */
    String get(String path);

    /**
     * @param path
     * @param pathMode
     * @throws PaasException
     */
    String get(String path, ConfigPathMode pathMode);

    /**
     * @param path
     * @return @
     */
    byte[] readBytes(String path);

    /**
     * @param path
     * @param pathMode
     * @return @
     */
    byte[] readBytes(String path, ConfigPathMode pathMode);

    /**
     * @param path
     * @param watcher
     * @return @
     */
    byte[] readBytes(String path, ConfigWatcher watcher);

    /**
     * @param path
     * @param pathMode
     * @param watcher
     * @return @
     */
    byte[] readBytes(String path, ConfigPathMode pathMode, ConfigWatcher watcher);

    /**
     * @param path
     * @param pathMode
     * @return @
     */
    boolean exists(String path, ConfigPathMode pathMode);

    /**
     * @param path
     * @param pathMode
     * @return @
     */
    boolean exists(String path, ConfigWatcher watcher);

    /**
     * @param path
     * @return @
     */
    boolean exists(String path);

    /**
     * 
     * @param path
     * @param pathMode
     * @param watcher
     * @return @
     */
    boolean exists(String path, ConfigPathMode pathMode, ConfigWatcher watcher);
}
