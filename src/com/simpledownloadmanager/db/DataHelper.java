package com.simpledownloadmanager.db;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * 使用ormlite操作数据库
 * 
 * @author liulei
 * 
 */
public class DataHelper extends OrmLiteSqliteOpenHelper {

	public static final String DATABASE_NAME = "SimpleDownloadManager.db";

	private static final int DATABASE_VERSON = 2;

	// 存储dao对象与model类名
	private Map<String, Dao> daos;

	public DataHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSON);
		daos = new HashMap<String, Dao>();
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			TableUtils.createTableIfNotExists(connectionSource, DownloadInfo.class);
		} catch (SQLException e) {
			Log.e(DataHelper.class.getName(), "fail create database", e);
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			TableUtils.dropTable(connectionSource, DownloadInfo.class, true);

			onCreate(db, connectionSource);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		super.close();
		daos.clear();
	}

	/**
	 * 根据model类获取dao
	 * 
	 * @author liulei
	 * @date 2015-4-3
	 * @param model
	 * @return
	 * @throws SQLException
	 *             Dao<T,Integer>
	 */
	@SuppressWarnings("unchecked")
	public <T> Dao<T, Integer> getMyDao(Class<T> model) throws SQLException {

		Dao dao = null;
		String className = model.getSimpleName();

		if (daos.containsKey(className)) {
			dao = daos.get(className);
		}
		if (dao == null) {
			dao = super.getDao(model);
			daos.put(className, dao);
		}
		return dao;

	}

}
