package com.ai.paas.ipaas.ccs.inner.constants;

import com.ai.paas.ipaas.ccs.constants.ConfigConstant;
import com.ai.paas.ipaas.ccs.constants.ConfigException;
import com.ai.paas.util.ResourceUtil;

/**
 * Created by astraea on 2015/4/28.
 */
public enum ConfigPathMode {
    WRITABLE(1), READONLY(2);

    private int flag;

    ConfigPathMode(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }

    static public String appendPath(String userName, int flag, String path) {
        if (!path.startsWith("/") || path.length() < 2)
            throw new ConfigException(ResourceUtil.getMessage("com.ai.paas.ipaas.config.path_ill"));

        switch (flag) {
        case 1:
            return ConfigConstant.UserNodePrefix.FOR_PAAS_PLATFORM_PREFIX + "/" + userName
                    + ConfigConstant.UserNodePrefix.FOR_PAAS_PLATFORM_HAS_WRITABLE_PREFIX + path;
        case 2:
            return ConfigConstant.UserNodePrefix.FOR_PAAS_PLATFORM_PREFIX + "/" + userName
                    + ConfigConstant.UserNodePrefix.FOR_PAAS_PLATFORM_HAS_READ_PREFIX + path;
        default:
            String errMsg = "Received an invalid flag value: " + flag + " to convert to a ConfigPathMode";
            throw new ConfigException(errMsg);
        }
    }
}
