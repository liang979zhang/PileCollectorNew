package com.jsti.pile.collector.db;

import java.io.File;

import com.jsti.pile.collector.common.CommonParams;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

/**
 * DB debug Context
 */
public class DatabaseDebugContext extends ContextWrapper {

	private final String outDir;

	public DatabaseDebugContext(Context base, String outDir) {
		super(base);
		this.outDir = outDir;
	}

	public static Context wrap(Context context, String outDir, boolean isDebug) {
		if (isDebug) {
			return new DatabaseDebugContext(context, outDir);
		} else {
			return context;
		}
	}

	@Override
	public File getDatabasePath(String name) {
		File file = new File(outDir + File.separatorChar + name);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		if (CommonParams.DEBUG) {
			Log.d("DBContext", "create Database file,name:" + name);
		}
		return file;
	}

	@Override
	public SQLiteDatabase openOrCreateDatabase(String name, int mode, CursorFactory factory) {
		return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), factory);
	}

	@Override
	public SQLiteDatabase openOrCreateDatabase(String name, int mode, CursorFactory factory,
			DatabaseErrorHandler errorHandler) {
		return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), factory);
	}

}