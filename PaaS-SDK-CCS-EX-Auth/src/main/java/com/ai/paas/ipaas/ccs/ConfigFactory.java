package com.ai.paas.ipaas.ccs;

import com.ai.paas.ipaas.ccs.constants.ConfigCenterConstants;
import com.ai.paas.ipaas.ccs.impl.ConfigClient;
import com.ai.paas.ipaas.util.CiperUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

public class ConfigFactory {

    private static Logger logger = LogManager.getLogger(ConfigFactory.class);
    private static Properties config = new Properties();
    private static final int DEFAULT_TIMEOUT = 1000 * 10;

    static {
        try {
            config.load(ConfigFactory.class.getResourceAsStream("/ccs.conf"));
        } catch (IOException e) {
            logger.error("Failed to load ccs.conf.", e);
        }
    }


    public static IConfigClient getConfigClient(int timeout) throws Exception {
        String zkAddr = config.getProperty("ccs.zk_address", "127.0.0.1:2181");
        String userName = config.getProperty("ccs.userName");
        // check user name
        if (userName == null || userName.length() == 0) {
            throw new IllegalArgumentException("ccs.userName cannot be null");
        }

        String password = config.getProperty("ccs.password");
        // check user password
        if (password == null || password.length() == 0) {
            throw new IllegalArgumentException("ccs.password cannot be null");
        }

        return new ConfigClient.ConfigClientBuilder(zkAddr, userName, password).timeOut(timeout).
                createUserNodeWithAllPermissionIfNecessary().build();
    }

    public static IConfigClient getConfigClient() throws Exception {
        return getConfigClient(DEFAULT_TIMEOUT);
    }

}
