package com.simpledownloadmanager.sample;

import java.util.ArrayList;

import com.simpledownloadmanager.DownloadManager;
import com.simpledownloadmanager.DownloadRequest;
import com.simpledownloadmanager.DownloadRequestQueue;
import com.simpledownloadmanager.DownloadStatusListener;
import com.simpledownloadmanager.R;
import com.simpledownloadmanager.SimpleDownloadManager;
import com.simpledownloadmanager.db.DownloadInfo;
import com.simpledownloadmanager.db.DownloadInfoUnit;
import com.simpledownloadmanager.sample.AdapterDownloadItems.ItemTag;
import com.simpledownloadmanager.sample.AdapterDownloadItems.OnClickCallBack;
import com.simpledownloadmanager.util.FileUtil;
import com.simpledownloadmanager.view.DownRefreshUpMoreListView;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickCallBack, OnClickListener, DownloadStatusListener {

	private static final String[] STR_LINKS = {
			"http://img.yingyonghui.com/apk/16457/com.rovio.angrybirdsspace.ads.1332528395706.apk",
			"http://img.yingyonghui.com/apk/15951/com.galapagossoft.trialx2_winter.1328012793227.apk",
			"http://cdn1.down.apk.gfan.com/asdf/Pfiles/2012/3/26/181157_0502c0c3-f9d1-460b-ba1d-a3bad959b1fa.apk",
			"http://static.nduoa.com/apk/258/258681/com.gameloft.android.GAND.GloftAsp6.asphalt6.apk",
			"http://cdn1.down.apk.gfan.com/asdf/Pfiles/2011/12/5/100522_b73bb8d2-2c92-4399-89c7-07a9238392be.apk",
			"http://file.m.163.com/app/free/201106/16/com.gameloft.android.TBFV.GloftGTHP.ML.apk" };

	private DownRefreshUpMoreListView lv;
	private Button btnAdd;

	private AdapterDownloadItems adapter;
	private ArrayList<DownloadInfo> list;
	private SimpleDownloadManager manager;
	private DownloadInfoUnit unit;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		context = this;
		initView();
	}

	private void initView() {
		manager = new SimpleDownloadManager(DownloadRequestQueue.MAX_THREAD_POOL_SIZE);
		unit = new DownloadInfoUnit(context);

		lv = (DownRefreshUpMoreListView) findViewById(R.id.lv_list);
		list = new ArrayList<DownloadInfo>();
		adapter = new AdapterDownloadItems(list, this);
		lv.setAdapter(adapter);

		btnAdd = (Button) findViewById(R.id.btn_add);
		btnAdd.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		int count = STR_LINKS.length;
		Integer index = (Integer) v.getTag();
		if (null == index) {
			index = 0;
		}

		manager.add(new DownloadRequest(Uri.parse(STR_LINKS[index])).setDownloadListener(this));

		list.clear();
		list.addAll(unit.getDownloadInfos(0, DownRefreshUpMoreListView.MAX_ITEMS_PER_PAGE));
		adapter.notifyDataSetChanged();

		v.setTag((++index) % count);
	}

	@Override
	public void onClick(ClickType type, DownloadInfo info) {
		// TODO Auto-generated method stub
		switch (type) {
		case CONTROL:

			break;
		case CANCEL:
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		manager.release();
		super.onDestroy();
	}

	private DownloadInfo getInfoById(int id) {

		for (DownloadInfo info : list) {
			if (info.getId() == id) {
				return info;
			}
		}

		return null;
	}

	@Override
	public void onDownloadComplete(int id) {
		// TODO Auto-generated method stub
		DownloadInfo info = getInfoById(id);

		ProgressBar pbBar = (ProgressBar) adapter.getViewByTag(lv, ItemTag.PBPROGRESS, info.getUrl(), info.getId());
		TextView tvProgress = (TextView) adapter.getViewByTag(lv, ItemTag.TVPROGRESS, info.getUrl(), info.getId());

		if (null != pbBar && null != tvProgress) {
			pbBar.setVisibility(View.GONE);
			tvProgress.setText("下载完成");
		}
	}

	@Override
	public void onDownloadFailed(int id, int errorCode, String errorMessage) {
		// TODO Auto-generated method stub
		DownloadInfo info = getInfoById(id);

		ProgressBar pbBar = (ProgressBar) adapter.getViewByTag(lv, ItemTag.PBPROGRESS, info.getUrl(), info.getId());
		TextView tvProgress = (TextView) adapter.getViewByTag(lv, ItemTag.TVPROGRESS, info.getUrl(), info.getId());

		if (null != pbBar && null != tvProgress) {

			switch (errorCode) {
			case DownloadManager.ERROR_PAUSED_TO_EXIT:
				tvProgress.setText("暂停");
				break;
			case DownloadManager.ERROR_DOWNLOAD_CANCELLED:
				tvProgress.setText("任务被取消");
				break;
			default:
				tvProgress.setText(errorMessage);
				break;
			}
		}
	}

	@Override
	public synchronized void onProgress(int id, long totalBytes, long downloadedBytes, int progress, int speed) {
		// TODO Auto-generated method stub

		DownloadInfo info = getInfoById(id);

		// 下载大小/文件大小 百分比 下载速度
		String text = "id:%3s %4s/%4s		%4s		%6s/s";
		String show = String.format(text, id + "", FileUtil.showFileSize(downloadedBytes),
				FileUtil.showFileSize(totalBytes), progress + "%", FileUtil.showFileSize(speed));

		ProgressBar pbBar = (ProgressBar) adapter.getViewByTag(lv, ItemTag.PBPROGRESS, info.getUrl(), info.getId());
		TextView tvProgress = (TextView) adapter.getViewByTag(lv, ItemTag.TVPROGRESS, info.getUrl(), info.getId());

		if (null != pbBar && null != tvProgress) {
			pbBar.setProgress(progress);
			tvProgress.setText(show);
		}
	}
}
