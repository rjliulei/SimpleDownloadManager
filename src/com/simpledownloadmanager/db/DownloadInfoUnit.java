package com.simpledownloadmanager.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.Context;

/**
 * 实体类的操作单元
 * 
 * @author liulei
 * 
 */
public class DownloadInfoUnit extends BeanUnit<DownloadInfo> {

	public DownloadInfoUnit(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	// 根据url获取下载id
	public DownloadInfo getInfoByURL(String url) {

		DownloadInfo info = null;

		try {
			List<DownloadInfo> list = dao.queryBuilder().limit(1l).where().eq("url", url).query();
			info = list.size() > 0 ? list.get(0) : null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return info;
	}

	/**
	 * 根据时间获取分页数据
	 * 
	 * @author liulei
	 * @date 2015-6-15
	 * @param page
	 * @param rows
	 * @return ArrayList<DownloadInfo>
	 */
	public ArrayList<DownloadInfo> getDownloadInfos(long page, long rows) {

		ArrayList<DownloadInfo> list = null;
		try {
			list = (ArrayList<DownloadInfo>) dao.queryBuilder().offset(page * rows).limit(rows).orderBy("date", false)
					.query();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}
}
