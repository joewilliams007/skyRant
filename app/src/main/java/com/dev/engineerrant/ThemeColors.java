package com.dev.engineerrant;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.ColorInt;

class ThemeColors {
    private static final String NAME = "ThemeColors", KEY = "color";

    @ColorInt
    private int color;

    ThemeColors(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        String stringColor = sharedPreferences.getString(KEY, "004bff");
        // color = Color.parseColor("#" + stringColor);

        //if (isLightActionBar()) context.setTheme(R.th.AppTheme);
        context.setTheme(context.getResources().getIdentifier("T_" + stringColor, "style", context.getPackageName()));
    }

    static void setNewThemeColor(Activity activity, int red, int green, int blue) {
        red = Math.round(red / 15.0f) * 15;
        green = Math.round(green / 15.0f) * 15;
        blue = Math.round(blue / 15.0f) * 15;

        String stringColor = Integer.toHexString(Color.rgb(red, green, blue)).substring(2);
        SharedPreferences.Editor editor = activity.getSharedPreferences(NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY, stringColor);
        editor.apply();

        activity.recreate();
    }

    private boolean isLightActionBar() {// Checking if title text color will be black
        int rgb = (Color.red(color) + Color.green(color) + Color.blue(color)) / 3;
        return rgb > 210;
    }
}