package com.ai.paas.ipaas.ccs.zookeeper.impl;

import com.ai.paas.ipaas.ccs.zookeeper.ZKClient;

import java.util.concurrent.ConcurrentHashMap;

public class ZKPool {
    private static ConcurrentHashMap<String, ZKClient> clients = new ConcurrentHashMap<String, ZKClient>();

    public ZKClient getZkClient(final String zkAddr, final String zkUserName) throws Exception {
        return clients.get(appendKey(zkAddr, zkUserName));
    }
    
    public ZKClient getZkClient(final String zkAddr, final String zkUserName ,final String serviceId) throws Exception {
        return clients.get(appendKey(zkAddr, zkUserName,serviceId));
    }

    private String appendKey(String zkAddr, String zkUserName) {
        return zkAddr + "-" + zkUserName;
    }
    
    private String appendKey(String zkAddr, String zkUserName,String serviceId) {
        return zkAddr + "-" + zkUserName + "-" + serviceId;
    }

    public void addZKClient(final String zkAddr, final String zkUserName, ZKClient zkClient) {
        clients.put(appendKey(zkAddr, zkUserName), zkClient);
    }
    
    public void addZKClient(final String zkAddr, final String zkUserName, final String serviceId ,ZKClient zkClient) {
        clients.put(appendKey(zkAddr, zkUserName,serviceId), zkClient);
    }

    public boolean exist(final String zkAddr, final String zkUserName) {
        return clients.containsKey(appendKey(zkAddr, zkUserName));
    }
    
    public boolean exist(final String zkAddr, final String zkUserName,final String serviceId) {
        return clients.containsKey(appendKey(zkAddr, zkUserName,serviceId));
    }

}
