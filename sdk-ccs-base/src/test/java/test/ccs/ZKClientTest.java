package test.ccs;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ai.paas.ipaas.ccs.zookeeper.ZKClient;

public class ZKClientTest {
	private ZKClient zkClient = null;

	@Before
	public void setUp() throws Exception {
		zkClient = new ZKClient("10.1.245.224:30181,10.1.245.225:30281,10.1.245.8:30281", 20000);
	}

	@After
	public void tearDown() throws Exception {
		if (null != zkClient) {
			zkClient = null;
		}
	}

	@Test
	public void testInsert() throws Exception {
		// 循环插入10000个节点，每个下面在10000节点，两层
		for (int i = 0; i < 10000; i++) {
			zkClient.createNode("/test" + i, "this is a testthis is a testthis is a testthis is a testthis is a test");
			for (int j = 0; j < 10000; j++) {
				zkClient.createNode("/test" + i + "/test" + j,
						"sendondy node,sendondy node,sendondy node,sendondy node,");
			}
		}
	}

}
