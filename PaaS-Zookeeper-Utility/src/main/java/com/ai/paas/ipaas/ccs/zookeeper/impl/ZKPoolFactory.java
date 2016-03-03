package com.ai.paas.ipaas.ccs.zookeeper.impl;

import com.ai.paas.ipaas.ccs.constants.BundleKeyConstant;
import com.ai.paas.ipaas.ccs.constants.ConfigCenterConstants;
import com.ai.paas.ipaas.ccs.constants.ConfigException;
import com.ai.paas.ipaas.ccs.zookeeper.ZKClient;
import com.ai.paas.ipaas.util.Assert;
import com.ai.paas.ipaas.util.ResourceUtil;
import com.ai.paas.ipaas.util.StringUtil;

public class ZKPoolFactory {
    private static final ZKPool zkPool = new ZKPool();

    public static ZKPool getZKPool(String zkAddress, String zkUser, String zkPasswd, int timeOut) throws ConfigException {
        validateParam(zkAddress, zkUser, zkPasswd);
        if (zkPool.exist(zkAddress, zkUser)) {
            return zkPool;
        }
        ZKClient client = null;
        try {
            client = new ZKClient(zkAddress, timeOut, new String[]{ConfigCenterConstants.ZKAuthSchema.DIGEST, getAuthInfo(zkUser, zkPasswd)});
            client.addAuth(ConfigCenterConstants.ZKAuthSchema.DIGEST, getAuthInfo(zkUser, zkPasswd));
        } catch (Exception e) {
            throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.GET_CONFIG_CLIENT_FAILED));
        }
        zkPool.addZKClient(zkAddress, zkUser, client);
        return zkPool;
    }

    public static ZKPool getZKPool(String zkAddress, String zkUser, String zkPasswd, String serviceId, int timeOut) throws ConfigException {
        validateParam(zkAddress, zkUser, zkPasswd, serviceId);
        if (zkPool.exist(zkAddress, zkUser, serviceId)) {
            return zkPool;
        }
        ZKClient client = null;
        try {
            client = new ZKClient(zkAddress, timeOut, new String[]{ConfigCenterConstants.ZKAuthSchema.DIGEST, getAuthInfo(zkUser, zkPasswd)});
            client.addAuth(ConfigCenterConstants.ZKAuthSchema.DIGEST, getAuthInfo(zkUser, zkPasswd));
        } catch (Exception e) {
            throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.GET_CONFIG_CLIENT_FAILED));
        }
        zkPool.addZKClient(zkAddress, zkUser, serviceId, client);
        return zkPool;
    }

    private static void validateParam(String zkAddress, String zkUser) {
        Assert.notNull(zkAddress, ResourceUtil.getMessage(BundleKeyConstant.CONFIG_ADDRESS_IS_NULL));
        Assert.notNull(zkUser, ResourceUtil.getMessage(BundleKeyConstant.USER_NAME_IS_NULL));
    }


    private static void validateParam(String zkAddress, String zkUser, String zkPasswd) {
        Assert.notNull(zkAddress, ResourceUtil.getMessage(BundleKeyConstant.CONFIG_ADDRESS_IS_NULL));
        Assert.notNull(zkUser, ResourceUtil.getMessage(BundleKeyConstant.USER_NAME_IS_NULL));
        Assert.notNull(zkPasswd, ResourceUtil.getMessage(BundleKeyConstant.PASSWD_IS_NULL));
    }

    private static void validateParam(String zkAddress, String zkUser, String zkPasswd, String serviceId) {
        Assert.notNull(zkAddress, ResourceUtil.getMessage(BundleKeyConstant.CONFIG_ADDRESS_IS_NULL));
        Assert.notNull(zkUser, ResourceUtil.getMessage(BundleKeyConstant.USER_NAME_IS_NULL));
        Assert.notNull(zkPasswd, ResourceUtil.getMessage(BundleKeyConstant.PASSWD_IS_NULL));
        Assert.notNull(serviceId, "service id is null!");
    }

    private static String getAuthInfo(String zkUser, String zkPasswd) {
        return zkUser + ":" + zkPasswd;
    }

    public static ZKPool getZKPool(String zkAddress, String zkUser, String zkPasswd) throws ConfigException {
        return getZKPool(zkAddress, zkUser, zkPasswd, 2000);
    }

    public static ZKPool getZKPool(String zkAddress, String zkUser, String zkPasswd, String serviceId) throws ConfigException {
        return getZKPool(zkAddress, zkUser, zkPasswd, serviceId, 2000);
    }

    public static ZKPool getZKPool(String zkAddress, int timeOut, String... authInfo) throws ConfigException {
        String zkUser = null;
        String zkPasswd = null;
        if ((null != authInfo) && (authInfo.length >= 2)) {
            if (!StringUtil.isBlank(authInfo[0])) {
                zkUser = authInfo[0];
            }
            if (!StringUtil.isBlank(authInfo[1])) {
                zkPasswd = authInfo[1];
            }
        }
        return getZKPool(zkAddress, zkUser, zkPasswd, 2000);
    }
}
