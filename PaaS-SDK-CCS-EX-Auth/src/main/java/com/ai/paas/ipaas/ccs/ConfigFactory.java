package com.ai.paas.ipaas.ccs;

import com.ai.paas.ipaas.ccs.impl.ConfigClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigFactory {

    private static Logger logger = LogManager.getLogger(ConfigFactory.class);

    private static final int DEFAULT_TIMEOUT = 1000 * 10;

    public static IConfigClient getConfigClient(Properties config, int timeout) throws Exception {
        String zkAddr = config.getProperty("ccs.zk_address", "127.0.0.1:2181");
        String userName = config.getProperty("ccs.userName");
        logger.info("Username:{} zkAddress:{}", userName, zkAddr);
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


    public static IConfigClient getConfigClient(int timeout) throws Exception {
        Properties config = new Properties();
        try {
            config.load(ConfigFactory.class.getResourceAsStream("/ccs.conf"));
        } catch (IOException e) {
            logger.error("Failed to load ccs.conf.", e);
        }

        return getConfigClient(config, timeout);
    }

    public static IConfigClient getConfigClient(InputStream confiFileStream) throws Exception {
        return getConfigClient(confiFileStream, DEFAULT_TIMEOUT);
    }

    public static IConfigClient getConfigClient(InputStream confiFileStream, int timeout) throws Exception {
        Properties config = new Properties();
        try {
            config.load(confiFileStream);
        } catch (IOException e) {
            logger.error("Failed to load ccs.conf.", e);
        }

        return getConfigClient(config, DEFAULT_TIMEOUT);
    }

    public static IConfigClient getConfigClient() throws Exception {
        return getConfigClient(DEFAULT_TIMEOUT);
    }

}
