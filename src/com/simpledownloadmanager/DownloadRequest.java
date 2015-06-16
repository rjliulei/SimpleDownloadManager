package com.simpledownloadmanager;

import java.util.HashMap;

import com.simpledownloadmanager.db.DownloadInfo;
import com.simpledownloadmanager.db.DownloadInfoUnit;
import com.simpledownloadmanager.util.FileUtil;
import com.simpledownloadmanager.util.Util;

import android.net.Uri;

public class DownloadRequest implements Comparable<DownloadRequest> {

	public static final String SUFFIX_TMP_NAME = ".tmp";

	/** Tells the current download state of this request */
	// private int mDownloadState;

	/** Download Id assigned to this request */
	// private int mDownloadId;

	/** The URI resource that this request is to download */
	private Uri mUri;

	/**
	 * The destination path on the device where the downloaded files needs to be
	 * put It can be either External Directory ( SDcard ) or internal app cache
	 * or files directory. For using external SDCard access, application should
	 * have this permission android.permission.WRITE_EXTERNAL_STORAGE declared.
	 */
	private Uri mDestinationURI;
	private Uri tmpUri;

	private DownloadInfo infoInDB;
	private DownloadInfoUnit unit;

	private RetryPolicy mRetryPolicy;

	/** Whether or not this request has been canceled. */
	private boolean mCanceled = false;

	private DownloadRequestQueue mRequestQueue;

	private DownloadStatusListener mDownloadListener;

	private HashMap<String, String> mCustomHeader;

	/**
	 * Priority values. Requests will be processed from higher priorities to
	 * lower priorities, in FIFO order.
	 */
	public enum Priority {
		LOW, NORMAL, HIGH, IMMEDIATE
	}

	private Priority mPriority = Priority.NORMAL;

	public DownloadRequest(Uri uri) {
		this(uri, null);
	}

	/**
	 * <p>
	 * Title:
	 * </p>
	 * <p>
	 * Description: 新建下载任务
	 * </p>
	 * 
	 * @param uri
	 */
	public DownloadRequest(Uri uri, Uri destinationURI) {
		if (uri == null) {
			throw new NullPointerException();
		}

		if (null == destinationURI) {
			destinationURI = Uri.parse(FileUtil.DEFAULT_DOWNLOAD_PATH);
		}

		String scheme = uri.getScheme();
		if (scheme == null || (!scheme.equals("http") && !scheme.equals("https"))) {
			throw new IllegalArgumentException("Can only download HTTP/HTTPS URIs: " + uri);
		}

		mUri = uri;
		String[] parts = uri.toString().split("/");
		String fileName = parts[parts.length - 1];
		String dest = destinationURI.toString() + fileName;
		this.mDestinationURI = Uri.parse(dest);
		String tmp = dest + SUFFIX_TMP_NAME;
		this.tmpUri = Uri.parse(tmp);

		unit = new DownloadInfoUnit(DownloadManagerApplication.getInstance());
		infoInDB = unit.getInfoByURL(mUri.toString());

		if (null == infoInDB) {
			infoInDB = new DownloadInfo();
			infoInDB.setState(DownloadManager.STATUS_PENDING);

			infoInDB.setUrl(uri.toString());
			infoInDB.setDestinationPath(dest);
			infoInDB.setTmpPath(tmp);
			infoInDB.setDate(Util.getDateTime());
			unit.createItem(infoInDB);
		} else {

			infoInDB.setUrl(uri.toString());
			infoInDB.setDestinationPath(dest);
			infoInDB.setTmpPath(tmp);
			infoInDB.setDate(Util.getDateTime());
			unit.updateItem(infoInDB);
		}

		mCustomHeader = new HashMap<String, String>();
	}

