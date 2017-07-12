package com.eof.libs.base.dao.db;

import android.database.Cursor;
import android.database.CursorWrapper;

/**
 *  捕获掉cursor中getCount触发native代码执行填充数据操作，导致的数据库查询异常
 */
public class SafeCursor extends CursorWrapper {

	public SafeCursor(Cursor cursor) {
		super(cursor);
	}

	@Override
	public int getCount() {
		int count = 0;
		try {
			count = super.getCount();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return count;
	}
	
	@Override
	public void close() {
		try {
			super.close();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
