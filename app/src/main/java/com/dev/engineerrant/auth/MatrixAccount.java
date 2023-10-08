package com.dev.engineerrant.auth;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MatrixAccount {
    public static String accessToken() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getString("matrix_access_token", null);
    }

    public static void setAccessToken(String matrix_access_token) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putString("matrix_access_token",matrix_access_token);
        editor.apply();
    }

    public static String username() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getString("matrix_username", null);
    }

    public static void setUsername(String username) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putString("matrix_username",username);
        editor.apply();
    }


    public static String user_id() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getString("matrix_user_id", null);
    }

    public static void setUser_id(String user_id) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putString("matrix_user_id",user_id);
        editor.apply();
    }

    public static int expire_time() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getInt("matrix_expire_time", 0);
    }

    public static void setExpire_time(int expire_time) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putInt("matrix_expire_time",expire_time);
        editor.apply();
    }

    public static Boolean isLoggedIn() {
        return MatrixAccount.user_id() != null;
    }
}
