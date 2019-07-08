package com.ai.paas.ipaas.ccs.zookeeper;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ZKClientTest {

    private ZKClient zkClient = null;

    @Before
    public void setUpBeforeClass() throws InterruptedException {
        zkClient = new ZKClient("10.19.10.88:18183", 20000);
    }

    @After
    public void tearDownAfterClass() {
        if (null != zkClient) {
            zkClient.quit();
            zkClient = null;
        }
    }

    @Test
    public void testGetNodeDataStringBoolean() throws Exception {
        zkClient.deleteNode("/test/zk/1");
        zkClient.createNode("/test/zk/1", "Test");
        String data = zkClient.getNodeData("/test/zk/1",true);
        assertTrue(data.equals("Test"));
        zkClient.deleteNode("/test/zk/1");
    }

    @Test
    public void testGetNodeDataString() throws Exception {
        zkClient.deleteNode("/test/zk/1");
        zkClient.createNode("/test/zk/1", "Test");
        String data = zkClient.getNodeData("/test/zk/1");
        assertTrue(data.equals("Test"));
        zkClient.deleteNode("/test/zk/1");
    }

    @Test
    public void testGetNodeDataStringWatcher() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetNodeBytesStringWatcher() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetNodeBytesString() {
        fail("Not yet implemented");
    }

    @Test
    public void testCreateNodeStringStringCreateMode() {
        fail("Not yet implemented");
    }

    @Test
    public void testCreateNodeStringListOfACLStringCreateMode() {
        fail("Not yet implemented");
    }

    @Test
    public void testCreateNodeStringListOfACLByteArrayCreateMode() {
        fail("Not yet implemented");
    }

    @Test
    public void testCreateNodeStringString() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetNodeDataStringString() {
        fail("Not yet implemented");
    }

    @Test
    public void testSetNodeDataStringByteArray() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetChildrenStringWatcher() {
        fail("Not yet implemented");
    }

    @Test
    public void testCreateSeqNode() {
        fail("Not yet implemented");
    }

    @Test
    public void testExistsString() {
        fail("Not yet implemented");
    }

    @Test
    public void testExistsStringWatcher() {
        fail("Not yet implemented");
    }

    @Test
    public void testIsConnected() {
        fail("Not yet implemented");
    }

    @Test
    public void testRetryConnection() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetChildrenString() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetChildrenStringBoolean() {
        fail("Not yet implemented");
    }

    @Test
    public void testDeleteNode() {
        fail("Not yet implemented");
    }

    @Test
    public void testQuit() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetInterProcessLock() {
        fail("Not yet implemented");
    }

}
