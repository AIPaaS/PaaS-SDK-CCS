package com.ai.paas.ipaas.ccs.inner;

import com.ai.paas.ipaas.ccs.constants.BundleKeyConstant;
import com.ai.paas.ipaas.ccs.constants.ConfigCenterConstants;
import com.ai.paas.ipaas.ccs.constants.ConfigException;
import com.ai.paas.ipaas.ccs.inner.impl.ConfigClientImpl;
import com.ai.paas.ipaas.util.Assert;
import com.ai.paas.ipaas.util.CiperUtil;
import com.ai.paas.ipaas.util.ResourceUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CCSComponentFactory {

	@SuppressWarnings("unused")
	private static transient final Logger logger = LoggerFactory
			.getLogger(CCSComponentFactory.class);

	// private static Map<String, ICCSComponent> configClientMap = new
	// ConcurrentHashMap<String, ICCSComponent>();

	private CCSComponentFactory() {
		// 禁止实例化
	}

	/**
	 * @param configAddr
	 * @param username
	 * @param passwd
	 * @return
	 * @throws ConfigException
	 */
	public static ICCSComponent getConfigClient(String configAddr,
			String username, String passwd) throws ConfigException {
		String timeOut = System.getProperty("zk.connect.timeout");
		if (timeOut == null || timeOut.length() <= 0) {
			return getConfigClient(configAddr, username, passwd, 20000);
		} else {
			return getConfigClient(configAddr, username, passwd,
					Integer.valueOf(timeOut));
		}
	}

	/**
	 * 根据用户传入的配置中心地址，用户名称和用户密码获取配置中心Client
	 *
	 * @param configAddr
	 * @param username
	 * @param passwd
	 * @return
	 */
	public static ICCSComponent getConfigClient(String configAddr,
			String username, String passwd, int timeout) throws ConfigException {
		/* 1. 校验参数是否为空 */
		validateParamater(configAddr, username, passwd);
		configAddr = configAddr.trim();
		username = username.trim();
		passwd = passwd.trim();
		if (passwd.length() % 2 != 0)
			throw new ConfigException(
					ResourceUtil.getMessage(BundleKeyConstant.USER_AUTH_FAILED));

		/* 2.1 生成新对象，并将变量放入缓存变量中 */
		ICCSComponent client = new ConfigClientImpl(configAddr, username,
				CiperUtil.decrypt(ConfigCenterConstants.operators, passwd),
				timeout);

		return client;
	}

	@SuppressWarnings("unused")
	private static String appendKey(String configAddr, String username) {
		return configAddr + "-" + username;
	}

	private static void validateParamater(String configAddr, String username,
			String passwd) {
		Assert.notNull(configAddr, ResourceUtil
				.getMessage(BundleKeyConstant.CONFIG_ADDRESS_IS_NULL));
		Assert.notNull(username,
				ResourceUtil.getMessage(BundleKeyConstant.USER_NAME_IS_NULL));
		Assert.notNull(passwd,
				ResourceUtil.getMessage(BundleKeyConstant.PASSWD_IS_NULL));
	}

}
