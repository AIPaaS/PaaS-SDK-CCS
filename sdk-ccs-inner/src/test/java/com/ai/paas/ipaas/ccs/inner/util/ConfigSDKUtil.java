package com.ai.paas.ipaas.ccs.inner.util;

import com.ai.paas.ipaas.ccs.constants.ConfigCenterConstants;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class ConfigSDKUtil {
    public static String appendUserReadOnlyPathPath(String userId) {
        return ConfigCenterConstants.UserNodePrefix.FOR_PAAS_PLATFORM_PREFIX + ConfigCenterConstants.SEPARATOR + userId + ConfigCenterConstants.UserNodePrefix.FOR_PAAS_PLATFORM_HAS_READ_PREFIX;
    }

    public static String appendUserWritablePathPath(String userId) {
        return ConfigCenterConstants.UserNodePrefix.FOR_PAAS_PLATFORM_PREFIX + ConfigCenterConstants.SEPARATOR + userId + ConfigCenterConstants.UserNodePrefix.FOR_PAAS_PLATFORM_HAS_WRITABLE_PREFIX;
    }


    public static List<ACL> createReadOnlyACL(String userId, String pwd, String adminUserId, String adminPwd) throws NoSuchAlgorithmException {
        List<ACL> acls = new ArrayList<ACL>();
        Id id1 = new Id("digest", DigestAuthenticationProvider.generateDigest(appendUserAuthInfo(adminUserId, adminPwd)));
        ACL adminACL = new ACL(ZooDefs.Perms.ALL, id1);
        acls.add(adminACL);
        Id id2 = new Id("digest", DigestAuthenticationProvider.generateDigest(appendUserAuthInfo(userId, pwd)));
        ACL userACL = new ACL(ZooDefs.Perms.READ, id2);
        acls.add(userACL);
        return acls;
    }


    public static List<ACL> createWritableACL(String userId, String pwd, String adminUserId, String adminPwd) throws NoSuchAlgorithmException {
        List<ACL> acls = new ArrayList<ACL>();
        Id id1 = new Id("digest", DigestAuthenticationProvider.generateDigest(appendUserAuthInfo(adminUserId, adminPwd)));
        ACL adminACL = new ACL(ZooDefs.Perms.ALL, id1);
        acls.add(adminACL);
        Id id2 = new Id("digest", DigestAuthenticationProvider.generateDigest(appendUserAuthInfo(userId, pwd)));
        ACL userACL = new ACL(ZooDefs.Perms.ALL, id2);
        acls.add(userACL);
        return acls;
    }


    public static String appendUserAuthInfo(String userId, String pwd) {
        return userId + ":" + pwd;
    }
}
