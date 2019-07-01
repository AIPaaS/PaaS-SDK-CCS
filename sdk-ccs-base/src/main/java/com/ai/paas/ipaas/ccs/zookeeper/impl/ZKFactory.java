package com.ai.paas.ipaas.ccs.zookeeper.impl;

import com.ai.paas.ipaas.ccs.constants.BundleKeyConstant;
import com.ai.paas.ipaas.ccs.constants.ConfigConstant;
import com.ai.paas.ipaas.ccs.constants.ConfigException;
import com.ai.paas.ipaas.ccs.zookeeper.ZKClient;
import com.ai.paas.util.Assert;
import com.ai.paas.util.ResourceUtil;
import com.ai.paas.util.StringUtil;

public class ZKFactory {
    private static int timeOut = 60000;

    private ZKFactory() {

    }

    public static ZKClient getZkClient(final String zkAddr) throws InterruptedException {
        return new ZKClient(zkAddr, timeOut);
    }

    public static ZKClient getZkClient(final String zkAddr, final String zkUserName) throws InterruptedException {
        return new ZKClient(zkAddr, timeOut);
    }

    public static ZKClient getZkClient(String zkAddress, String zkUser, String zkPasswd, int timeOut) {
        validateParam(zkAddress, zkUser, zkPasswd);
        return getZkClient(zkAddress, zkUser, zkPasswd, null, timeOut);
    }

    public static ZKClient getZkClient(String zkAddress, String zkUser, String zkPasswd) {
        return getZkClient(zkAddress, zkUser, zkPasswd, 60000);
    }

    public static ZKClient getZkClient(String zkAddress, String zkUser, String zkPasswd, String serviceId) {
        validateParam(zkAddress, zkUser, zkPasswd, serviceId);
        return getZkClient(zkAddress, zkUser, zkPasswd, serviceId, 60000);
    }

    public static ZKClient getZkClient(String zkAddress, int timeOut, String... authInfo) {
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
        return getZkClient(zkAddress, zkUser, zkPasswd, null, timeOut);
    }

    public static ZKClient getZkClient(String zkAddress, String zkUser, String zkPasswd, String serviceId,
            int timeOut) {
        ZKClient client = null;
        try {
            // 新建
            if (!StringUtil.isBlank(zkUser) && !StringUtil.isBlank(zkPasswd)) {
                client = new ZKClient(zkAddress, timeOut,
                        new String[] { ConfigConstant.ZKAuthSchema.DIGEST, getAuthInfo(zkUser, zkPasswd) });
                client.addAuth(ConfigConstant.ZKAuthSchema.DIGEST, getAuthInfo(zkUser, zkPasswd));
            } else {
                client = new ZKClient(zkAddress, timeOut, new String[] {});
            }
        } catch (Exception e) {
            throw new ConfigException(ResourceUtil.getMessage(BundleKeyConstant.GET_CONFIG_CLIENT_FAILED));
        }

        return client;
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
