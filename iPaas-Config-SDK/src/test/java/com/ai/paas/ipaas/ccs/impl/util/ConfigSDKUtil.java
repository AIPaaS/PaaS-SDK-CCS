package com.ai.paas.ipaas.ccs.impl.util;

import com.ai.paas.ipaas.ccs.constants.ConfigCenterConstants;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class ConfigSDKUtil {

    public static String appendUserNodePath(String userId, String serviceId) {
        return ConfigCenterConstants.UserNodePrefix.FOR_USER_CUSTOM_PREFIX + ConfigCenterConstants.SEPARATOR + userId + ConfigCenterConstants.SEPARATOR + serviceId;
    }


    public static List<ACL> createWritableACL(String userId, String pwd, String adminName, String adminPwd) throws NoSuchAlgorithmException {
        List<ACL> acls = new ArrayList<ACL>();

        Id id1 = new Id("digest", DigestAuthenticationProvider.generateDigest(appendUserAuthInfo(adminName, adminPwd)));
        ACL userACL = new ACL(ZooDefs.Perms.ALL, id1);
        acls.add(userACL);

        Id id2 = new Id("digest", DigestAuthenticationProvider.generateDigest(appendUserAuthInfo(userId, pwd)));
        ACL userACL1 = new ACL(ZooDefs.Perms.ALL, id2);
        acls.add(userACL1);
        return acls;
    }


    public static String appendUserAuthInfo(String userId, String pwd) {
        return userId + ":" + pwd;
    }
}
