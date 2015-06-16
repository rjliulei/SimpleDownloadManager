package com.simpledownloadmanager;

public interface DownloadStatusListener {

    //Callback when download is successfully completed
	void onDownloadComplete (int id);

    //Callback if download is failed. Corresponding error code and error messages are provided
    void onDownloadFailed (int id, int errorCode, String errorMessage);

    //Callback provides download progress
	/** 
	 * synchronized注意使用同步关键字
	 * @author liulei	
	 * @date 2015-6-12
	 * @param id
	 * @param totalBytes
	 * @param downloadedBytes
	 * @param progress
	 * @param speed KB/s    
	*/
	 void onProgress (int id, long totalBytes, long downloadedBytes, int progress, int speed);
}
