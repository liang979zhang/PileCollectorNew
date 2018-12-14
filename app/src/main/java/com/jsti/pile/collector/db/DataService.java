package com.jsti.pile.collector.db;

import java.util.List;

import com.jsti.pile.collector.db.DBHelper.TableRoadCollectTask;
import com.jsti.pile.collector.model.RoadCollectTask;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * 数据库操作封装
 *
 */
public class DataService {

	private static final String TAG = "DataService";
	private Context application;

	private DBHelper dbHelper;
	private static DataService instance;

	private DataService(Context context) {
		application = context.getApplicationContext();
		dbHelper = new DBHelper(application);
	}

	public static DataService getInstance(Context context) {
		synchronized (DataService.class) {
			if (instance == null) {
				instance = new DataService(context);
			}
		}
		return instance;
	}

	public void addCollectTask(RoadCollectTask task) {
		SQLiteDatabase db = getDatabase();
		ContentValues cv = new ContentValues();
		toContentValues(task, cv);
		db.insertWithOnConflict(TableRoadCollectTask.__table_name, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
		onDatabaseOperatFinish(db);
	}

	public RoadCollectTask getRoadTask(String creatorId, String roadId, int startPile, String direaction) {
		SQLiteDatabase db = getDatabase();
		StringBuilder sb = new StringBuilder();
		sb.append("select * from ");
		sb.append(TableRoadCollectTask.__table_name);
		sb.append(" where ");
		sb.append(TableRoadCollectTask._creatorId);
		sb.append("='");
		sb.append(creatorId);
		sb.append("'");
		sb.append(" and ");
		//
		sb.append(TableRoadCollectTask._roadId);
		sb.append("='");
		sb.append(roadId);
		sb.append("'");
		sb.append(" and ");
		//
		sb.append(TableRoadCollectTask._startPile);
		sb.append("=");
		sb.append(startPile);
		sb.append(" and ");
		//
		sb.append(TableRoadCollectTask._direction);
		sb.append("='");
		sb.append(direaction);
		sb.append("'");
		sb.append(" limit 1");
		String sql = sb.toString();
		Log.d(TAG, "get road task:" + sql);
		Cursor c = db.rawQuery(sb.toString(), null);
		RoadCollectTask task = null;
		if (c != null) {
			if (c.moveToFirst()) {
				task = toRoadTask(c);
			}
			c.close();
		}
		onDatabaseOperatFinish(db);
		return task;
	}

	public void deleteCollectTask(String creatorId, String roadId, int startPile, String direaction) {
		SQLiteDatabase db = getDatabase();
		StringBuilder sb = new StringBuilder();
		sb.append("delete from ");
		sb.append(TableRoadCollectTask.__table_name);
		sb.append(" where ");
		sb.append(TableRoadCollectTask._creatorId);
		sb.append("='");
		sb.append(creatorId);
		sb.append("'");
		sb.append(" and ");
		//
		sb.append(TableRoadCollectTask._roadId);
		sb.append("='");
		sb.append(roadId);
		sb.append("'");
		sb.append(" and ");
		//
		sb.append(TableRoadCollectTask._startPile);
		sb.append("=");
		sb.append(startPile);
		sb.append(" and ");
		//
		sb.append(TableRoadCollectTask._direction);
		sb.append("='");
		sb.append(direaction);
		sb.append("'");
		String sql = sb.toString();
		Log.d(TAG, "delete road task:" + sql);
		db.execSQL(sql);
		onDatabaseOperatFinish(db);
	}

	public void getAllTask(String creatorId, List<RoadCollectTask> out) {
		SQLiteDatabase db = getDatabase();
		StringBuilder sb = new StringBuilder();
		sb.append("select * from ");
		sb.append(TableRoadCollectTask.__table_name);
		sb.append(" where ");
		sb.append(TableRoadCollectTask._creatorId);
		sb.append("='");
		sb.append(creatorId);
		sb.append("' order by ");
		sb.append(TableRoadCollectTask._startTime);
		sb.append(" DESC");
		String sql = sb.toString();
		Log.d(TAG, "getAllTask:" + sql);
		Cursor c = db.rawQuery(sb.toString(), null);
		RoadCollectTask task = null;
		if (c != null) {
			if (c.moveToFirst()) {
				do {
					task = toRoadTask(c);
					out.add(task);
				} while (c.moveToNext());
			}
			c.close();
		}
		onDatabaseOperatFinish(db);
	}

	private RoadCollectTask toRoadTask(Cursor c) {
		RoadCollectTask task = new RoadCollectTask();
		task.setRoadId(c.getString(c.getColumnIndex(TableRoadCollectTask._roadId)));
		task.setRoadName(c.getString(c.getColumnIndex(TableRoadCollectTask._roadName)));
		task.setRoadCode(c.getString(c.getColumnIndex(TableRoadCollectTask._roadCode)));
		task.setDirectionName(c.getString(c.getColumnIndex(TableRoadCollectTask._directionName)));
		task.setDirection(c.getString(c.getColumnIndex(TableRoadCollectTask._direction)));
		task.setStartPile(c.getInt(c.getColumnIndex(TableRoadCollectTask._startPile)));
		task.setLastFindPile(c.getInt(c.getColumnIndex(TableRoadCollectTask._lastFindPile)));
		task.setLastPileLatitude(c.getDouble(c.getColumnIndex(TableRoadCollectTask._lastPileLongitude)));
		task.setLastPileLongitude(c.getDouble(c.getColumnIndex(TableRoadCollectTask._lastPileLatitude)));
		task.setLastScanLongitude(c.getDouble(c.getColumnIndex(TableRoadCollectTask._lastScanLongitude)));
		task.setLastScanLatitude(c.getDouble(c.getColumnIndex(TableRoadCollectTask._lastScanLatitude)));
		task.setStartTime(c.getLong(c.getColumnIndex(TableRoadCollectTask._startTime)));
		task.setFinishTime(c.getLong(c.getColumnIndex(TableRoadCollectTask._finishTime)));
		task.setCreatorId(c.getString(c.getColumnIndex(TableRoadCollectTask._creatorId)));
		task.setCreatorName(c.getString(c.getColumnIndex(TableRoadCollectTask._creatorName)));
		task.setCollectPileASC(intToBoolean(c.getInt(c.getColumnIndex(TableRoadCollectTask._isCollectPileASC))));
		task.setCollectStatus(c.getInt(c.getColumnIndex(TableRoadCollectTask._collectStatus)));
		task.setSubmitToServer(intToBoolean(c.getInt(c.getColumnIndex(TableRoadCollectTask._isSubmitToServer))));
		task.setGpsReferCount(c.getInt(c.getColumnIndex(TableRoadCollectTask._gpsReferCount)));
		task.setExactPileCount(c.getInt(c.getColumnIndex(TableRoadCollectTask._exactPileCount)));
		task.setFilePath(c.getString(c.getColumnIndex(TableRoadCollectTask._filePath)));
		return task;
	}

	private void toContentValues(RoadCollectTask task, ContentValues out) {
		out.put(TableRoadCollectTask._roadId, task.getRoadId());
		out.put(TableRoadCollectTask._roadName, task.getRoadName());
		out.put(TableRoadCollectTask._roadCode, task.getRoadCode());
		out.put(TableRoadCollectTask._directionName, task.getDirectionName());
		out.put(TableRoadCollectTask._direction, task.getDirection());
		out.put(TableRoadCollectTask._startPile, task.getStartPile());
		out.put(TableRoadCollectTask._lastFindPile, task.getLastFindPile());
		out.put(TableRoadCollectTask._lastPileLongitude, task.getLastPileLongitude());
		out.put(TableRoadCollectTask._lastPileLatitude, task.getLastPileLatitude());
		out.put(TableRoadCollectTask._lastScanLongitude, task.getLastScanLongitude());
		out.put(TableRoadCollectTask._lastScanLatitude, task.getLastScanLatitude());
		out.put(TableRoadCollectTask._startTime, task.getStartTime());
		out.put(TableRoadCollectTask._finishTime, task.getFinishTime());
		out.put(TableRoadCollectTask._creatorId, task.getCreatorId());
		out.put(TableRoadCollectTask._creatorName, task.getCreatorName());
		out.put(TableRoadCollectTask._isCollectPileASC, booleanToInt(task.isCollectPileASC()));
		out.put(TableRoadCollectTask._collectStatus, task.getCollectStatus());
		out.put(TableRoadCollectTask._isSubmitToServer, booleanToInt(task.isSubmitToServer()));
		out.put(TableRoadCollectTask._gpsReferCount, task.getGpsReferCount());
		out.put(TableRoadCollectTask._exactPileCount, task.getExactPileCount());
		out.put(TableRoadCollectTask._filePath, task.getFilePath());
		out.put(TableRoadCollectTask._arg0, "");
		out.put(TableRoadCollectTask._arg1, "");
		out.put(TableRoadCollectTask._arg2, "");
	}

	private int booleanToInt(boolean b) {
		return b ? 1 : 0;
	}

	private boolean intToBoolean(int i) {
		return i != 0;
	}

	// --------------------------------------------------
	private SQLiteDatabase db;

	private SQLiteDatabase getDatabase() {
		synchronized (dbHelper) {
			if (db == null || !db.isOpen()) {
				db = dbHelper.getWritableDatabase();
			}
			return db;
		}
	}

	private void onDatabaseOperatFinish(SQLiteDatabase db) {
		// do nothing
	}
}
