package com.jsti.pile.collector.common;

import java.io.File;

import android.os.Environment;

public class CommonParams {
	final static String SERVER_ADDRESS_TEST = "http://115.28.40.117:4080";// 测试
	final static String SERVER_ADDRESS_PRODUCT = "http://road.cparm.cn";// 正式

	public static final String SERVER_ADDRESS = "http://"+SERVER_ADDRESS_PRODUCT+"/roadpatrol_server";

	private CommonParams() {
	}

	public static final boolean DEBUG = true;

//	public static final String GPS_TYPE = "gcj02";
	public static final String GPS_TYPE = "bd09ll";

	public static final boolean DEBUG_DB = false;

	public static final float DEFAULT_MIN_MOVE_DIST = 5;

	public static final String APP_BOOT_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator
			+ "PileCollector";
	public static final String PATH_COLLECT_RESULT = APP_BOOT_PATH + File.separator + "collect";

	public static final String PATH_DEBUG_DB = APP_BOOT_PATH + File.separatorChar + "databases";

	public static final int PILE_CONFIRM_MAX_SECONDS = 5;
}
