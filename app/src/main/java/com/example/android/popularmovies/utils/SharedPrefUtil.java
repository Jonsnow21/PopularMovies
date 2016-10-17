package com.example.android.popularmovies.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class SharedPrefUtil {
    String preference_file;
    Activity activity;
    SharedPreferences sharedPreferences;

    public SharedPrefUtil(Activity activity) {
        this.activity = activity;
        this.sharedPreferences = activity.getSharedPreferences("APP_PREF", Context.MODE_PRIVATE);
    }

    /**
     * Helper method to retrieve a String value from {@link SharedPreferences}.
     *
     * @param key
     * @return The value from shared preferences, or null if the value could not be read.
     */
    public String getStringPreference(String key, String defaultValue) {
        String value = null;
        if (sharedPreferences != null) {
            value = sharedPreferences.getString(key, defaultValue);
        }
        return value;
    }

    /**
     * Helper method to write a String value to {@link SharedPreferences}.
     *
     * @param key
     * @param value
     * @return true if the new value was successfully written to persistent storage.
     */
    public boolean setStringPreference(String key, String value) {
        if (this.sharedPreferences != null && !TextUtils.isEmpty(key)) {
            SharedPreferences.Editor editor = this.sharedPreferences.edit();
            editor.putString(key, value);
            return editor.commit();
        }
        return false;
    }

    /**
     * Helper method to retrieve a Integer value from {@link SharedPreferences}.
     *
     * @param key
     * @return The value from shared preferences, or null if the value could not be read.
     */
    public Integer getIntegerPreference(String key, Integer defaultValue) {
        Integer value = null;
        if (sharedPreferences != null) {
            value = sharedPreferences.getInt(key, defaultValue);
        }
        return value;
    }

    /**
     * Helper method to write a Integer value to {@link SharedPreferences}.
     *
     * @param key
     * @param value
     * @return true if the new value was successfully written to persistent storage.
     */
    public boolean setIntegerPreference(String key, Integer value) {
        if (this.sharedPreferences != null && !TextUtils.isEmpty(key)) {
            SharedPreferences.Editor editor = this.sharedPreferences.edit();
            editor.putInt(key, value);
            return editor.commit();
        }
        return false;
    }

    public boolean setBooleanPreference(String key, boolean value) {
        if (this.sharedPreferences != null && !TextUtils.isEmpty(key)) {
            SharedPreferences.Editor editor = this.sharedPreferences.edit();
            editor.putBoolean(key, value);
            return editor.commit();
        }
        return false;
    }

    public boolean getBooleanPreference(String key, boolean defaultValue) {
        boolean value = false;
        if (sharedPreferences != null) {
            value = sharedPreferences.getBoolean(key, defaultValue);
        }
        return value;
    }

    /**
     * Helper method to remove a key from {@link SharedPreferences}.
     *
     * @param key
     * @return true if the new value was successfully written to persistent storage.
     */
    public boolean removePreference(String key) {
        if (this.sharedPreferences != null && !TextUtils.isEmpty(key)) {
            SharedPreferences.Editor editor = this.sharedPreferences.edit();
            editor.remove(key);
            return editor.commit();
        }
        return false;
    }

}