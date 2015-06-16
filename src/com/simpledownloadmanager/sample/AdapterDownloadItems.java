package com.simpledownloadmanager.sample;

import java.util.ArrayList;

import com.simpledownloadmanager.DownloadManager;
import com.simpledownloadmanager.DownloadManagerApplication;
import com.simpledownloadmanager.R;
import com.simpledownloadmanager.db.DownloadInfo;
import com.simpledownloadmanager.sample.AdapterDownloadItems.OnClickCallBack.ClickType;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AdapterDownloadItems extends BaseAdapter {

	public class ViewHolder {
		TextView url = null;
		ProgressBar pbProgress = null;
		TextView tvProgress = null;
		Button btnControl = null;
		Button btnCancel = null;
	}

	public enum ItemTag {

		PBPROGRESS("progress"), TVPROGRESS("tvprogress"), BTNCONTROL("btncontrol");

		String tag;

		ItemTag(String tag) {
			this.tag = tag;
		}

		public String getTag() {
			return tag;
		}
	}

	public interface OnClickCallBack {

		enum ClickType {
			CONTROL, CANCEL
		}

		void onClick(ClickType type, DownloadInfo info);
	}

	ArrayList<DownloadInfo> list;
	OnClickCallBack callBack;

	public AdapterDownloadItems(ArrayList<DownloadInfo> list, OnClickCallBack callBack) {
		this.list = list;
		this.callBack = callBack;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return null == list ? 0 : list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position - 1);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		ViewHolder holder = null;
		if (null == convertView) {
			convertView = LayoutInflater.from(DownloadManagerApplication.getInstance()).inflate(R.layout.list_item,
					null);
			holder = new ViewHolder();
			holder.url = (TextView) convertView.findViewById(R.id.tv_url);
			holder.pbProgress = (ProgressBar) convertView.findViewById(R.id.pb_progress);
			holder.tvProgress = (TextView) convertView.findViewById(R.id.tv_progress);
			holder.btnCancel = (Button) convertView.findViewById(R.id.btn_cancel);
			holder.btnControl = (Button) convertView.findViewById(R.id.btn_control);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final DownloadInfo info = list.get(position);
		String url = info.getUrl();
		holder.url.setText(url);

		holder.pbProgress.setTag(ItemTag.PBPROGRESS.getTag() + info.getId() + url);
		holder.tvProgress.setTag(ItemTag.TVPROGRESS.getTag() + info.getId() + url);
		holder.btnControl.setTag(ItemTag.BTNCONTROL.getTag() + info.getId() + url);

		holder.btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				callBack.onClick(ClickType.CANCEL, info);
			}
		});

		holder.btnControl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				callBack.onClick(ClickType.CONTROL, info);
			}
		});

		switch (info.getState()) {

		case DownloadManager.STATUS_FAILED:
		case DownloadManager.STATUS_NOT_FOUND:
			holder.pbProgress.setVisibility(View.GONE);
			holder.tvProgress.setText("下载失败 " + info.getErrorMsg());
			break;

		case DownloadManager.STATUS_SUCCESSFUL:
			holder.pbProgress.setVisibility(View.GONE);
			holder.tvProgress.setText("下载完成");
			break;

		case DownloadManager.STATUS_PAUSED:
			holder.pbProgress.setVisibility(View.GONE);
			holder.tvProgress.setText("暂停");
			break;

		case DownloadManager.STATUS_RETRYING:
			holder.pbProgress.setVisibility(View.GONE);
			holder.tvProgress.setText("重试中");
			break;
		case DownloadManager.STATUS_CONNECTING:
		case DownloadManager.STATUS_PENDING:
		case DownloadManager.STATUS_RUNNING:
		case DownloadManager.STATUS_STARTED:
		default:
			holder.pbProgress.setVisibility(View.VISIBLE);
			break;
		}

		return convertView;
	}

	public View getViewByTag(ListView lv, ItemTag tag, String url, int id) {

		return lv.findViewWithTag(tag.getTag() + id + url);
	}
}
