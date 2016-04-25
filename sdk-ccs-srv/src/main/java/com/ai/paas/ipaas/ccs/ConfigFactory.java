package com.ai.paas.ipaas.ccs;

import com.ai.paas.ipaas.PaaSConstant;
import com.ai.paas.ipaas.ccs.constants.ConfigCenterConstants;
import com.ai.paas.ipaas.ccs.impl.ConfigClientImpl;
import com.ai.paas.ipaas.ccs.zookeeper.ZKClient;
import com.ai.paas.ipaas.ccs.zookeeper.impl.ZKPoolFactory;
import com.ai.paas.ipaas.uac.service.IUserClient;
import com.ai.paas.ipaas.uac.service.UserClientFactory;
import com.ai.paas.ipaas.uac.vo.AuthDescriptor;
import com.ai.paas.ipaas.uac.vo.AuthResult;
import com.ai.paas.ipaas.util.Assert;
import com.ai.paas.ipaas.util.CiperUtil;
import com.ai.paas.ipaas.util.ResourceUtil;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigFactory {

	private static final String ZK_ADDR = "zkAddr";
	private static final String USER_NAME = "zkUser";
	private static final String ZK_PASSWD = "zkPwd";
	private static final String ZK_TIMEOUT = "timOut";
	@SuppressWarnings("unused")
	private static transient final Logger logger = LoggerFactory
			.getLogger(ConfigFactory.class);
	private static Map<String, IConfigClient> configClientCache = new ConcurrentHashMap<String, IConfigClient>();

	private ConfigFactory() {
		// 禁止实例化
	}

	/**
	 * 为外部使用
	 *
	 * @param ad
	 * @return
	 */
	public static IConfigClient getConfigClient(AuthDescriptor ad)
			throws Exception {
		IConfigClient configClient = null;
		Assert.notNull(ad,
				ResourceUtil.getMessage("com.ai.paas.ipaas.common.auth_null"));
		Assert.notNull(ad.getAuthAdress(), ResourceUtil
				.getMessage("com.ai.paas.ipaas.common.auth_addr_null"));
		Assert.notNull(ad.getPid(), ResourceUtil
				.getMessage("com.ai.paas.ipaas.common.auth_pid_null"));
		Assert.notNull(ad.getPassword(), ResourceUtil
				.getMessage("com.ai.paas.ipaas.common.auth_passwd_null"));
		Assert.notNull(ad.getServiceId(),
				ResourceUtil.getMessage("com.ai.paas.ipaas.common.srvid_null"));
		// 认证通过后，判断是否存在已有实例，有，直接返回
		if (configClientCache.containsKey(ad.getPid().trim() + "_"
				+ ad.getServiceId().trim())) {
			configClient = configClientCache.get(ad.getPid().trim() + "_"
					+ ad.getServiceId().trim());
			return configClient;
		}

		IUserClient userClient = UserClientFactory.getUserClient();
		AuthResult result = userClient.auth(ad);

		// 获取缓存服务的Zookeeper地址用户名和密码
		ZKClient client = ZKPoolFactory.getZKPool(
				result.getConfigAddr(),
				result.getConfigUser(),
				CiperUtil.decrypt(ConfigCenterConstants.operators,
						result.getConfigPasswd()), ad.getServiceId())
				.getZkClient(result.getConfigAddr(), result.getConfigUser(),
						ad.getServiceId());
		String configValue = client.getNodeData(apendUserNodePath(ad, result),
				false);

		Gson gson = new Gson();
		@SuppressWarnings("unchecked")
		Map<String, Object> configMap = gson.fromJson(configValue, Map.class);

		String zkAddr = String.valueOf(configMap.get(ZK_ADDR));
		String zkUser = String.valueOf(configMap.get(USER_NAME));
		String zkPasswd = String.valueOf(configMap.get(ZK_PASSWD));
		int timeout = Double.valueOf(String.valueOf(configMap.get(ZK_TIMEOUT)))
				.intValue();

		configClient = new ConfigClientImpl(zkAddr, zkUser, CiperUtil.decrypt(
				ConfigCenterConstants.operators, zkPasswd), ad.getServiceId(),
				timeout);
		configClientCache.put(ad.getPid() + "_" + ad.getServiceId(),
				configClient);
		return configClient;
	}

	private static String apendUserNodePath(AuthDescriptor ad, AuthResult result) {
		return ConfigCenterConstants.UserNodePrefix.FOR_PAAS_PLATFORM_PREFIX
				+ PaaSConstant.UNIX_SEPERATOR
				+ result.getUserId()
				+ ConfigCenterConstants.UserNodePrefix.FOR_PAAS_PLATFORM_HAS_READ_PREFIX
				+ ConfigCenterConstants.CONFIG_SERVICE_NODE_NAME
				+ PaaSConstant.UNIX_SEPERATOR + ad.getServiceId();
	}

}
