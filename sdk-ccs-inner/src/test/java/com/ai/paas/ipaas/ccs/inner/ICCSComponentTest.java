package com.ai.paas.ipaas.ccs.inner;

import com.ai.paas.ipaas.ccs.constants.ConfigCenterConstants;
import com.ai.paas.ipaas.ccs.constants.ConfigException;
import com.ai.paas.ipaas.ccs.inner.constants.ConfigPathMode;
import com.ai.paas.ipaas.ccs.inner.util.ConfigSDKUtil;
import com.ai.paas.ipaas.ccs.zookeeper.ConfigWatcher;
import com.ai.paas.ipaas.ccs.zookeeper.ConfigWatcherEvent;
import com.ai.paas.ipaas.ccs.zookeeper.ZKClient;
import com.ai.paas.ipaas.ccs.zookeeper.impl.ZKPoolFactory;
import com.ai.paas.ipaas.util.CiperUtil;
import com.ai.paas.ipaas.util.UUIDTool;

import org.apache.zookeeper.CreateMode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ICCSComponentTest {

    private final String configAddr = "127.0.0.1:2181";
    
    private final String writableValue = "test value";
    private String readonlyValue = "test a ttttAAAA";
    private String modifyValue = "testBBBBB";

    private String userName = UUIDTool.genId32();
    private int userPwd = UUIDTool.genShortId();
    private String adminName = "admin";
    private String adminPwd = CiperUtil.decrypt(ConfigCenterConstants.operators, "ec4c9e0e78f76a69");

    private String parentReadOnlyPath = "/test";
    private String readOnlyPath = parentReadOnlyPath + "/readonly";
    private String readOnlyPathA = parentReadOnlyPath + "/readonlyA";
    private String writablePath = parentReadOnlyPath + "/writable";
    private String removePath = parentReadOnlyPath + "/removePath";

    @Before
    public void setUp() throws Exception {
        ZKClient client = ZKPoolFactory.getZKPool(configAddr, adminName, adminPwd).getZkClient(configAddr, adminName);

        client.createNode(ConfigSDKUtil.appendUserReadOnlyPathPath(userName), ConfigSDKUtil.createReadOnlyACL(userName,
                String.valueOf(userPwd), adminName, adminPwd), readonlyValue, CreateMode.PERSISTENT);
        assertTrue(client.exists(ConfigSDKUtil.appendUserReadOnlyPathPath(userName)));

        client.createNode(ConfigSDKUtil.appendUserWritablePathPath(userName), ConfigSDKUtil.createWritableACL(userName,
                String.valueOf(userPwd), adminName, adminPwd), writableValue, CreateMode.PERSISTENT);
        assertTrue(client.exists(ConfigSDKUtil.appendUserWritablePathPath(userName)));


        client.createNode(ConfigSDKUtil.appendUserReadOnlyPathPath(userName) + readOnlyPath, ConfigSDKUtil.createReadOnlyACL(userName,
                String.valueOf(userPwd), adminName, adminPwd), readonlyValue, CreateMode.PERSISTENT);
        assertTrue(client.exists(ConfigSDKUtil.appendUserReadOnlyPathPath(userName) + readOnlyPath));

        client.createNode(ConfigSDKUtil.appendUserReadOnlyPathPath(userName) + readOnlyPathA, ConfigSDKUtil.createReadOnlyACL(userName,
                String.valueOf(userPwd), adminName, adminPwd), readonlyValue, CreateMode.PERSISTENT);
        assertTrue(client.exists(ConfigSDKUtil.appendUserReadOnlyPathPath(userName) + readOnlyPathA));

        createWritableNode();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNoParameterThrows() throws ConfigException {
        CCSComponentFactory.getConfigClient(configAddr, "admin", "");
    }

    @Test
    private void createWritableNode() throws ConfigException {
        ICCSComponent componentClient = CCSComponentFactory.getConfigClient(configAddr, userName, CiperUtil.encrypt(ConfigCenterConstants.operators, String.valueOf(userPwd)));
        componentClient.add(writablePath, writableValue);
        assertTrue(componentClient.exists(writablePath, ConfigPathMode.WRITABLE));
        assertEquals(writableValue, componentClient.get(writablePath, ConfigPathMode.WRITABLE));

        componentClient.add(removePath, writableValue);
        assertTrue(componentClient.exists(removePath, ConfigPathMode.WRITABLE));
        assertEquals(writableValue, componentClient.get(removePath, ConfigPathMode.WRITABLE));
    }

    @Test
    public void testGetReadOnlyNode() throws ConfigException {
        ICCSComponent componentClient = CCSComponentFactory.getConfigClient(configAddr, userName, CiperUtil.encrypt(ConfigCenterConstants.operators, String.valueOf(userPwd)));
        assertEquals(readonlyValue, componentClient.get(readOnlyPath, ConfigPathMode.READONLY));
    }

    @Test
    public void testExistReadOnlyNode() throws ConfigException {
        ICCSComponent componentClient = CCSComponentFactory.getConfigClient(configAddr, userName, CiperUtil.encrypt(ConfigCenterConstants.operators, String.valueOf(userPwd)));
        assertTrue(componentClient.exists(readOnlyPath));
    }

    @Test
    public void testExistReadOnlyWithConfigPathModeNode() throws ConfigException {
        ICCSComponent componentClient = CCSComponentFactory.getConfigClient(configAddr, userName, CiperUtil.encrypt(ConfigCenterConstants.operators, String.valueOf(userPwd)));
        assertTrue(componentClient.exists(readOnlyPath, ConfigPathMode.READONLY));
    }


    @Test
    public void testNoExistReadOnlyWithConfigPathModeNode() throws ConfigException {
        ICCSComponent componentClient = CCSComponentFactory.getConfigClient(configAddr, userName, CiperUtil.encrypt(ConfigCenterConstants.operators, String.valueOf(userPwd)));
        assertFalse(componentClient.exists(readOnlyPath + 1, ConfigPathMode.READONLY));
    }

    @Test
    public void testNoExistReadOnlyNode() throws ConfigException {
        ICCSComponent componentClient = CCSComponentFactory.getConfigClient(configAddr, userName, CiperUtil.encrypt(ConfigCenterConstants.operators, String.valueOf(userPwd)));
        assertFalse(componentClient.exists(readOnlyPath + 1));
    }

    @Test
    public void testModifyNodeByStringValue() throws ConfigException {
        ICCSComponent componentClient = CCSComponentFactory.getConfigClient(configAddr, userName, CiperUtil.encrypt(ConfigCenterConstants.operators, String.valueOf(userPwd)));
        componentClient.modify(writablePath, modifyValue);

        assertEquals(modifyValue, componentClient.get(writablePath, ConfigPathMode.WRITABLE));
    }

    @Test(expected = ConfigException.class)
    public void testModifyNoExistsNodeByStringValue() throws ConfigException {
        ICCSComponent componentClient = CCSComponentFactory.getConfigClient(configAddr, userName, CiperUtil.encrypt(ConfigCenterConstants.operators, String.valueOf(userPwd)));
        componentClient.modify(writablePath + 1, modifyValue);
    }

    @Test
    public void testModifyNodeByBytesValue() throws ConfigException {
        byte[] data = new byte[]{110, 10, 1, 20, 30};
        ICCSComponent componentClient = CCSComponentFactory.getConfigClient(configAddr, userName, CiperUtil.encrypt(ConfigCenterConstants.operators, String.valueOf(userPwd)));
        componentClient.modify(writablePath, data);

        assertArrayEquals(data, componentClient.readBytes(writablePath, ConfigPathMode.WRITABLE));
    }

    @Test(expected = ConfigException.class)
    public void testModifyNoExistsNodeByBytesValue() throws ConfigException {
        byte[] data = new byte[]{110, 10, 1, 20, 30};
        ICCSComponent componentClient = CCSComponentFactory.getConfigClient(configAddr, userName, CiperUtil.encrypt(ConfigCenterConstants.operators, String.valueOf(userPwd)));
        componentClient.modify(writablePath + 1, data);
    }


    @Test
    public void testReadOnlySubChild() throws ConfigException {
        ICCSComponent componentClient = CCSComponentFactory.getConfigClient(configAddr, userName, CiperUtil.encrypt(ConfigCenterConstants.operators, String.valueOf(userPwd)));
        List<String> childrenPath = componentClient.listSubPath(parentReadOnlyPath);

        assertEquals(2, childrenPath.size());
    }

    @Test(expected = ConfigException.class)
    public void testReadOnlyNoExistNodeSubChild() throws ConfigException {
        ICCSComponent componentClient = CCSComponentFactory.getConfigClient(configAddr, userName, CiperUtil.encrypt(ConfigCenterConstants.operators, String.valueOf(userPwd)));
        @SuppressWarnings("unused")
		List<String> childrenPath = componentClient.listSubPath(parentReadOnlyPath + 1);
    }

    @Test
    public void testReadOnlyNoSubChild() throws ConfigException {
        ICCSComponent componentClient = CCSComponentFactory.getConfigClient(configAddr, userName, CiperUtil.encrypt(ConfigCenterConstants.operators, String.valueOf(userPwd)));
        List<String> childrenPath = componentClient.listSubPath(readOnlyPath);

        assertNotNull(childrenPath);
        assertEquals(0, childrenPath.size());
    }

    @Test
    public void testAddExistNode() throws ConfigException {
        ICCSComponent componentClient = CCSComponentFactory.getConfigClient(configAddr, userName, CiperUtil.encrypt(ConfigCenterConstants.operators, String.valueOf(userPwd)));
        componentClient.add(readOnlyPath, "testValue");
        assertTrue(componentClient.exists(readOnlyPath, ConfigPathMode.WRITABLE));

        assertEquals("testValue", componentClient.get(readOnlyPath, ConfigPathMode.WRITABLE));
    }

    @Test
    public void testWritableSubChild() throws ConfigException {
        ICCSComponent componentClient = CCSComponentFactory.getConfigClient(configAddr, userName, CiperUtil.encrypt(ConfigCenterConstants.operators, String.valueOf(userPwd)));
        List<String> childrenPath = componentClient.listSubPath(parentReadOnlyPath, ConfigPathMode.WRITABLE);
        assertEquals(2, childrenPath.size());
    }

    @Test(expected = ConfigException.class)
    public void testWritableNoExistsSubChild() throws ConfigException {
        ICCSComponent componentClient = CCSComponentFactory.getConfigClient(configAddr, userName, CiperUtil.encrypt(ConfigCenterConstants.operators, String.valueOf(userPwd)));
        @SuppressWarnings("unused")
		List<String> childrenPath = componentClient.listSubPath(parentReadOnlyPath + 1, ConfigPathMode.WRITABLE);
    }

    @Test
    public void testRemove() throws ConfigException {
        ICCSComponent componentClient = CCSComponentFactory.getConfigClient(configAddr, userName, CiperUtil.encrypt(ConfigCenterConstants.operators, String.valueOf(userPwd)));
        componentClient.remove(removePath);
        assertFalse(componentClient.exists(removePath, ConfigPathMode.WRITABLE));
    }

    @After
    public void clearData() throws Exception {
        ZKClient client = ZKPoolFactory.getZKPool(configAddr, adminName, adminPwd).getZkClient(configAddr, adminName).addAuth("digest",
                userName + ":" + String.valueOf(userPwd));
        client.deleteNode(ConfigCenterConstants.UserNodePrefix.FOR_PAAS_PLATFORM_PREFIX + ConfigCenterConstants.SEPARATOR + userName);
        assertFalse(client.exists(ConfigCenterConstants.UserNodePrefix.FOR_PAAS_PLATFORM_PREFIX + ConfigCenterConstants.SEPARATOR + userName));
    }


    @Test
    public void testAdd() throws Exception {
        ICCSComponent componentClient = CCSComponentFactory.getConfigClient(configAddr, userName, CiperUtil.encrypt(ConfigCenterConstants.operators, String.valueOf(userPwd)));
        componentClient.get(parentReadOnlyPath, ConfigPathMode.WRITABLE, new ConfigWatcher() {
            @Override
            public void processEvent(ConfigWatcherEvent event) {
                System.out.println("AAABBB");
            }
        });


        componentClient.get(parentReadOnlyPath, ConfigPathMode.WRITABLE, new ConfigWatcher() {
            @Override
            public void processEvent(ConfigWatcherEvent event) {
                System.out.println("AAABBB");
            }
        });
    }

    @Test
    public void testListSub() throws Exception {
        ICCSComponent componentClient = CCSComponentFactory.getConfigClient(configAddr, userName, CiperUtil.encrypt(ConfigCenterConstants.operators, String.valueOf(userPwd)));
        componentClient.listSubPath(parentReadOnlyPath, ConfigPathMode.WRITABLE, new ConfigWatcher() {
            @Override
            public void processEvent(ConfigWatcherEvent event) {
                System.out.println("AAABBB");
            }
        });


        componentClient.listSubPath(parentReadOnlyPath, ConfigPathMode.WRITABLE, new ConfigWatcher() {
            @Override
            public void processEvent(ConfigWatcherEvent event) {
                System.out.println("AAABBB");
            }
        });
    }


}
