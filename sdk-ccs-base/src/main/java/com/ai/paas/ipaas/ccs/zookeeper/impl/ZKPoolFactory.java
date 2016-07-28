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
        return getZkPool0(zkAddress, zkUser, zkPasswd, null, timeOut);
    }

    public static ZKPool getZKPool(String zkAddress, String zkUser, String zkPasswd, String serviceId, int timeOut) throws ConfigException {
        validateParam(zkAddress, zkUser, zkPasswd, serviceId);
        return getZkPool0(zkAddress, zkUser, zkPasswd, serviceId, timeOut);
    }

    public static ZKPool getZKPool(String zkAddress, String zkUser, String zkPasswd) throws ConfigException {
        return getZKPool(zkAddress, zkUser, zkPasswd, 60000);
    }

    public static ZKPool getZKPool(String zkAddress, String zkUser, String zkPasswd, String serviceId) throws ConfigException {
        return getZKPool(zkAddress, zkUser, zkPasswd, serviceId, 60000);
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
        return getZkPool0(zkAddress, zkUser, zkPasswd, null, timeOut);
    }

    private static ZKPool getZkPool0(String zkAddress, String zkUser, String zkPasswd, String serviceId, int timeOut) throws ConfigException {
        if (zkPool.exist(zkAddress, zkUser, serviceId)) {
            return zkPool;
        }
        ZKClient client = null;
        try {
            if (!StringUtil.isBlank(zkUser) && !StringUtil.isBlank(zkPasswd)) {
                client = new ZKClient(zkAddress, timeOut, new String[]{ConfigCenterConstants.ZKAuthSchema.DIGEST, getAuthInfo(zkUser, zkPasswd)});
                client.addAuth(ConfigCenterConstants.ZKAuthSchema.DIGEST, getAuthInfo(zkUser, zkPasswd));
            } else {
                client = new ZKClient(zkAddress, timeOut, new String[]{});
            }
        } catch (Exception e) {
            throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.GET_CONFIG_CLIENT_FAILED));
        }
        zkPool.addZKClient(zkAddress, zkUser, serviceId, client);
        return zkPool;
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
        Assert.notNull(serviceId, ResourceUtil.getMessage(BundleKeyConstant.SERVICEID_IS_NULL));
    }

    private static String getAuthInfo(String zkUser, String zkPasswd) {
        return zkUser + ":" + zkPasswd;
    }



}
