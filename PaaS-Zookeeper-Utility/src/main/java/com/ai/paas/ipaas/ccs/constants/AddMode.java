package com.ai.paas.ipaas.ccs.constants;

import org.apache.zookeeper.CreateMode;

public enum AddMode {

    PERSISTENT(0),

    PERSISTENT_SEQUENTIAL(2),

    EPHEMERAL(1),

    EPHEMERAL_SEQUENTIAL(3);

    private int flag;

    AddMode(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }

    static public CreateMode convertMode(int flag) throws ConfigException {
        switch (flag) {
            case 0:
                return CreateMode.PERSISTENT;
            case 1:
                return CreateMode.EPHEMERAL;
            case 2:
                return CreateMode.PERSISTENT_SEQUENTIAL;
            case 3:
                return CreateMode.EPHEMERAL_SEQUENTIAL;
            default:
                String errMsg = "Received an invalid flag value: " + flag
                        + " to convert to a CreateMode";
                throw new ConfigException(errMsg);
        }
    }
}
