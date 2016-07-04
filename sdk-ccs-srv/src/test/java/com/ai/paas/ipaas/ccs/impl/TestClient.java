package com.ai.paas.ipaas.ccs.impl;

import com.ai.paas.ipaas.ccs.ConfigFactory;
import com.ai.paas.ipaas.ccs.IConfigClient;
import com.ai.paas.ipaas.uac.vo.AuthDescriptor;
import org.junit.Test;

/**
 * Created by astraea on 2015/5/8.
 */
public class TestClient {

    @Test
    public void test() throws Exception {
        AuthDescriptor ad = new AuthDescriptor();
        ad.setPassword("123456");
        ad.setServiceId("CCS007");
        ad.setAuthAdress("http://10.1.245.4:19811/service-portal-uac-web/service/auth");
        ad.setPid("DD9AA862535641EEA67A25F719E37CBD");
        IConfigClient client = ConfigFactory.getConfigClient(ad);
        System.out.println(client.get("/com/ai/opt/paas-mdsns-mds-mapped"));
    }

}
