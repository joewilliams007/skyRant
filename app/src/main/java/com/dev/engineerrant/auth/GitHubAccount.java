package com.dev.engineerrant.auth;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class GitHubAccount {
    public static String githubKey() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getString("githubKey", null);
    }

    public static void setGithubKey(String githubKey) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putString("githubKey",githubKey);
        editor.apply();
    }
}
