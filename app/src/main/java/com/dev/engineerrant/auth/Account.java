package com.dev.engineerrant.auth;

import static com.dev.engineerrant.app.toast;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Account {
    public static String key() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getString("key", null);
    }

    public static void setKey(String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putString("key",key);
        editor.apply();
    }

    public static String username() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getString("username", "PROFILE");
    }

    public static void setUsername(String username) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putString("username",username);
        editor.apply();
    }

    public static int id() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getInt("id", 0);
    }

    public static void setId(int id) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putInt("id",id);
        editor.apply();
    }

    public static int user_id() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getInt("user_id", 0);
    }

    public static void setUser_id(int user_id) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putInt("user_id",user_id);
        editor.apply();
    }

    public static int expire_time() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getInt("expire_time", 0);
    }

    public static void setExpire_time(int expire_time) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putInt("expire_time",expire_time);
        editor.apply();
    }

    public static Boolean isLoggedIn() {
        return Account.user_id() != 0;
    }

    public static String theme() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getString("theme", "dark");
    }

    public static void setTheme(String theme) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putString("theme",theme);
        editor.apply();
    }

    public static String search() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getString("search", "print");
    }

    public static void setSearch(String search) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putString("search",search);
        editor.apply();
    }

    public static String language() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getString("language", null);
    }

    public static void setLanguage(String language) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putString("language",language);
        editor.apply();
    }

    public static String following() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getString("following", "");
    }

    public static void setFollowing(String following) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putString("following",following);
        editor.apply();
    }

    public static Boolean isFollow (String user_id) {
        String[] following = Account.following().split("\n");
        Boolean isFollowing = false;
        for (String user : following) {
            if (user_id.equals(user.split(" ")[0])) {
                isFollowing = true;
                break;
            }
        }
        return isFollowing;
    }

    public static void follow (String user_id, String username, String color, String image_url) {
        if (Account.following()==null || Account.following().equals("")) {
            Account.setFollowing(user_id+" "+username+" "+color+" "+image_url);
        } else {
            Account.setFollowing(Account.following()+"\n"+user_id+" "+username+" "+color+" "+image_url);
        }
    }

    public static void unfollow (String user_id) {
        String[] following = Account.following().split("\n");
        String new_following = null;
        for (String user : following) {
            if (!user_id.equals(user.split(" ")[0])) {
                new_following += user.toLowerCase()+"\n";
            }
        }
        if (new_following!=null) {
            Account.setFollowing(new_following.substring(0, new_following.length() - 2).replaceFirst("null",""));
        } else {
            Account.setFollowing(null);
        }
    }

    public static String blockedWords() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getString("blockedWords", "");
    }

    public static void setBlockedWords(String blockedWords) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putString("blockedWords",blockedWords);
        editor.apply();
    }
    public static String blockedUsers() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getString("blockedUsers", "");
    }

    public static void setBlockedUsers(String blockedUsers) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putString("blockedUsers",blockedUsers);
        editor.apply();
    }
    public static Boolean blockWordsComments() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getBoolean("blockWordsComments", false);
    }

    public static void setBlockWordsComments(Boolean blockWordsComments) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putBoolean("blockWordsComments",blockWordsComments);
        editor.apply();
    }

    public static Boolean blockUsersComments() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getBoolean("blockUsersComments", false);
    }

    public static void setBlockUsersComments(Boolean blockUsersComments) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putBoolean("blockUsersComments",blockUsersComments);
        editor.apply();
    }

    public static Boolean blockGreenDot() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getBoolean("blockGreenDot", false);
    }

    public static void setBlockGreenDot(Boolean blockGreenDot) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putBoolean("blockGreenDot",blockGreenDot);
        editor.apply();
    }

    public static Boolean animate() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getBoolean("animate", true);
    }

    public static void setAnimate(Boolean animate) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putBoolean("animate",animate);
        editor.apply();
    }

    public static Boolean surprise() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getBoolean("surprise", true);
    }

    public static void setSurprise(Boolean surprise) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putBoolean("surprise",surprise);
        editor.apply();
    }

    public static Boolean binary() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getBoolean("binary", false);
    }

    public static void setBinary(Boolean binary) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putBoolean("binary",binary);
        editor.apply();
    }

    public static Boolean followBtn() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getBoolean("followBtn", true);
    }

    public static void setFollowBtn(Boolean followBtn) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putBoolean("followBtn",followBtn);
        editor.apply();
    }

    public static Boolean highlighter() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getBoolean("highlighter", false);
    }

    public static void setHighlighter(Boolean highlighter) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putBoolean("highlighter",highlighter);
        editor.apply();
    }

    public static Boolean autoLoad() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getBoolean("autoLoad", true);
    }

    public static void setAutoLoad(Boolean autoLoad) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putBoolean("autoLoad",autoLoad);
        editor.apply();
    }

    public static Boolean userInfo() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getBoolean("userInfo", true);
    }

    public static void setUserInfo(Boolean userInfo) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putBoolean("userInfo",userInfo);
        editor.apply();
    }

    public static Boolean vibrate() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getBoolean("vibrate", true);
    }

    public static void setVibrate(Boolean vibrate) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putBoolean("vibrate",vibrate);
        editor.apply();
    }

    public static int limit() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getInt("limit", 50);
    }

    public static void setLimit(int limit) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putInt("limit",limit);
        editor.apply();
    }

    // FOR NOTIFICATIONS - PLEASE DON'T JUDGE HOW NOTIFICATIONS WORK, because AT LEAST THEY WORK!

    public static boolean isPushNotif() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getBoolean("push_notif", true);
    }

    public static void setPushNotif(boolean b) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("push_notif",b);
        editor.apply();
    }

    public static boolean isFeedUsername() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getBoolean("feed_username", false);
    }



    public static void setFeedUsername(boolean b) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("feed_username",b);
        editor.apply();
    }

    public static long pushNotifTime() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getLong("push_notif_time", 30L);
    }

    public static void setPushNotifTime(long t) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("last_push_notif_time",t);
        editor.apply();
    }

    public static long lastPushNotifTime() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getLong("last_push_notif_time", 0);
    }

    public static void setLastPushNotifTime(long t) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("last_push_notif_time",t);
        editor.apply();
    }

    public static boolean isPushNotifCommentVote() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getBoolean("comment_vote", true);
    }

    public static void setPushNotifCommentVote(boolean b) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("comment_vote",b);
        editor.apply();
    }

    public static boolean isPushNotifRantVote() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getBoolean("content_vote", true);
    }

    public static void setPushNotifRantVote(boolean b) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("content_vote",b);
        editor.apply();
    }

    public static boolean isPushNotifMention() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getBoolean("comment_mention", true);
    }

    public static void setPushNotifMention(boolean b) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("comment_mention",b);
        editor.apply();
    }

    public static boolean isPushNotifComment() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getBoolean("comment_content", true);
    }

    public static void setPushNotifComment(boolean b) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("comment_content",b);
        editor.apply();
    }

    public static boolean isPushNotifCommentDiscuss() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getBoolean("comment_discuss", true);
    }

    public static void setPushNotifCommentDiscuss(boolean b) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("comment_discuss",b);
        editor.apply();
    }

    public static boolean isPushNotifSub() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getBoolean("rant_sub", true);
    }

    public static void setPushNotifSub(boolean b) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("rant_sub",b);
        editor.apply();
    }

    public static Boolean isSessionSkyVerified() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getBoolean("sessionSkyVerified", false);
    }

    public static void setSessionSkyVerified(Boolean verified) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putBoolean("sessionSkyVerified",verified);
        editor.apply();
    }
}
