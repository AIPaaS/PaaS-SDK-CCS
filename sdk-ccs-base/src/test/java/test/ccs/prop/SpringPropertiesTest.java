package test.ccs.prop;

import java.io.StringWriter;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ai.paas.ipaas.ccs.zookeeper.ZKClient;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class SpringPropertiesTest {

	@Before
	public void setUp() throws Exception {
		ZKClient zkClient = new ZKClient("127.0.0.1:2181", 60000);
		Properties prop = new Properties();
		prop.setProperty("test", "123456");
		prop.setProperty("dxf", "654321");
		StringWriter writer = new StringWriter();
		prop.store(writer, "");
		if(!zkClient.exists("/com/ai/test/test")){
			zkClient.createNode("/com/ai/test/test", "");
		}
		zkClient.setNodeData("/com/ai/test/test", writer.getBuffer().toString());
		Gson gson = new Gson();
		JsonObject json = new JsonObject();
		json.addProperty("ssoServer", "http://10.1.235.24:18060/chan-sso");
		json.addProperty("client", "127.0.0.1");
		if (zkClient.exists("/com/ai/test/dxf")) {

		} else {
			zkClient.createNode("/com/ai/test/dxf", gson.toJson(json));
		}
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testProp() {
		final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "classpath*:core-context.xml" });
		context.registerShutdownHook();
		context.start();
		System.out.println(context.getBeanFactory().resolveEmbeddedValue("${test}"));
		System.out.println(context.getBeanFactory().resolveEmbeddedValue("${dxf}"));
		context.close();
	}

}
