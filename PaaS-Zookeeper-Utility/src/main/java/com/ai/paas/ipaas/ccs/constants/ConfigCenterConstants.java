package com.ai.paas.ipaas.ccs.constants;


/**
 * 配置中心公共常量
 *
 * @author lixiongcheng
 */
public final class ConfigCenterConstants {
    /**
     * 配置服务信息相对节点
     */
    public static final String CONFIG_SERVICE_NODE_NAME = "/CCS-SERVICE";

    /**
     * 用户节点前缀
     *
     * @author lixiongcheng
     */
    public static class UserNodePrefix {
        public static final String FOR_PAAS_PLATFORM_PREFIX = "/com/ai/paas/ipaas/ccs/user";
        public static final String FOR_PAAS_PLATFORM_HAS_READ_PREFIX = "/readonly";
        public static final String FOR_PAAS_PLATFORM_HAS_WRITABLE_PREFIX = "/writable";
        public static final String FOR_USER_CUSTOM_PREFIX = "/com/ai/paas/ipaas/ccs/custom/user";
    }

    public static class ZKAuthSchema {
        public static final String DIGEST = "digest";
    }

    public static final String operators = "1@3^$aGH;._|$!@#";


    public static final String SEPARATOR = "/";


}
