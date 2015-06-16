package com.simpledownloadmanager.db;

import com.j256.ormlite.field.DatabaseField;

/**
 * 保存每个下载线程下载信息类 取消和删除的文件不需要存储
 */
public class DownloadInfo {
	@DatabaseField(generatedId = true)
	private int id; // 下载的id
	@DatabaseField
	private String url; // 下载文件的URL地址
	@DatabaseField
	private String date;
	/**
	 * {@link com.simpledownloadmanager.DownloadManager}
	 */
	@DatabaseField
	private int state;
	@DatabaseField
	private String destinationPath;
	@DatabaseField
	private String tmpPath;
	@DatabaseField
	private long fileSize;
	@DatabaseField
	private String errorMsg;

	public DownloadInfo() {
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public String getDestinationPath() {
		return destinationPath;
	}

	public void setDestinationPath(String destinationPath) {
		this.destinationPath = destinationPath;
	}

	public String getTmpPath() {
		return tmpPath;
	}

	public void setTmpPath(String tmpPath) {
		this.tmpPath = tmpPath;
	}

	/** 获取下载地址 */
	public String getUrl() {
		return url;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
}
