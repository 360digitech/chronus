package com.qihoo.finance.chronus.storage.h2.plugin.util;

import java.util.UUID;

public class H2IdUtil {

	public static String getId() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

}
