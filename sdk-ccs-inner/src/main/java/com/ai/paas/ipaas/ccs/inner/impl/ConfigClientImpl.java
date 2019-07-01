package com.ai.paas.ipaas.ccs.inner.impl;

import com.ai.paas.Constant;
import com.ai.paas.ipaas.ccs.constants.*;
import com.ai.paas.ipaas.ccs.inner.ICCSComponent;
import com.ai.paas.ipaas.ccs.inner.constants.ConfigPathMode;
import com.ai.paas.ipaas.ccs.util.ZKUtil;
import com.ai.paas.ipaas.ccs.zookeeper.ConfigWatcher;
import com.ai.paas.ipaas.ccs.zookeeper.MutexLock;
import com.ai.paas.ipaas.ccs.zookeeper.ZKClient;
import com.ai.paas.ipaas.ccs.zookeeper.impl.ZKFactory;
import com.ai.paas.util.ResourceUtil;
import com.ai.paas.util.StringUtil;

import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class ConfigClientImpl implements ICCSComponent {
    private static final Logger log = LoggerFactory.getLogger(ConfigClientImpl.class);

    private String zkAddr;
    private String authInfo;
    private String userName;
    private String zkUserNodePath;
    private String passwd;
    private int timeout;
    ZKClient zkClient = null;

    public ConfigClientImpl() {
    }

    public ConfigClientImpl(String configAddr, String username, String passwd, int timeout) {
        this.authInfo = username + ":" + passwd;
        this.userName = username;
        this.zkAddr = configAddr;
        this.passwd = passwd;
        this.timeout = timeout;

        zkClient = getZkClient();
        zkUserNodePath = ConfigConstant.UserNodePrefix.FOR_PAAS_PLATFORM_PREFIX + Constant.UNIX_SEPERATOR + userName;

        // 校验用户节点是否存在，不存在则提示给用户
        if (!userNodeIsExist()) {
            throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.USER_NODE_NOT_EXISTS));
        }
        log.info("zookeeper info:{},{},{}", configAddr, username, timeout);
        // 校验用户是否认证成功
        userAuth();
    }

    private boolean userNodeIsExist() {
        try {
            return zkClient.exists(zkUserNodePath);
        } catch (Exception e) {
            throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.USER_NODE_NOT_EXISTS), e);
        }
    }

    private ZKClient getZkClient() {
        ZKClient client = ZKFactory.getZkClient(zkAddr, userName, passwd, timeout);

        if (client == null)
            throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.GET_CONFIG_CLIENT_FAILED));

        return client;
    }

    /**
     * 校验zookeeper用户是否授权
     *
     * @return
     */
    private boolean userAuth() {
        try {
            zkClient.getNodeData(zkUserNodePath, false);
            return true;
        } catch (Exception e) {
            if (e instanceof KeeperException.NoAuthException) {
                throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.USER_AUTH_FAILED));
            } else {
                throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.AUTH_FAILED), e);
            }
        }
    }

    @Override
    public void add(String path, String value) {
        add(path, value, AddMode.PERSISTENT);
    }

    @Override
    public void add(String path, String value, AddMode addMode) {
        byte[] bytes = null;
        if (!StringUtil.isBlank(value)) {
            bytes = value.getBytes(StandardCharsets.UTF_8);
        }

        add(path, bytes, addMode);
    }

    @Override
    public void add(String path, byte[] value) {
        add(path, value, AddMode.PERSISTENT);
    }

    @Override
    public void add(String path, byte[] value, AddMode addMode) {
        if (!validatePath(path))
            throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.PATH_ILL));
        if (exists(path, ConfigPathMode.WRITABLE)) {
            modify(path, value);
            return;
        }

        try {
            zkClient.createNode(ConfigPathMode.appendPath(userName, ConfigPathMode.WRITABLE.getFlag(), path),
                    ZKUtil.createWritableACL(authInfo), value, AddMode.convertMode(addMode.getFlag()));
        } catch (Exception e) {
            if (e instanceof KeeperException.NoAuthException) {
                throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.USER_AUTH_FAILED, path));
            }
            throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.ADD_CONFIG_FAILED), e);
        }
    }

    /**
     * 得到节点互斥锁
     */
    @Override
    public MutexLock getMutexLock(String path) {
        if (!validatePath(path))
            throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.PATH_ILL));
        try {
            return new MutexLock(zkClient
                    .getInterProcessLock(ConfigPathMode.appendPath(userName, ConfigPathMode.WRITABLE.getFlag(), path)));
        } catch (Exception e) {
            if (e instanceof KeeperException.NoAuthException) {
                throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.USER_AUTH_FAILED, path));
            }
            throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.GET_LOCK_FAILED), e);
        }
    }

    @Override
    public void modify(String path, String value) {
        byte[] bytes = null;
        if (!StringUtil.isBlank(value)) {
            bytes = value.getBytes(StandardCharsets.UTF_8);
        }

        modify(path, bytes);
    }

    @Override
    public void modify(String path, byte[] value) {
        if (!validatePath(path))
            throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.PATH_ILL));
        if (!exists(path, ConfigPathMode.WRITABLE)) {
            throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.PATH_NOT_EXISTS, path));
        }

        try {
            zkClient.setNodeData(ConfigPathMode.appendPath(userName, ConfigPathMode.WRITABLE.getFlag(), path), value);
        } catch (Exception e) {
            if (e instanceof KeeperException.NoAuthException) {
                throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.USER_AUTH_FAILED, path));
            }
            throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.MODIFY_FAILED), e);
        }
    }

    @Override
    public List<String> listSubPath(String path, ConfigPathMode pathMode, ConfigWatcher watcher) {
        if (!validatePath(path))
            throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.PATH_ILL));
        if (!exists(path, pathMode)) {
            throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.PATH_NOT_EXISTS, path));
        }

        try {
            return zkClient.getChildren(ConfigPathMode.appendPath(userName, pathMode.getFlag(), path), watcher);
        } catch (Exception e) {
            if (e instanceof KeeperException.NoAuthException) {
                throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.USER_AUTH_FAILED, path));
            }
            throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.LIST_CHILDREN_FAILED), e);
        }
    }

    @Override
    public List<String> listSubPath(String path, ConfigPathMode pathMode) {
        return listSubPath(path, pathMode, null);
    }

    @Override
    public List<String> listSubPath(String path) {
        return listSubPath(path, ConfigPathMode.READONLY, null);
    }

    @Override
    public List<String> listSubPath(String path, ConfigWatcher watcher) {
        return listSubPath(path, ConfigPathMode.READONLY, watcher);
    }

    @Override
    public void remove(String path) {
        if (!validatePath(path))
            throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.PATH_ILL));
        if (!exists(path, ConfigPathMode.WRITABLE)) {
            return;
        }

        try {
            zkClient.deleteNode(ConfigPathMode.appendPath(userName, ConfigPathMode.WRITABLE.getFlag(), path));
        } catch (Exception e) {
            if (e instanceof KeeperException.NoAuthException) {
                throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.USER_AUTH_FAILED, path));
            }
            throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.REMOVE_CONFIG_FAILED), e);
        }
    }

    /**
     * 得到节点value，默认是readOnly路径下的
     */
    @Override
    public String get(String path) {
        return get(path, ConfigPathMode.READONLY, null);
    }

    @Override
    public String get(String path, ConfigPathMode pathMode) {
        return get(path, pathMode, null);
    }

    @Override
    public String get(String path, ConfigWatcher watcher) {
        return get(path, ConfigPathMode.READONLY, watcher);
    }

    @Override
    public String get(String path, ConfigPathMode pathMode, ConfigWatcher watcher) {
        if (!exists(path, pathMode)) {
            throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.PATH_NOT_EXISTS, path));
        }

        try {
            return zkClient.getNodeData(ConfigPathMode.appendPath(userName, pathMode.getFlag(), path), watcher);
        } catch (Exception e) {
            if (e instanceof KeeperException.NoAuthException) {
                throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.USER_AUTH_FAILED, path));
            }
            throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.CONVERT_DATA_FAILED), e);
        }
    }

    @Override
    public byte[] readBytes(String path) {
        return readBytes(path, ConfigPathMode.READONLY, null);
    }

    @Override
    public byte[] readBytes(String path, ConfigPathMode pathMode) {
        return readBytes(path, pathMode, null);
    }

    @Override
    public byte[] readBytes(String path, ConfigWatcher watcher) {
        return readBytes(path, ConfigPathMode.READONLY, watcher);
    }

    @Override
    public byte[] readBytes(String path, ConfigPathMode pathMode, ConfigWatcher watcher) {
        if (!exists(path, pathMode)) {
            throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.PATH_NOT_EXISTS, path));
        }

        try {
            return zkClient.getNodeBytes(ConfigPathMode.appendPath(userName, pathMode.getFlag(), path), watcher);
        } catch (Exception e) {
            if (e instanceof KeeperException.NoAuthException) {
                throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.USER_AUTH_FAILED, path));
            }
            throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.GET_CONFIG_VALUE_FAILED), e);
        }
    }

    /**
     * 
     * 判断Path是否存在，不指定路径类型默认是只读路径下的
     * 
     */
    @Override
    public boolean exists(String path) {
        return exists(path, ConfigPathMode.READONLY);
    }

    @Override
    public boolean exists(String path, ConfigWatcher watcher) {
        return exists(path, ConfigPathMode.READONLY, watcher);
    }

    /**
     * 
     * 判断Path是否存在，指定路径类型（1-writable，2-readOnly）
     * 
     */
    @Override
    public boolean exists(String path, ConfigPathMode pathMode) {
        return exists(path, pathMode, null);
    }

    @Override
    public boolean exists(String path, ConfigPathMode pathMode, ConfigWatcher watcher) {
        try {
            if (null != watcher) {
                return zkClient.exists(ConfigPathMode.appendPath(userName, pathMode.getFlag(), path), watcher);
            } else {
                return zkClient.exists(ConfigPathMode.appendPath(userName, pathMode.getFlag(), path));
            }
        } catch (Exception e) {
            if (e instanceof KeeperException.NoAuthException) {
                throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.USER_AUTH_FAILED));
            } else {
                throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.GET_CONFIG_CLIENT_FAILED), e);
            }
        }
    }

    public boolean validatePath(String path) {
        return path.startsWith(Constant.UNIX_SEPERATOR) || path.endsWith(Constant.UNIX_SEPERATOR);
    }
}
