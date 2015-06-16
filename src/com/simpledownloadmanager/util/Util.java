package com.simpledownloadmanager.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {

	public static final String DEFAULT_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

	public static String formatDate(Date date, String pattern) {
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(date);
	}

	public static String getDateTime() {
		return formatDate(new Date(), DEFAULT_DATETIME_PATTERN);
	}
}
