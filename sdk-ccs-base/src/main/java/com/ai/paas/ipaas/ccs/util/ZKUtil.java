package com.ai.paas.ipaas.ccs.util;

import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import com.ai.paas.Constant;
import com.ai.paas.ipaas.ccs.constants.ConfigConstant;
import com.ai.paas.util.StringUtil;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by astraea on 2015/4/28.
 */
public class ZKUtil {
    private ZKUtil() {

    }

    public static List<ACL> createWritableACL(String authInfo) throws NoSuchAlgorithmException {
        List<ACL> acls = new ArrayList<>();
        Id id2 = new Id(ConfigConstant.ZKAuthSchema.DIGEST, DigestAuthenticationProvider.generateDigest(authInfo));
        ACL userACL = new ACL(ZooDefs.Perms.ALL, id2);
        acls.add(userACL);
        return acls;
    }

    public static String processPath(String nodePath) {
        String path = nodePath;
        if (StringUtil.isBlank(path))
            return path;
        if (path.charAt(0) != '/')
            path = Constant.UNIX_SEPERATOR + path;
        return path;
    }

}
