package com.ai.paas.ipaas.ccs.inner;

import com.ai.paas.ipaas.PaasException;
import com.ai.paas.ipaas.PaasRuntimeException;
import com.ai.paas.ipaas.ccs.constants.AddMode;
import com.ai.paas.ipaas.ccs.constants.ConfigException;
import com.ai.paas.ipaas.ccs.inner.constants.ConfigPathMode;
import com.ai.paas.ipaas.ccs.zookeeper.ConfigWatcher;
import com.ai.paas.ipaas.ccs.zookeeper.MutexLock;

import java.util.List;

public interface ICCSComponent {

    /**
     * @param path
     * @return
     * @throws ConfigException
     */
    void add(String path, String value) throws ConfigException;

    /**
     * @param path
     * @param value
     * @param addMode
     * @return
     * @throws ConfigException
     */
    void add(String path, String value, AddMode addMode) throws ConfigException;


    /**
     * @param path
     * @param value
     * @return
     * @throws ConfigException
     */
    void add(String path, byte[] value) throws ConfigException;

    /**
     * @param path
     * @param value
     * @param addMode
     * @return
     * @throws ConfigException
     */
    void add(String path, byte[] value, AddMode addMode) throws ConfigException;

    /**
     * @param path
     * @param watcher
     * @return
     * @throws ConfigException
     */
    String get(String path, ConfigWatcher watcher) throws ConfigException;

    /**
     * @param path
     * @param pathMode
     * @param watcher
     * @return
     * @throws ConfigException
     */
    String get(String path, ConfigPathMode pathMode, ConfigWatcher watcher) throws ConfigException;

    /**
     * @param path
     * @return
     * @throws PaasRuntimeException
     */
    MutexLock getMutexLock(String path) throws ConfigException;


    /**
     * @param path
     * @throws ConfigException
     */
    void modify(String path, String value) throws ConfigException;


    /**
     * @param path
     * @param value
     * @throws ConfigException
     */
    void modify(String path, byte[] value) throws ConfigException;

    /**
     * @param path
     * @param pathMode
     * @param watcher
     * @return
     * @throws ConfigException
     */
    List<String> listSubPath(String path, ConfigPathMode pathMode, ConfigWatcher watcher) throws ConfigException;

    /**
     * @param path
     * @param pathMode
     * @return
     * @throws ConfigException
     */
    List<String> listSubPath(String path, ConfigPathMode pathMode) throws ConfigException;

    /**
     * @param path
     * @return
     */
    List<String> listSubPath(String path) throws ConfigException;

    /**
     * @param path
     * @param watcher
     * @return
     * @throws ConfigException
     */
    List<String> listSubPath(String path, ConfigWatcher watcher) throws ConfigException;


    /**
     * @param path
     * @throws PaasRuntimeException
     */
    void remove(String path) throws ConfigException;

    /**
     * @param path
     * @throws PaasException
     */
    String get(String path) throws ConfigException;

    /**
     * @param path
     * @param pathMode
     * @throws PaasException
     */
    String get(String path, ConfigPathMode pathMode) throws ConfigException;

    /**
     * @param path
     * @return
     * @throws ConfigException
     */
    byte[] readBytes(String path) throws ConfigException;

    /**
     * @param path
     * @param pathMode
     * @return
     * @throws ConfigException
     */
    byte[] readBytes(String path, ConfigPathMode pathMode) throws ConfigException;

    /**
     * @param path
     * @param watcher
     * @return
     * @throws ConfigException
     */
    byte[] readBytes(String path, ConfigWatcher watcher) throws ConfigException;

    /**
     * @param path
     * @param pathMode
     * @param watcher
     * @return
     * @throws ConfigException
     */
    byte[] readBytes(String path, ConfigPathMode pathMode, ConfigWatcher watcher) throws ConfigException;

    /**
     * @param path
     * @param pathMode
     * @return
     * @throws ConfigException
     */
    boolean exists(String path, ConfigPathMode pathMode) throws ConfigException;
    
    /**
     * @param path
     * @param pathMode
     * @return
     * @throws ConfigException
     */
    boolean exists(String path,ConfigWatcher watcher) throws ConfigException;

    /**
     * @param path
     * @return
     * @throws ConfigException
     */
    boolean exists(String path) throws ConfigException;
    
    /**
     * 
     * @param path
     * @param pathMode
     * @param watcher
     * @return
     * @throws ConfigException
     */
	boolean exists(String path, ConfigPathMode pathMode, ConfigWatcher watcher)
			throws ConfigException;
}
