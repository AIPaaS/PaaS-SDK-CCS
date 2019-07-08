package test.ccs;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ai.paas.ipaas.ccs.zookeeper.ZKClient;

public class ZKClientBenchTest {
    private ZKClient zkClient = null;

    @Before
    public void setUp() throws InterruptedException {
        zkClient = new ZKClient("10.19.10.88:18183", 20000);
    }

    @After
    public void tearDown() {
        if (null != zkClient) {
            zkClient = null;
        }
    }

    @Test
    public void testInsert() throws Exception {
        // 循环插入10000个节点，每个下面在10000节点，两层
        long start=System.currentTimeMillis();
        zkClient.deleteNode("/dxf");
        zkClient.createNode("/dxf", null);
        for (int i = 0; i < 10000; i++) {
            zkClient.createNode("/dxf/test" + i,
                    "this is a testthis is a testthis is a testthis is a testthis is a test");
        }
        long end=System.currentTimeMillis();
        System.out.println(end-start);
    }

}
