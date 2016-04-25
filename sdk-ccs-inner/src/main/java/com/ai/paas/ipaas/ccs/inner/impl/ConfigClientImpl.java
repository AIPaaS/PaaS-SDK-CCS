package com.ai.paas.ipaas.ccs.inner.impl;

import com.ai.paas.ipaas.PaaSConstant;
import com.ai.paas.ipaas.ccs.constants.*;
import com.ai.paas.ipaas.ccs.inner.ICCSComponent;
import com.ai.paas.ipaas.ccs.inner.constants.ConfigPathMode;
import com.ai.paas.ipaas.ccs.util.ZKUtil;
import com.ai.paas.ipaas.ccs.zookeeper.ConfigWatcher;
import com.ai.paas.ipaas.ccs.zookeeper.MutexLock;
import com.ai.paas.ipaas.ccs.zookeeper.ZKClient;
import com.ai.paas.ipaas.ccs.zookeeper.impl.ZKPool;
import com.ai.paas.ipaas.ccs.zookeeper.impl.ZKPoolFactory;
import com.ai.paas.ipaas.util.ResourceUtil;
import com.ai.paas.ipaas.util.StringUtil;

import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class ConfigClientImpl implements ICCSComponent {
    @SuppressWarnings("unused")
	private static transient final Logger log = LoggerFactory.getLogger(ConfigClientImpl.class);

    private String zkAddr;
    private String authInfo;
    private String userName;
    private String zkUserNodePath;
    private ZKPool zkPool;

    public ConfigClientImpl() {
    }


    public ConfigClientImpl(String configAddr, String username, String passwd, int timeout) throws ConfigException {
        this.authInfo = username + ":" + passwd;
        this.userName = username;
        this.zkAddr = configAddr;
        zkPool = ZKPoolFactory.getZKPool(configAddr, username, passwd, timeout);
        zkUserNodePath = ConfigCenterConstants.UserNodePrefix.FOR_PAAS_PLATFORM_PREFIX + "/" + userName;

        //校验用户节点是否存在，不存在则提示给用户
        if (!userNodeIsExist()) {
            throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.USER_NODE_NOT_EXISTS));
        }
        //校验用户是否认证成功
        userAuth();
    }

    private boolean userNodeIsExist() throws ConfigException {
        ZKClient zkClient = null;
        try {
            zkClient = getZkClientFromPool();
            return zkClient.exists(zkUserNodePath);
        } catch (Exception e) {
            throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.USER_NODE_NOT_EXISTS), e);
        }
    }


    private ZKClient getZkClientFromPool() throws Exception {
        ZKClient zkClient = zkPool.getZkClient(zkAddr, userName);

        if (zkClient == null)
            throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.GET_CONFIG_CLIENT_FAILED));

        return zkClient;
    }

    /**
     * 校验zookeeper用户是否授权
     *
     * @return
     */
    private boolean userAuth() throws ConfigException {
        ZKClient client = null;
        try {
            client = getZkClientFromPool();
            client.getNodeData(zkUserNodePath, false);
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
    public void add(String path, String value) throws ConfigException {    	
        add(path, value, AddMode.PERSISTENT);
    }

    @Override
    public void add(String path, String value, AddMode addMode) throws ConfigException {
        byte[] bytes = null;
        if (!StringUtil.isBlank(value)) {
            try {
                bytes = value.getBytes(PaaSConstant.CHARSET_UTF8);
            } catch (UnsupportedEncodingException e) {
                throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.CONVERT_DATA_FAILED), e);
            }
        }

        add(path, bytes, addMode);
    }

    @Override
    public void add(String path, byte[] value) throws ConfigException {
        add(path, value, AddMode.PERSISTENT);
    }

    @Override
    public void add(String path, byte[] value, AddMode addMode) throws ConfigException {
    	if (!validatePath(path))
            throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.PATH_ILL));
    	if (exists(path, ConfigPathMode.WRITABLE)) {
            modify(path, value);
            return;
        }

        ZKClient zkClient = null;
        try {
            zkClient = getZkClientFromPool();
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
    public MutexLock getMutexLock(String path) throws ConfigException {
    	if (!validatePath(path))
            throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.PATH_ILL));
    	ZKClient zkClient = null;
        try {
            zkClient = getZkClientFromPool();
            return new MutexLock(zkClient.getInterProcessLock(ConfigPathMode.appendPath(userName,
                    ConfigPathMode.WRITABLE.getFlag(), path)));
        } catch (Exception e) {
            if (e instanceof KeeperException.NoAuthException) {
                throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.USER_AUTH_FAILED, path));
            }
            throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.GET_LOCK_FAILED), e);
        }
    }

    @Override
    public void modify(String path, String value) throws ConfigException {
        byte[] bytes = null;
        if (!StringUtil.isBlank(value)) {
            try {
                bytes = value.getBytes(ZkErrorCodeConstants.CHARSET_UTF8);
            } catch (UnsupportedEncodingException e) {
                throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.CONVERT_DATA_FAILED), e);
            }
        }

        modify(path, bytes);
    }

    @Override
    public void modify(String path, byte[] value) throws ConfigException {
    	if (!validatePath(path))
            throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.PATH_ILL));
    	if (!exists(path, ConfigPathMode.WRITABLE)) {
            throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.PATH_NOT_EXISTS, path));
        }

        ZKClient zkClient = null;
        try {
            zkClient = getZkClientFromPool();
            zkClient.setNodeData(ConfigPathMode.appendPath(userName, ConfigPathMode.WRITABLE.getFlag(), path), value);
        } catch (Exception e) {
            if (e instanceof KeeperException.NoAuthException) {
                throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.USER_AUTH_FAILED, path));
            }
            throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.MODIFY_FAILED), e);
        }/* finally {
            returnResources(zkClient);
        }*/
    }

    @Override
    public List<String> listSubPath(String path, ConfigPathMode pathMode, ConfigWatcher watcher) throws ConfigException {
    	if (!validatePath(path))
            throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.PATH_ILL));
    	if (!exists(path, pathMode)) {
            throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.PATH_NOT_EXISTS, path));
        }

        ZKClient zkClient = null;
        try {
            zkClient = getZkClientFromPool();
            return zkClient.getChildren(ConfigPathMode.appendPath(userName, pathMode.getFlag(), path), watcher);
        } catch (Exception e) {
            if (e instanceof KeeperException.NoAuthException) {
                throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.USER_AUTH_FAILED, path));
            }
            throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.LIST_CHILDREN_FAILED), e);
        }
    }

    @Override
    public List<String> listSubPath(String path, ConfigPathMode pathMode) throws ConfigException {
        return listSubPath(path, pathMode, null);
    }

    @Override
    public List<String> listSubPath(String path) throws ConfigException {
        return listSubPath(path, ConfigPathMode.READONLY, null);
    }

    @Override
    public List<String> listSubPath(String path, ConfigWatcher watcher) throws ConfigException {
        return listSubPath(path, ConfigPathMode.READONLY, watcher);
    }

    @Override
    public void remove(String path) throws ConfigException {
    	if (!validatePath(path))
            throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.PATH_ILL));
    	if (!exists(path, ConfigPathMode.WRITABLE)) {
            return;
        }

        ZKClient zkClient = null;
        try {
            zkClient = getZkClientFromPool();
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
    public String get(String path) throws ConfigException {
        return get(path, ConfigPathMode.READONLY, null);
    }

    @Override
    public String get(String path, ConfigPathMode pathMode) throws ConfigException {
        return get(path, pathMode, null);
    }

    @Override
    public String get(String path, ConfigWatcher watcher) throws ConfigException {
        return get(path, ConfigPathMode.READONLY, watcher);
    }

    @Override
    public String get(String path, ConfigPathMode pathMode, ConfigWatcher watcher) throws ConfigException {
        if (!exists(path, pathMode)) {
            throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.PATH_NOT_EXISTS, path));
        }

        ZKClient client = null;
        try {
            client = getZkClientFromPool();
            return client.getNodeData(ConfigPathMode.appendPath(userName, pathMode.getFlag(), path), watcher);
        } catch (Exception e) {
            if (e instanceof KeeperException.NoAuthException) {
                throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.USER_AUTH_FAILED, path));
            }
            throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.CONVERT_DATA_FAILED), e);
        }
    }

    @Override
    public byte[] readBytes(String path) throws ConfigException {
        return readBytes(path, ConfigPathMode.READONLY, null);
    }

    @Override
    public byte[] readBytes(String path, ConfigPathMode pathMode) throws ConfigException {
        return readBytes(path, pathMode, null);
    }

    @Override
    public byte[] readBytes(String path, ConfigWatcher watcher) throws ConfigException {
        return readBytes(path, ConfigPathMode.READONLY, watcher);
    }

    @Override
    public byte[] readBytes(String path, ConfigPathMode pathMode, ConfigWatcher watcher) throws ConfigException {
        if (!exists(path, pathMode)) {
            throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.PATH_NOT_EXISTS, path));
        }

        ZKClient client = null;
        try {
            client = getZkClientFromPool();
            return client.getNodeBytes(ConfigPathMode.appendPath(userName, pathMode.getFlag(), path), watcher);
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
    public boolean exists(String path) throws ConfigException {
        return exists(path, ConfigPathMode.READONLY);
    }
    
    @Override
	public boolean exists(String path, ConfigWatcher watcher)
			throws ConfigException {
    	return exists(path, ConfigPathMode.READONLY,watcher);
	}
    
    /**
     * 
     * 判断Path是否存在，指定路径类型（1-writable，2-readOnly）
     * 
     */
    @Override
    public boolean exists(String path, ConfigPathMode pathMode) throws ConfigException {
    	return exists(path, pathMode,null);
    }
    
    @Override
    public boolean exists(String path, ConfigPathMode pathMode, ConfigWatcher watcher) throws ConfigException {
        ZKClient zkClient = null;
        try {
            zkClient = getZkClientFromPool();
            if(null!=watcher){           	
            	return zkClient.exists(ConfigPathMode.appendPath(userName, pathMode.getFlag(), path),watcher);
            }else{
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
    
    public boolean validatePath(String path){
    	if(!path.startsWith(PaaSConstant.UNIX_SEPERATOR)&&path.endsWith(PaaSConstant.UNIX_SEPERATOR)){
    		return false;    		
    	}
    	return true;
    }
}
