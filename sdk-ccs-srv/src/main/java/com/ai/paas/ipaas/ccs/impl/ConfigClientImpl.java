package com.ai.paas.ipaas.ccs.impl;

import com.ai.paas.ipaas.PaaSConstant;
import com.ai.paas.ipaas.ccs.IConfigClient;
import com.ai.paas.ipaas.ccs.constants.AddMode;
import com.ai.paas.ipaas.ccs.constants.BundleKeyConstant;
import com.ai.paas.ipaas.ccs.constants.ConfigCenterConstants;
import com.ai.paas.ipaas.ccs.constants.ConfigException;
import com.ai.paas.ipaas.ccs.zookeeper.ConfigWatcher;
import com.ai.paas.ipaas.ccs.zookeeper.ZKClient;
import com.ai.paas.ipaas.ccs.zookeeper.impl.ZKPool;
import com.ai.paas.ipaas.ccs.zookeeper.impl.ZKPoolFactory;
import com.ai.paas.ipaas.util.ResourceUtil;
import com.ai.paas.ipaas.util.StringUtil;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class ConfigClientImpl implements IConfigClient {
	private static transient final Logger log = LoggerFactory
			.getLogger(ConfigClientImpl.class);

	private String authInfo;
	private String userName;
	private String zkUserNodePath;
	private ZKPool zkPool;
	private String zkAddr;
	private ZKClient client;

	public ConfigClientImpl() {
	}

	public ConfigClientImpl(String zkAddr, String userName, String password,
			String serviceId, int timeout) throws ConfigException {
		try {
			this.authInfo = userName + ":" + password;
			this.userName = userName;
			this.zkAddr = zkAddr;
			zkPool = ZKPoolFactory.getZKPool(this.zkAddr, userName, password,
					serviceId, timeout);
			this.client = zkPool.getZkClient(this.zkAddr, userName, serviceId);

			// 拼接用户节点
			zkUserNodePath = ConfigCenterConstants.UserNodePrefix.FOR_PAAS_PLATFORM_PREFIX
					+ ConfigCenterConstants.SEPARATOR
					+ userName
					+ PaaSConstant.UNIX_SEPERATOR + serviceId;

			if (log.isDebugEnabled()) {
				log.debug("拼接的用户节点[zkUserNodePath]:" + zkUserNodePath);
			}
			// 校验用户节点是否存在，不存在则提示给用户
			if (!userNodeIsExist()) {
				client.createNode(zkUserNodePath, createWritableACL(), "",
						AddMode.convertMode(AddMode.PERSISTENT.getFlag()));
			}
			// 校验用户是否认证成功
			checkUser();
		} catch (Exception e) {
			throw new ConfigException(
					ResourceUtil
							.getMessage(BundleKeyConstant.GET_CONFIG_CLIENT_FAILED),
					e);
		}
	}

	/**
	 * 校验zookeeper用户是否授权
	 *
	 * @return
	 */
	private void checkUser() throws ConfigException {
		try {
			// getZkClientFromPool(serviceId).getNodeData(zkUserNodePath);
			this.client.getNodeData(zkUserNodePath);
		} catch (Exception e) {
			if (e instanceof KeeperException.NoAuthException) {
				throw new ConfigException(
						ResourceUtil
								.getMessage(BundleKeyConstant.USER_AUTH_FAILED));
			} else {
				throw new ConfigException(
						ResourceUtil
								.getMessage(BundleKeyConstant.USER_NODE_NOT_EXISTS));
			}
		}
	}

	private String appendUserDataNodePrefix(String path) throws ConfigException {
		// 校验用户传入Path，必须以'/'开头,否则抛出异常
		if (!path.startsWith(PaaSConstant.UNIX_SEPERATOR)
				&& path.endsWith(PaaSConstant.UNIX_SEPERATOR))
			throw new ConfigException(
					ResourceUtil.getMessage(BundleKeyConstant.PATH_ILL));
		return zkUserNodePath + path;
	}

	private boolean userNodeIsExist() throws ConfigException {
		boolean result = true;
		try {
			// result = getZkClientFromPool(serviceId).exists(zkUserNodePath);
			result = this.client.exists(zkUserNodePath);
		} catch (Exception e) {
			if (e instanceof KeeperException.NoAuthException) {
				throw new ConfigException(
						ResourceUtil
								.getMessage(BundleKeyConstant.USER_AUTH_FAILED));
			}
			throw new ConfigException(
					ResourceUtil
							.getMessage(BundleKeyConstant.USER_NODE_NOT_EXISTS),
					e);
		}
		log.debug("校验用户[" + userName + "]用户节点是否存在:" + result);
		return result;
	}

	@Override
	public void add(String configPath, String value) throws ConfigException {
		byte[] data = null;
		if (!StringUtil.isBlank(value))
			try {
				data = value.getBytes(PaaSConstant.CHARSET_UTF8);
			} catch (UnsupportedEncodingException e) {
				throw new ConfigException(
						ResourceUtil
								.getMessage(BundleKeyConstant.CONVERT_DATA_FAILED),
						e);
			}
		add(configPath, data, AddMode.PERSISTENT);
	}

	@Override
	public void add(String configPath, byte[] bytes) throws ConfigException {
		add(configPath, bytes, AddMode.PERSISTENT);
	}

	private void add(String configPath, byte[] bytes, AddMode mode)
			throws ConfigException {
		if (exists(appendUserDataNodePrefix(configPath))) {
			throw new ConfigException(ResourceUtil.getMessage(
					BundleKeyConstant.PATH_EXISTS, configPath));
		}

		// ZKClient client = null;
		try {
			// client = getZkClientFromPool();
			client.createNode(appendUserDataNodePrefix(configPath),
					createWritableACL(), bytes,
					AddMode.convertMode(mode.getFlag()));
		} catch (Exception e) {
			if (e instanceof KeeperException.NoAuthException) {
				throw new ConfigException(ResourceUtil.getMessage(
						BundleKeyConstant.USER_AUTH_FAILED, configPath));
			}
			throw new ConfigException(
					ResourceUtil
							.getMessage(BundleKeyConstant.ADD_CONFIG_FAILED),
					e);
		}
	}

	@Override
	public String get(String configPath) throws ConfigException {
		return get(configPath, null);
	}

	@Override
	public String get(String configPath, ConfigWatcher watcher)
			throws ConfigException {
		// ZKClient client = null;
		try {
			// client = getZkClientFromPool();
			return client.getNodeData(appendUserDataNodePrefix(configPath),
					watcher);
		} catch (Exception e) {
			if (e instanceof KeeperException.NoAuthException) {
				throw new ConfigException(ResourceUtil.getMessage(
						BundleKeyConstant.USER_AUTH_FAILED, configPath));
			}
			throw new ConfigException(
					ResourceUtil
							.getMessage(BundleKeyConstant.ADD_CONFIG_FAILED),
					e);
		}
	}

	@Override
	public byte[] readBytes(String configPath) throws ConfigException {
		return readBytes(configPath, null);
	}

	@Override
	public byte[] readBytes(String configPath, ConfigWatcher watcher)
			throws ConfigException {
		if (!exists(configPath)) {
			throw new ConfigException(ResourceUtil.getMessage(
					BundleKeyConstant.PATH_NOT_EXISTS, configPath));
		}

		// ZKClient client = null;
		try {
			// client = getZkClientFromPool();
			return client.getNodeBytes(appendUserDataNodePrefix(configPath),
					watcher);
		} catch (Exception e) {
			if (e instanceof KeeperException.NoAuthException) {
				throw new ConfigException(ResourceUtil.getMessage(
						BundleKeyConstant.USER_AUTH_FAILED, configPath));
			}
			throw new ConfigException(ResourceUtil.getMessage(
					BundleKeyConstant.PATH_NOT_EXISTS, configPath), e);
		}
	}

	@Override
	public void modify(String configPath, String value) throws ConfigException {
		byte[] data = null;
		if (!StringUtil.isBlank(value))
			try {
				data = value.getBytes(PaaSConstant.CHARSET_UTF8);
			} catch (UnsupportedEncodingException e) {
				throw new ConfigException(
						ResourceUtil
								.getMessage(BundleKeyConstant.CONVERT_DATA_FAILED),
						e);
			}
		modify(configPath, data);
	}

	@Override
	public void modify(String configPath, byte[] value) throws ConfigException {
		if (!exists(configPath)) {
			throw new ConfigException(ResourceUtil.getMessage(
					BundleKeyConstant.PATH_NOT_EXISTS, configPath));
		}

		// ZKClient client = null;
		try {
			// client = getZkClientFromPool();
			client.setNodeData(appendUserDataNodePrefix(configPath), value);
		} catch (Exception e) {
			if (e instanceof KeeperException.NoAuthException) {
				throw new ConfigException(ResourceUtil.getMessage(
						BundleKeyConstant.USER_AUTH_FAILED, configPath));
			}
			throw new ConfigException(
					ResourceUtil.getMessage(BundleKeyConstant.MODIFY_FAILED), e);
		}
	}

	@Override
	public boolean exists(String configPath) throws ConfigException {
		return exists(configPath, null);
	}

	@Override
	public boolean exists(String configPath, ConfigWatcher watcher)
			throws ConfigException {
		// ZKClient client = null;
		try {
			// client = getZkClientFromPool();
			if (null != watcher) {
				return client.exists(appendUserDataNodePrefix(configPath),
						watcher);
			} else {
				return client.exists(appendUserDataNodePrefix(configPath));
			}
		} catch (Exception e) {
			if (e instanceof KeeperException.NoAuthException) {
				throw new ConfigException(ResourceUtil.getMessage(
						BundleKeyConstant.USER_AUTH_FAILED, configPath));
			}
			throw new ConfigException(ResourceUtil.getMessage(
					BundleKeyConstant.PATH_NOT_EXISTS, configPath), e);
		}
	}

	@Override
	public void remove(String configPath) throws ConfigException {
		if (!exists(configPath)) {
			return;
		}

		// ZKClient client = null;
		try {
			// client = getZkClientFromPool();
			client.deleteNode(appendUserDataNodePrefix(configPath));
		} catch (Exception e) {
			if (e instanceof KeeperException.NoAuthException) {
				throw new ConfigException(ResourceUtil.getMessage(
						BundleKeyConstant.USER_AUTH_FAILED, configPath));
			}
			throw new ConfigException(ResourceUtil.getMessage(
					BundleKeyConstant.PATH_NOT_EXISTS, configPath), e);
		}
	}

	@Override
	public List<String> listSubPath(String configPath) throws ConfigException {
		return listSubPath(configPath, null);
	}

	public List<String> listSubPath(String configPath, ConfigWatcher watcher)
			throws ConfigException {
		if (!exists(configPath)) {
			throw new ConfigException(ResourceUtil.getMessage(
					BundleKeyConstant.PATH_NOT_EXISTS, configPath));
		}

		// ZKClient client = null;
		try {
			// client = getZkClientFromPool();
			return client.getChildren(appendUserDataNodePrefix(configPath),
					watcher);
		} catch (Exception e) {
			if (e instanceof KeeperException.NoAuthException) {
				throw new ConfigException(ResourceUtil.getMessage(
						BundleKeyConstant.USER_AUTH_FAILED, configPath));
			}
			throw new ConfigException(
					ResourceUtil
							.getMessage(BundleKeyConstant.LIST_CHILDREN_FAILED),
					e);
		}
	}

	// private ZKClient getZkClientFromPool(String serviceId) throws Exception {
	// ZKClient zkClient = zkPool.getZkClient(zkAddr,userName,serviceId);
	//
	// if (zkClient == null)
	// throw new
	// ConfigException(ResourceUtil.getMessage(BundleKeyConstant.GET_CONFIG_CLIENT_FAILED));
	//
	// return zkClient;
	// }

	private List<ACL> createWritableACL() throws NoSuchAlgorithmException {
		List<ACL> acls = new ArrayList<ACL>();
		Id id1 = new Id("digest",
				DigestAuthenticationProvider.generateDigest(authInfo));
		ACL userACL = new ACL(ZooDefs.Perms.ALL, id1);
		acls.add(userACL);
		return acls;
	}
}
