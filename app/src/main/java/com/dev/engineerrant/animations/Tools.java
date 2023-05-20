package com.dev.engineerrant.animations;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.dev.engineerrant.MainActivity;
import com.dev.engineerrant.R;
import com.dev.engineerrant.auth.Account;
import com.dev.engineerrant.auth.MyApplication;
import com.google.android.material.color.MaterialColors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Tools {
    public static boolean isEmoji(String message){
        return message.matches("(?:[\uD83C\uDF00-\uD83D\uDDFF]|[\uD83E\uDD00-\uD83E\uDDFF]|" +
                "[\uD83D\uDE00-\uD83D\uDE4F]|[\uD83D\uDE80-\uD83D\uDEFF]|" +
                "[\u2600-\u26FF]\uFE0F?|[\u2700-\u27BF]\uFE0F?|\u24C2\uFE0F?|" +
                "[\uD83C\uDDE6-\uD83C\uDDFF]{1,2}|" +
                "[\uD83C\uDD70\uD83C\uDD71\uD83C\uDD7E\uD83C\uDD7F\uD83C\uDD8E\uD83C\uDD91-\uD83C\uDD9A]\uFE0F?|" +
                "[\u0023\u002A\u0030-\u0039]\uFE0F?\u20E3|[\u2194-\u2199\u21A9-\u21AA]\uFE0F?|[\u2B05-\u2B07\u2B1B\u2B1C\u2B50\u2B55]\uFE0F?|" +
                "[\u2934\u2935]\uFE0F?|[\u3030\u303D]\uFE0F?|[\u3297\u3299]\uFE0F?|" +
                "[\uD83C\uDE01\uD83C\uDE02\uD83C\uDE1A\uD83C\uDE2F\uD83C\uDE32-\uD83C\uDE3A\uD83C\uDE50\uD83C\uDE51]\uFE0F?|" +
                "[\u203C\u2049]\uFE0F?|[\u25AA\u25AB\u25B6\u25C0\u25FB-\u25FE]\uFE0F?|" +
                "[\u00A9\u00AE]\uFE0F?|[\u2122\u2139]\uFE0F?|\uD83C\uDC04\uFE0F?|\uD83C\uDCCF\uFE0F?|" +
                "[\u231A\u231B\u2328\u23CF\u23E9-\u23F3\u23F8-\u23FA]\uFE0F?)+");
    }

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
            case "green":
                context.setTheme(R.style.Theme_Green);
                break;
            case "discord":
                context.setTheme(R.style.Theme_Discord);
                break;
            case "coffee":
                context.setTheme(R.style.Theme_Coffee);
                break;
        }

        if (Account.language()!=null) {
            Locale locale = new Locale("de");
            Locale.setDefault(locale);

            Configuration config = new Configuration();
            config.setLocale(locale);
            context.getResources().updateConfiguration(config,
                    context.getResources().getDisplayMetrics());
        } else {
            Locale locale = new Locale("en");
            Locale.setDefault(locale);

            Configuration config = new Configuration();
            config.setLocale(locale);
            context.getResources().updateConfiguration(config,
                    context.getResources().getDisplayMetrics());
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
                            if (!_arg.equals("\n")) {
                                _arg = _arg + " ";
                            }
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

    public static void logHighlighter(String text, TextView tv) {
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

                    switch (_arg.replaceAll(",","").replaceAll(";","")) {
                        case "FIX":
                        case "FIXED":{
                            _arg = _arg + " ";
                            SpannableString str = new SpannableString(_arg);
                            str.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFF0000")), 0, str.length(), 0);
                            builderHighlighted.append(str);
                            break;
                        }
                        case "SKYRANT":
                        case "IMPROVED": {
                            _arg = _arg + " ";
                            SpannableString str = new SpannableString(_arg);
                            str.setSpan(new ForegroundColorSpan(Color.parseColor("#0096FF")), 0, str.length(), 0);
                            builderHighlighted.append(str);
                            break;
                        }
                        case "MISC":
                        case "MOVED": {
                            _arg = _arg + " ";
                            SpannableString str = new SpannableString(_arg);
                            str.setSpan(new ForegroundColorSpan(Color.parseColor("#FFC300")), 0, str.length(), 0);
                            builderHighlighted.append(str);
                            break;
                        }
                        case "ADDED":
                        case "RE-ADDED":
                        case "FEATURE":
                        case "NEW": {
                            _arg = _arg + " ";
                            SpannableString str = new SpannableString(_arg);
                            str.setSpan(new ForegroundColorSpan(Color.parseColor("#008f39")), 0, str.length(), 0);
                            builderHighlighted.append(str);
                            break;
                        }
                        default:
                            if (!_arg.equals("\n")) {
                                _arg = _arg + " ";
                            }
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
    public static void textUrlMentionHighlighter(String text, TextView tv) {

    }
    public static void textTagsHighlighter(String text, TextView tv) {
        SpannableString ss = new SpannableString(text);
        String[] args = text.split(",");
        for (String arg : args) {
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    String searchTag = arg;
                    if (searchTag.startsWith(" ")) {
                        searchTag = searchTag.substring(1);
                    }

                    Intent i = new Intent(MyApplication.getAppContext(), MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("searchTag",searchTag);
                    MyApplication.getAppContext().startActivity(i);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(true);
                }
            };

            String _searchTag = arg;
            if (_searchTag.startsWith(" ")) {
                _searchTag = _searchTag.substring(1);
            }

            ss.setSpan(clickableSpan, text.indexOf(_searchTag), text.indexOf(arg) + arg.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        tv.setText(ss);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        tv.setPaintFlags(0);
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static Boolean writeStringAsFile(final String fileContents, String fileName) {
        Context context = MyApplication.getAppContext();
        try {
            FileWriter out = new FileWriter(new File(context.getFilesDir(), fileName));
            out.write(fileContents);
            out.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static String readFileAsString(String fileName) {
        Context context = MyApplication.getAppContext();
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader in = null;

        try {
            in = new BufferedReader(new FileReader(new File(context.getFilesDir(), fileName)));
            while ((line = in.readLine()) != null) stringBuilder.append(line);

        } catch (FileNotFoundException e) {
            return "no file";
        } catch (IOException e) {
            return "error";
        }

        return stringBuilder.toString();
    }
}