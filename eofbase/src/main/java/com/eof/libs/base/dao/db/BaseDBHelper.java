package com.eof.libs.base.dao.db;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.eof.libs.base.debug.Log;

/**
 * 数据库类
 */
public abstract class BaseDBHelper extends SQLiteOpenHelper implements IBaseDBHelperInterface{

	protected final static String TAG = "BaseDBHelper";
	private String DB_NAME;

	public BaseDBHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, null, version);
		
		assert name != null;
		DB_NAME = name;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
	 * .SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(TAG, DB_NAME, "onCreate");
		onCreateTable(db);
		onCreateTrigger(db);
	}

//	// 辅助执行sql语句，在重新版本安装旧版本然后再安装新版本，update执行会报错，用execSql封装sql的执行，防止报错的扩散
//	private void execSql(SQLiteDatabase db, String sql) {
//		try {
//			db.execSQL(sql);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * 创建表
	 * 
	 * @param db
	 */
	protected abstract void onCreateTable(SQLiteDatabase db);

	/**
	 * 创建触发器
	 * 
	 * @param db
	 */
	protected abstract void onCreateTrigger(SQLiteDatabase db);

	@Override
	public SafeCursor query(String queryStr) {
		Log.i(TAG, DB_NAME, "query|queryStr=", queryStr);
		SafeCursor safeCursor = null;
		try {
			Cursor cursor = this.getWritableDatabase().rawQuery(queryStr, null);
			if(null != cursor){
				safeCursor = new SafeCursor(cursor);
			}
		} catch (Throwable e) {
			Log.w(TAG, DB_NAME, "query error");
			Log.w(TAG, DB_NAME, e);
		}
		return safeCursor;
	}

	@Override
	public SafeCursor query(String table, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy) {
		Log.i(TAG, DB_NAME, "query|selection", selection,
				"table", table);
		SafeCursor safeCursor = null;
		try {
			Cursor cursor = this.getWritableDatabase().query(table, columns,
					selection, selectionArgs, groupBy, having, orderBy);
			if(null != cursor){
				safeCursor = new SafeCursor(cursor);
			}
		} catch (Throwable e) {
			Log.w(TAG, DB_NAME, "query: noneQuery error");
			Log.w(TAG, DB_NAME, e);
		}
		return safeCursor;

	}

	@Override
	public SafeCursor query(String table, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy, String limit) {
		Log.i(TAG, DB_NAME, "query|selection=", selection,
				"table", "limit", limit);
		SafeCursor safeCursor = null;
		try {
			Cursor cursor = this.getWritableDatabase().query(table, columns,
					selection, selectionArgs, groupBy, having, orderBy, limit);
			if(null != cursor){
				safeCursor = new SafeCursor(cursor);
			}
		} catch (Throwable e) {
			Log.w(TAG, DB_NAME, "query: noneQuery error");
			Log.w(TAG, DB_NAME, e);
		}
		return safeCursor;

	}

	@Override
	public boolean noneQuery(String queryStr) {
		Log.i(TAG, DB_NAME, "noneQuery|queryStr", queryStr);
		try {
			this.getWritableDatabase().execSQL(queryStr);
			return true;
		} catch (SQLException e) {
			Log.w(TAG, DB_NAME, "database: noneQuery error");
			Log.w(TAG, DB_NAME, e);
			return false;
		}
	}

	@Override
	public boolean execSQL(String queryStr) {
		Log.i(TAG, DB_NAME, "noneQuery|queryStr=", queryStr);
		try {
			this.getWritableDatabase().execSQL(queryStr);
			return true;
		} catch (SQLException e) {
			Log.w(TAG, DB_NAME, "database: execSQL error");
			Log.w(TAG, DB_NAME, e);
			return false;
		}
	}

	@Override
	public long insertData(String table, String nullColumnHack,
			ContentValues values) {
		Log.i(TAG, DB_NAME, "insertData|table=", table);
		long id = -1;
		try {
			id = this.getWritableDatabase().insert(table, nullColumnHack,
					values);
		} catch (Throwable e) {
			Log.w(TAG, DB_NAME, "database: insertData error");
			Log.w(TAG, DB_NAME, e);
		}
		return id;
	}

	@Override
	public boolean apptchInsertData(String table, String nullColumnHack,
			List<ContentValues> values) {
		Log.i(TAG, DB_NAME, "apptchInsertData|table=", table);
		boolean ret = false;
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.beginTransaction();
			int size = values.size();
			for (int i = 0; i < size; i++) {
				db.insert(table, nullColumnHack, values.get(i));
			}
			db.setTransactionSuccessful();
			ret = true;
		} catch (Throwable e) {
			Log.w(TAG, DB_NAME, "database: apptchInsertData error");
			Log.w(TAG, DB_NAME, e);
			ret = false;
		} finally {
			db.endTransaction();
		}
		return ret;
	}

	@Override
	public boolean transactionInsertData(String table, String nullColumnHack, String returnColumnName, List<ContentValues> values) {
		Log.i(TAG, DB_NAME, "transactionInsertData|table=", table);
		SQLiteDatabase db = this.getWritableDatabase();
		boolean transactionSuccess = true;
		long insertId = -1;
		try {
			db.beginTransaction();
			for (int i = 0; i < values.size(); i++) {
				insertId = db.insert(table, nullColumnHack, values.get(i));
				if (insertId == -1) {
					transactionSuccess = false;
					break;
				}else {
					values.get(i).put(returnColumnName, insertId);
				}
			}
			if (transactionSuccess)
				db.setTransactionSuccessful();
		} catch (Throwable e) {
			transactionSuccess = false;
			Log.w(TAG, DB_NAME, "database: transactionInsertData error");
			Log.w(TAG, DB_NAME, e);
		} finally {
			db.endTransaction();
		}
		
		return transactionSuccess;
	}
	
	
	@Override
	public boolean transactionUpdateData(String table, List<String> wheres, List<ContentValues> values){
		Log.i(TAG, DB_NAME, "transactionInsertData|table=", table);
		SQLiteDatabase db = this.getWritableDatabase();
		boolean transactionSuccess = true;
		long result = -1;
		try {
			db.beginTransaction();
			for (int i = 0; i < values.size(); i++) {
				result = db.update(table, values.get(i), wheres.get(i), null);
//				if (result ==0) {
//					transactionSuccess = false;
//					break;
//				}
			}
			if (transactionSuccess)
				db.setTransactionSuccessful();
		} catch (Throwable e) {
			transactionSuccess = false;
			Log.w(TAG, DB_NAME, "database: transactionInsertData error");
			Log.w(TAG, DB_NAME, e);
		} finally {
			db.endTransaction();
		}
		
		return transactionSuccess;
	}
	
	
	@Override
	public int deleteData(String table, String whereClause, String[] whereArgs) {
		Log.i(TAG, DB_NAME, "deleteData|table=", table);
		int result = 0;
		try {
			result = this.getWritableDatabase().delete(table, whereClause,
					whereArgs);
		} catch (Throwable e) {
			Log.w(TAG, DB_NAME, "database: deleteData error");
			Log.w(TAG, DB_NAME, e);
		}
		return result;
	}

	@Override
	public int deleteData(SQLiteDatabase db, String table, String whereClause, String[] whereArgs) {
		Log.i(TAG, DB_NAME, "deleteData|table=", table);
		int result = 0;
		try {
			result = db.delete(table, whereClause, whereArgs);
		} catch (Throwable e) {
			Log.w(TAG, DB_NAME, "database: deleteData error");
			Log.w(TAG, DB_NAME, e);
		}
		return result;
	}

	@Override
	public int updateData(String table, ContentValues values,
			String whereClause, String[] whereArgs) {
		Log.i(TAG, DB_NAME, "updateData|table=", table);
		int result = 0;
		try {
			result = this.getWritableDatabase().update(table, values,
					whereClause, whereArgs);
		} catch (Throwable e) {
			Log.w(Log.TAG, "database: updateData error");
			Log.w(TAG, DB_NAME, e);
		}
		return result;
	}

	@Override
	public long replaceData(String table, String nullColumnHack,
			ContentValues values) {
		Log.i(TAG, DB_NAME, "replaceData|table=", table);
		long result = 0;
		try {
			result = this.getWritableDatabase().replace(table, nullColumnHack, values);
		} catch (Throwable e) {
			Log.w(Log.TAG, "database: replaceData error");
			Log.w(TAG, DB_NAME, e);
		}
		return result;
	}

	@Override
	public synchronized void close() {
		Log.i(TAG, DB_NAME, "close");
		try {
			super.close();
		} catch (Throwable e) {
			Log.w(TAG, DB_NAME, "close error");
			Log.w(TAG, DB_NAME, e);
		}
	}

	@Override
	protected void finalize() throws Throwable {
		Log.i(TAG, DB_NAME, "finalize");
		super.finalize();
		Log.d(Log.TAG, DB_NAME, "DBHelper::finalize()");
		try {
			super.close();
		} catch (Throwable e) {
			Log.w(TAG, DB_NAME, "close error");
			Log.w(TAG, DB_NAME, e);
		}
	}

	
	
}
