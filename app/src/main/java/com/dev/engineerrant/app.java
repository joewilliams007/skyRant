package com.dev.engineerrant;

import android.widget.Toast;

import com.dev.engineerrant.auth.MyApplication;

public class app {
    public static void toast(String message){
        Toast.makeText(MyApplication.getAppContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static void toastLong(String message){
        Toast.makeText(MyApplication.getAppContext(), message, Toast.LENGTH_LONG).show();
    }
}
