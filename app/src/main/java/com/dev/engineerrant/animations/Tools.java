package com.dev.engineerrant.animations;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.dev.engineerrant.R;
import com.dev.engineerrant.auth.Account;
import com.dev.engineerrant.auth.MyApplication;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Tools {

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }



    private static int screenWidth = 0, screenHeight = 0;
    public static int getScreenWidth(Context context){
        if (screenWidth != 0)
            return screenWidth;

        calculateScreenSize(context);
        return screenWidth;
    }
    public static int getScreenHeight(Context context) {
        if (screenHeight != 0)
            return screenHeight;

        calculateScreenSize(context);
        return screenHeight;
    }

    private static void calculateScreenSize(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
    }

    public static void setTheme(Context context) {
        switch (Account.theme()) {
            case "light":
                context.setTheme(R.style.Theme_Light);
                break;
            case "dark":
                context.setTheme(R.style.Theme_Dark);
                break;
            case "amoled":
                context.setTheme(R.style.Theme_Amoled);
                break;
        }
    }

    public static void highlighter(String text, TextView tv) {
        SpannableStringBuilder builderHighlighted = new SpannableStringBuilder();

        String[] __args = text.split("\n");

        for (String __arg: __args) {
            __arg = __arg + " \n";
            if (__arg.startsWith("##") || __arg.startsWith("//")) {
                SpannableString str = new SpannableString(__arg);
                str.setSpan(new ForegroundColorSpan(Color.parseColor("#DAA06D")), 0, str.length(), 0);
                builderHighlighted.append(str);
            } else {
                String[] _args = __arg.split(" ");

                for (String _arg: _args) {

                    switch (_arg.toLowerCase().replaceAll(",","").replaceAll(";","")) {
                        case "function":
                        case "func":
                        case "fun": {
                            _arg = _arg + " ";
                            SpannableString str = new SpannableString(_arg);
                            str.setSpan(new ForegroundColorSpan(Color.parseColor("#d896ff")), 0, str.length(), 0);
                            builderHighlighted.append(str);
                            break;
                        }
                        case "true":
                        case "false": {
                            _arg = _arg + " ";
                            SpannableString str = new SpannableString(_arg);
                            str.setSpan(new ForegroundColorSpan(Color.parseColor("#DA70D6")), 0, str.length(), 0);
                            builderHighlighted.append(str);
                            break;
                        }
                        case "const":
                        case "var":
                        case "let":
                        case "boolean":
                        case "int":
                        case "integer":
                        case "string":
                        case "object":
                        case "long":
                        case "val":
                        case "double":
                        case "char":
                        case "undefined":
                        case "null":
                        case "bool": {
                            _arg = _arg + " ";
                            SpannableString str = new SpannableString(_arg);
                            str.setSpan(new ForegroundColorSpan(Color.parseColor("#A782EC")), 0, str.length(), 0);
                            builderHighlighted.append(str);
                            break;
                        }
                        case "else if":
                        case "elif":
                        case "if": {
                            _arg = _arg + " ";
                            SpannableString str = new SpannableString(_arg);
                            str.setSpan(new ForegroundColorSpan(Color.parseColor("#B59DFA")), 0, str.length(), 0);
                            builderHighlighted.append(str);
                            break;
                        }
                        default:
                            _arg = _arg + " ";
                            String[] args = _arg.split("");

                            for (String arg : args) {
                                if (arg.equals("{") || arg.equals("}")) {
                                    SpannableString str = new SpannableString(arg);
                                    str.setSpan(new ForegroundColorSpan(Color.parseColor("#efbbff")), 0, str.length(), 0);
                                    builderHighlighted.append(str);
                                } else if (arg.equals("(") || arg.equals(")")) {
                                    SpannableString str = new SpannableString(arg);
                                    str.setSpan(new ForegroundColorSpan(Color.parseColor("#CCACDB")), 0, str.length(), 0);
                                    builderHighlighted.append(str);
                                } else if (arg.equals("[") || arg.equals("]")) {
                                    SpannableString str = new SpannableString(arg);
                                    str.setSpan(new ForegroundColorSpan(Color.parseColor("#61346B")), 0, str.length(), 0);
                                    builderHighlighted.append(str);
                                } else if (arg.matches("[^\"]*\"[^\"]*") || arg.equals("'")) {
                                    SpannableString str = new SpannableString(arg);
                                    str.setSpan(new ForegroundColorSpan(Color.parseColor("#EADDCA")), 0, str.length(), 0);
                                    builderHighlighted.append(str);
                                } else if (arg.equals(":")) {
                                    SpannableString str = new SpannableString(arg);
                                    str.setSpan(new ForegroundColorSpan(Color.parseColor("#F0E68C")), 0, str.length(), 0);
                                    builderHighlighted.append(str);
                                } else if (arg.equals("=")) {
                                    SpannableString str = new SpannableString(arg);
                                    str.setSpan(new ForegroundColorSpan(Color.parseColor("#E6E6FA")), 0, str.length(), 0);
                                    builderHighlighted.append(str);
                                } else {
                                    builderHighlighted.append(arg);
                                }
                            }

                            break;
                    }
                    }
            }
        }



        tv.setText( builderHighlighted, TextView.BufferType.SPANNABLE);
    }
}