package com.ai.paas.ipaas.ccs;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class ConfigFactoryTest {

    @Test
    public void test() throws Exception {
        ConfigFactory.getConfigClient().add("/test-add", "1");
        String value = ConfigFactory.getConfigClient().get("/test-add");
        assertEquals(value, "1");
    }
}
