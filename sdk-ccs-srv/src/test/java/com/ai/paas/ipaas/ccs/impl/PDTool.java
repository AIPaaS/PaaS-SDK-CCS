package com.ai.paas.ipaas.ccs.impl;

import java.security.NoSuchAlgorithmException;

import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import com.ai.paas.ipaas.util.CiperUtil;

public class PDTool {

	public static void main(String[] args) throws NoSuchAlgorithmException {
		String operators = "1@3^$aGH;._|$!@#";
		System.out.println(DigestAuthenticationProvider.generateDigest("ECBCA29571714183B23A630E2311DD66:-536482680"));
		System.out.println(CiperUtil.encrypt(operators, "123456"));
		System.out.println(CiperUtil.decrypt(operators, "eae43959e5a4aa2221e343ccd3546ef1"));
		
	}

}