	/**
	 * <p>
	 * Title:
	 * </p>
	 * <p>
	 * Description:数据库中已有的下载创建下载请求,确保您的数据来自本地存储的数据
	 * </p>
	 * 
	 * @param info
	 */
	public DownloadRequest(DownloadInfo info) {
		if (info == null) {
			throw new NullPointerException();
		}

		mUri = Uri.parse(info.getUrl());
		this.mDestinationURI = Uri.parse(info.getDestinationPath());
		String tmp = mDestinationURI.toString() + SUFFIX_TMP_NAME;
		this.tmpUri = Uri.parse(tmp);

		unit = new DownloadInfoUnit(DownloadManagerApplication.getInstance());
		infoInDB = info;
		infoInDB.setDate(Util.getDateTime());

		mCustomHeader = new HashMap<String, String>();
	}

	/**
	 * Returns the {@link Priority} of this request; {@link Priority#NORMAL} by
	 * default.
	 */
	public Priority getPriority() {
		return mPriority;
	}

	/**
	 * Set the {@link Priority} of this request;
	 * 
	 * @param priority
	 * @return request
	 */
	public DownloadRequest setPriority(Priority priority) {
		mPriority = priority;
		return this;
	}

	/**
	 * Adds custom header to request
	 * 
	 * @param key
	 * @param value
	 */
	public DownloadRequest addCustomHeader(String key, String value) {
		mCustomHeader.put(key, value);
		return this;
	}

	/**
	 * Associates this request with the given queue. The request queue will be
	 * notified when this request has finished.
	 */
	void setDownloadRequestQueue(DownloadRequestQueue downloadQueue) {
		mRequestQueue = downloadQueue;
	}

	public RetryPolicy getRetryPolicy() {
		return mRetryPolicy == null ? new DefaultRetryPolicy() : mRetryPolicy;
	}

	public DownloadRequest setRetryPolicy(RetryPolicy mRetryPolicy) {
		this.mRetryPolicy = mRetryPolicy;
		return this;
	}

	/**
	 * Sets the download Id of this request. Used by
	 * {@link DownloadRequestQueue}.
	 */
	// final void setDownloadId(int downloadId) {
	// mDownloadId = downloadId;
	// }

	final int getDownloadId() {
		return infoInDB.getId();
	}

	int getDownloadState() {
		return infoInDB.getState();
	}

	void setDownloadState(int mDownloadState, String msg) {
		infoInDB.setState(mDownloadState);
		infoInDB.setErrorMsg(null == msg ? "" : msg);
		synchronized (unit) {
			unit.updateItem(infoInDB);
		}
	}

	DownloadStatusListener getDownloadListener() {
		return mDownloadListener;
	}

	public DownloadRequest setDownloadListener(DownloadStatusListener downloadListener) {
		this.mDownloadListener = downloadListener;
		return this;
	}

	public Uri getUri() {
		return mUri;
	}

	public Uri getDestinationURI() {
		return mDestinationURI;
	}

	// public DownloadRequest setDestinationURI(Uri destinationURI) {
	//
	// return this;
	// }

	public Uri getTempURI() {
		return tmpUri;
	}

	public DownloadInfo getDownloadInfo() {
		return infoInDB;
	}

	// Package-private methods.

	/**
	 * Mark this request as canceled. No callback will be delivered.
	 */
	public void cancel() {
		mCanceled = true;
	}

	/**
	 * Returns true if this request has been canceled.
	 */
	public boolean isCanceled() {
		return mCanceled;
	}

	void deleteInfoInDB() {
		unit.deleteItem(infoInDB);
	}

	void saveFileSize(long size) {
		infoInDB.setFileSize(size);
		unit.updateItem(infoInDB);
	}

	/**
	 * Returns all custom headers set by user
	 * 
	 * @return
	 */
	HashMap<String, String> getCustomHeaders() {
		return mCustomHeader;
	}

	void finish() {
		mRequestQueue.finish(this);
	}

	@Override
	public int compareTo(DownloadRequest other) {
		Priority left = this.getPriority();
		Priority right = other.getPriority();

		// High-priority requests are "lesser" so they are sorted to the front.
		// Equal priorities are sorted by sequence number to provide FIFO
		// ordering.
		return left == right ? this.infoInDB.getId() - other.getDownloadId() : right.ordinal() - left.ordinal();
	}
}
