package com.eof.libs.base.dao.db;

import java.util.List;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.eof.libs.base.dao.db.SafeCursor;

/**
 * 基础数据库操作接口
 */
public interface IBaseDBHelperInterface{
	/**
	 * 有返回结果查询
	 * 
	 * @param queryStr
	 *            SQL语句
	 * @return 当前游标
	 */
	public SafeCursor query(String queryStr);

	/**
	 * 有返回结果查询
	 * 
	 * @param table
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @return
	 */
	public SafeCursor query(String table, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy);

	/**
	 * @param table
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @param limit
	 * @return
	 */
	public SafeCursor query(String table, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy, String limit);

	/**
	 * 无返回结果查询
	 * 
	 * @param queryStr
	 *            SQL语句
	 * @return
	 */
	public boolean noneQuery(String queryStr);

	/**
	 * 无返回结果查询
	 * 
	 * @param queryStr
	 *            SQL语句
	 * @return
	 */
	public boolean execSQL(String queryStr);

	/**
	 * 插入数据
	 * 
	 * @param table
	 *            表名
	 * @param nullColumnHack
	 *            空列
	 * @param values
	 *            值
	 * @return 插入成功返回ID值，失败返回-1
	 */
	public long insertData(String table, String nullColumnHack,
			ContentValues values);

	public boolean apptchInsertData(String table, String nullColumnHack,
			List<ContentValues> values);
	
	/**
	 * 事务性批量插入，失败会回滚。成功会将插入的id置于参数{@code values}中。
	 * @param table 表名
	 * @param nullColumnHack SQL doesn't allow inserting a completely empty row, so if initialValues is empty this column will explicitly be assigned a NULL value
	 * @param returnColumnName 插入成功的时候，每条记录返回的id将放置在{@code values}中的，key为此参数值。
	 * @param values 要插入的记录值
	 * @return 成功与否
	 */
	public boolean transactionInsertData(String table, String nullColumnHack, String returnColumnName, List<ContentValues> values);

	
	

	public boolean transactionUpdateData(String table, List<String> wheres, List<ContentValues> values);
	/**
	 * 删除数据
	 * 
	 * @param table
	 *            表名
	 * @param whereClause
	 * @param whereArgs
	 * @return 删除条数
	 */
	public int deleteData(String table, String whereClause, String[] whereArgs);

	/**
	 * 
	 * @param db
	 * @param table
	 * @param whereClause
	 * @param whereArgs
	 * @return
	 * 
	 * @author danyangguo
	 */
	public int deleteData(SQLiteDatabase db, String table, String whereClause,
			String[] whereArgs);

	/**
	 * 更新数据
	 * 
	 * @param table
	 *            表名
	 * @param values
	 * @param whereClause
	 * @param whereArgs
	 * @return
	 */
	public int updateData(String table, ContentValues values,
			String whereClause, String[] whereArgs);
	
	/**
	 * 替换数据
	 * @param table
	 * @param nullColumnHack
	 * @param values
	 * @return
	 */
	public long replaceData(String table, String nullColumnHack,
			ContentValues values);
}
