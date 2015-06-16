package com.simpledownloadmanager;

import android.app.Application;

public class DownloadManagerApplication extends Application {
	private static DownloadManagerApplication instance;

	public static DownloadManagerApplication getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		instance = this;
	}
}
