package com.ai.paas.ipaas.ccs.inner;

import com.ai.paas.ipaas.ccs.constants.ConfigCenterConstants;
import com.ai.paas.ipaas.ccs.inner.util.ConfigSDKUtil;
import com.ai.paas.ipaas.ccs.zookeeper.ZKClient;
import com.ai.paas.ipaas.ccs.zookeeper.impl.ZKPoolFactory;
import com.ai.paas.ipaas.util.CiperUtil;

import org.apache.zookeeper.CreateMode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by astraea on 2015/4/30.
 */
public class TestComponent {

    private final String configAddr = "10.1.228.198:39181,10.1.228.199:39181,10.1.228.200:39181";

    private String userName = "697CB33E9F824CCD856BE85A4EDCA8C6";
    private String userPwd = CiperUtil.decrypt(ConfigCenterConstants.operators, "37768f39318b73de4b724b46646f1e4d");

    private String adminName = "admin";
    private String adminPwd = CiperUtil.decrypt(ConfigCenterConstants.operators, "ec4c9e0e78f76a69");

    private String readOnlyPathA ="/DSS/DSS001";
    private String readonlyValue ="{\"dbName\":\"DSS1\",\"size\":\"128\",\"limitSize\":\"12\"}";
    @SuppressWarnings("unused")
	private String modifyValue = "{'metadata.broker.list':'10.1.228.198:39091,10.1.228.199:39091,10.1.228.202:39091','serializer.class':'kafka.serializer.DefaultEncoder','key.serializer.class':'kafka.serializer.StringEncoder','partitioner.class':'com.ai.paas.ipaas.mds.impl.sender.ModPartitioner','request.required.acks':'1','queue.buffering.max.messages':'1048576','producer.type':'sync','message.send.max.retries':'3','compression.codec':'none','request.timeout.ms':'20000','batch.num.messages':'64000','send.buffer.bytes':'67108864','maxProducer':'5'}";

    @Test
    public void testadd() throws Exception {
        ZKClient client = ZKPoolFactory.getZKPool(configAddr, adminName, adminPwd).getZkClient(configAddr, adminName);

        client.createNode(ConfigSDKUtil.appendUserReadOnlyPathPath(userName) + readOnlyPathA, ConfigSDKUtil.createReadOnlyACL(userName,
                userPwd, adminName, adminPwd), readonlyValue, CreateMode.PERSISTENT);

        assertTrue(client.exists(ConfigSDKUtil.appendUserReadOnlyPathPath(userName) + readOnlyPathA));
    }


    @Test
    public void testGet() throws Exception {
        ZKClient client = ZKPoolFactory.getZKPool(configAddr, adminName, adminPwd).getZkClient(configAddr, adminName);
        System.out.println(client.getNodeData(ConfigSDKUtil.appendUserReadOnlyPathPath(userName) + readOnlyPathA, false));
    }


    @Test
    public void testModify() throws Exception {
        ZKClient client = ZKPoolFactory.getZKPool(configAddr, adminName, adminPwd).getZkClient(configAddr, adminName);
        client.setNodeData(ConfigSDKUtil.appendUserReadOnlyPathPath(userName) + readOnlyPathA, readonlyValue);

        assertEquals(readonlyValue, client.getNodeData(ConfigSDKUtil.appendUserReadOnlyPathPath(userName) + readOnlyPathA, false));
    }

    @Test
    public void testExist() throws Exception {
        ZKClient client = ZKPoolFactory.getZKPool(configAddr, adminName, adminPwd).getZkClient(configAddr, adminName);
        assertTrue(client.exists(ConfigSDKUtil.appendUserWritablePathPath(userName) + readOnlyPathA));
    }


    @Test
    public void testExist1() throws Exception {
        System.out.println(CiperUtil.decrypt(ConfigCenterConstants.operators, "ec4c9e0e78f76a69"));
    }

}
