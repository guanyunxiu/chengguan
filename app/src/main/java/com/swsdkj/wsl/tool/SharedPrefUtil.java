package com.swsdkj.wsl.tool;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 保存SharedPreferences工具类
 *
 * @author fsh
 * @version V1.0
 * @date 2015-4-11 上午9:03:08
 */
public class SharedPrefUtil {
  private Editor mEditor;
  private SharedPreferences mSharedPreferences;

  public SharedPrefUtil(Context context, String name) {
         mSharedPreferences = context.getApplicationContext().getSharedPreferences(name, Context.MODE_PRIVATE);
         mEditor = mSharedPreferences.edit();
  }

  public Editor putInt(String key, int value) {
    return mEditor.putInt(key, value);
  }

  public Editor putFloat(String key, float value) {
    return mEditor.putFloat(key, value);
  }

  public Editor putLong(String key, long value) {
    return mEditor.putLong(key, value);
  }

  public Editor putBoolean(String key, boolean value) {
    return mEditor.putBoolean(key, value);
  }

  public Editor putString(String key, String value) {
    return mEditor.putString(key, value);
  }

  public int getInt(String key, int defaultValue) {
    return mSharedPreferences.getInt(key, defaultValue);
  }

  public float getFloat(String key, float defaultValue) {
    return mSharedPreferences.getFloat(key, defaultValue);
  }

  public long getLong(String key, long defaultValue) {
    return mSharedPreferences.getLong(key, defaultValue);
  }

  public boolean getBoolean(String key, boolean defaultValue) {
    return mSharedPreferences.getBoolean(key, defaultValue);
  }

  public String getString(String key, String defaultValue) {
    return mSharedPreferences.getString(key, defaultValue);
  }

  public void commit() {
    mEditor.commit();
  }

  public void clear() {
    mEditor.clear().commit();
  }
}
