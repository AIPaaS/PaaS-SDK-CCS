package com.ai.paas.ipaas.ccs.constants;

import com.ai.paas.util.ResourceUtil;

public class BundleKeyConstant {

    static {
        ResourceUtil.addBundle("com.ai.paas.ipaas.ccs.ipaas-config");
    }

    private BundleKeyConstant() {

    }

    /**
     * 配置地址为空
     */
    public static final String CONFIG_ADDRESS_IS_NULL = "com.ai.paas.ipaas.config.configaddr_null";

    /**
     * 配置用户名为空
     */
    public static final String USER_NAME_IS_NULL = "com.ai.paas.ipaas.config.username_null";

    /**
     * 配置密码为空
     */
    public static final String PASSWD_IS_NULL = "com.ai.paas.ipaas.config.passwd_null";

    /**
     * 用户节点为空
     */
    public static final String USER_NODE_NOT_EXISTS = "com.ai.paas.ipaas.config.user_node_not_exists";

    /**
     * 获取Config Client失败
     */
    public static final String GET_CONFIG_CLIENT_FAILED = "com.ai.paas.ipaas.config.resource_client_failed";

    /**
     * 用户认证失败
     */
    public static final String USER_AUTH_FAILED = "com.ai.paas.ipaas.config.user_auth_failed";

    /**
     * 认证过程失败
     */
    public static final String AUTH_FAILED = "com.ai.paas.ipaas.config.auth_failed";

    /**
     * 归还Config Client资源失败
     */
    public static final String RETURN_RESOURCE_FAILED = "com.ai.paas.ipaas.config.return_resource_failed";

    /**
     * 转换数据失败
     */
    public static final String CONVERT_DATA_FAILED = "com.ai.paas.ipaas.config.convert_data_failed";

    /**
     * 配置节点已经存在
     */
    public static final String PATH_EXISTS = "com.ai.paas.ipaas.config.path_exists";

    /**
     * 添加配置失败
     */
    public static final String ADD_CONFIG_FAILED = "com.ai.paas.ipaas.config.add_config_failed";

    /**
     * 配置节点不存在
     */
    public static final String PATH_NOT_EXISTS = "com.ai.paas.ipaas.config.path_not_exists";

    /**
     * 获取分布式锁失败
     */
    public static final String GET_LOCK_FAILED = "com.ai.paas.ipaas.config.get_lock_failed";

    /**
     * 修改失败
     */
    public static final String MODIFY_FAILED = "com.ai.paas.ipaas.config.modify_failed";

    /**
     * 获取子配置失败
     */
    public static final String LIST_CHILDREN_FAILED = "com.ai.paas.ipaas.config.list_children_failed";

    /**
     * 移除配置失败
     */
    public static final String REMOVE_CONFIG_FAILED = "com.ai.paas.ipaas.config.remove_config_failed";

    /**
     * 获取配置失败
     */
    public static final String GET_CONFIG_VALUE_FAILED = "com.ai.paas.ipaas.config.get_config_value_failed";

    /**
     * 路径格式错误
     */
    public static final String PATH_ILL = "com.ai.paas.ipaas.config.path_ill";

    public static final String SERVICEID_IS_NULL = "com.ai.paas.ipaas.config.serviceid_is_null";
}
