package com.eof.libs.base.dao.sp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.eof.libs.base.GlobalData;

import java.util.HashMap;


/**
 * sp存储管理器
 * 
 * @author danyangguo
 *
 */
public class SpManager {

	private HashMap<String, ISPer> mSpMap = new HashMap<String, ISPer>();
	
	public interface ISPer {
		String getString(String key);
		String getString(String key, String defaultValue);
		int getInt(String key);
		int getInt(String key, int defaultValue);
		float getFloat(String key);
		float getFloat(String key, float defaultValue);
		long getLong(String key);
		long getLong(String key, long defaultValue);
		boolean getBoolean(String key);
		boolean getBoolean(String key, boolean defalut);
		
		boolean putString(String key, String value, boolean commit);
		boolean putInt(String key, int value, boolean commit);
		boolean putFloat(String key, float value, boolean commit);
		boolean putLong(String key, long value, boolean commit);
		boolean putBoolean(String key, boolean value, boolean commit);
		
		boolean commit();
	}
	
	private static class SingletonContainer {
		public static SpManager mSingleInstance = new SpManager();
	}
	
	public static SpManager getInstance() {
		return SingletonContainer.mSingleInstance;
	}
	
	private SpManager() {
		
	}
	
	public ISPer getSPer(String name) {
		ISPer per = null;
		synchronized (mSpMap) {
			per = mSpMap.get(name);
			if (null == per) {
				per = new SPer(name);
			}
			mSpMap.put(name, per);
		}
		return per;
	}
	
	private class SPer implements ISPer {
		private String mName;
		private SharedPreferences mPreferences;
		private Editor mEditor;
		
		public SPer(String name) {
			mName = name;
			mPreferences = GlobalData.mApplicationContext.getSharedPreferences(name, Context.MODE_PRIVATE);
		}

		private Editor getEditor() {
			if (null == mEditor) {
				mEditor = mPreferences.edit();
			}
			return mEditor;
		}

		@Override
		public String getString(String key) {
			return mPreferences.getString(key, null);
		}

		@Override
		public String getString(String key, String defaultValue) {
			return mPreferences.getString(key, defaultValue);
		}

		@Override
		public int getInt(String key) {
			return mPreferences.getInt(key, 0);
		}

		@Override
		public int getInt(String key, int defaultValue) {
			return mPreferences.getInt(key, defaultValue);
		}

		@Override
		public float getFloat(String key) {
			return mPreferences.getFloat(key, 0f);
		}

		@Override
		public float getFloat(String key, float defaultValue) {
			return mPreferences.getFloat(key, defaultValue);
		}

		@Override
		public long getLong(String key) {
			return mPreferences.getLong(key, 0l);
		}

		@Override
		public long getLong(String key, long defaultValue) {
			return mPreferences.getLong(key, defaultValue);
		}

		@Override
		public boolean getBoolean(String key) {
			return mPreferences.getBoolean(key, false);
		}

		@Override
		public boolean getBoolean(String key, boolean defalut) {
			return mPreferences.getBoolean(key, defalut);
		}

		@Override
		public boolean putString(String key, String value, boolean commit) {
			Editor editor = getEditor();
			editor.putString(key, value);
			if (commit) {
				return editor.commit();
			}
			return true;
		}

		@Override
		public boolean putInt(String key, int value, boolean commit) {
			Editor editor = getEditor();
			editor.putInt(key, value);
			if (commit) {
				return editor.commit();
			}
			return true;
		}

		@Override
		public boolean putFloat(String key, float value, boolean commit) {
			Editor editor = getEditor();
			editor.putFloat(key, value);
			if (commit) {
				return editor.commit();
			}
			return true;
		}

		@Override
		public boolean putLong(String key, long value, boolean commit) {
			Editor editor = getEditor();
			editor.putLong(key, value);
			if (commit) {
				return editor.commit();
			}
			return true;
		}

		@Override
		public boolean putBoolean(String key, boolean value, boolean commit) {
			Editor editor = getEditor();
			editor.putBoolean(key, value);
			if (commit) {
				return editor.commit();
			}
			return true;
		}

		@Override
		public boolean commit() {
			Editor editor = getEditor();
			return editor.commit();
		}
		
	}
	
}
