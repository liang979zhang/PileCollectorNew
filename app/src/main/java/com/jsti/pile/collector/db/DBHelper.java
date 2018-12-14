package com.jsti.pile.collector.db;

import com.jsti.pile.collector.common.CommonParams;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 数据库创建
 * 
 * @author shmwei
 *
 */
public class DBHelper extends SQLiteOpenHelper {
	/**
	 * 日志的打印的标签
	 */
	private static final String TAG = DBHelper.class.getSimpleName();
	/**
	 * 数据库名称
	 */
	public final static String DATABASE_NAME = "pile.db";

	/**
	 * 数据库版本号
	 */
	public final static int DATABASE_VERSION = 3;

	public static class TableRoadCollectTask {
		public static final String __table_name = "collect_task";
		//
		public static final String _roadId = "roadId";
		public static final String _roadName = "roadName";
		public static final String _roadCode = "roadCode";
		public static final String _directionName = "directionName";
		public static final String _direction = "direction";
		public static final String _startPile = "startPile";
		public static final String _lastFindPile = "lastFindPile";
		public static final String _lastPileLongitude = "lastPileLongitude";
		public static final String _lastPileLatitude = "lastPileLatitude";
		public static final String _lastScanLongitude = "lastScanLongitude";
		public static final String _lastScanLatitude = "lastScanLatitude";
		public static final String _startTime = "startTime";
		public static final String _finishTime = "finishTime";
		public static final String _creatorId = "creatorId";
		public static final String _creatorName = "creatorName";
		public static final String _isCollectPileASC = "isCollectPileASC";
		public static final String _collectStatus = "collectStatus";
		public static final String _isSubmitToServer = "isSubmitToServer";
		public static final String _gpsReferCount = "gpsReferCount";
		public static final String _exactPileCount = "exactPileCount";
		public static final String _filePath = "filePath";
		public static final String _arg0 = "arg0";
		public static final String _arg1 = "arg1";
		public static final String _arg2 = "arg2";

		public static final String CREATE_TABLE = "create table if not exists " + //
				__table_name + //
				"(" + //
				_roadId + " text," + //
				_roadName + " text," + //
				_roadCode + " text," + //
				_directionName + " text," + //
				_direction + " text," + //
				_startPile + " integer," + //
				_lastFindPile + " integer," + //
				_lastPileLongitude + " double," + //
				_lastPileLatitude + " double," + //
				_lastScanLongitude + " double," + //
				_lastScanLatitude + " double," + //
				_startTime + " long," + //
				_finishTime + " long," + //
				_creatorId + " text," + //
				_creatorName + " text," + //
				_isCollectPileASC + " integer," + //
				_collectStatus + " integer," + //
				_isSubmitToServer + " integer," + //
				_gpsReferCount + " integer," + //
				_exactPileCount + " integer," + //
				_filePath + " text," + //
				_arg0 + " text," + //
				_arg1 + " text," + //
				_arg2 + " text," + //
				" primary key(" + //
				_roadId + "," + //
				_startPile + "," + //
				_direction + "," + //
				_creatorId + //
				"))";
	}

	/**
	 * 构造器
	 * 
	 * @param context
	 *            上下文
	 */
	public DBHelper(Context context) {
		super(DatabaseDebugContext.wrap(context, CommonParams.PATH_DEBUG_DB, CommonParams.DEBUG_DB), DATABASE_NAME,
				null, DATABASE_VERSION);
		if (CommonParams.DEBUG) {
			Log.d(TAG, "create Database helper");
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTables(db);
	}

	private void createTables(SQLiteDatabase db) {
		db.beginTransaction();
		try {
			db.execSQL(TableRoadCollectTask.CREATE_TABLE);
			//
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		} finally {
			db.endTransaction();
		}
	}

	private void dropTables(SQLiteDatabase db) {
		db.beginTransaction();
		try {
			db.execSQL(String.format("drop table if exists %s;", TableRoadCollectTask.__table_name));
			//
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		} finally {
			db.endTransaction();
		}
	}

	/**
	 * 关闭当前正在使用的数据库
	 * 
	 * @param db
	 *            正处于打开状态的数据库
	 */
	public void close(SQLiteDatabase db) {
		try {
			if (db != null) {
				if (db.isOpen()) {
					db.close();
					db = null;
				}
			}
		} catch (Exception e) {
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, "start onUpgrade");
		dropTables(db);
		createTables(db);
		Log.d(TAG, "end onUpgrade");
	}

}
