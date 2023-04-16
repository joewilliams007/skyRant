package com.dev.engineerrant.notifcenter;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dev.engineerrant.auth.Account;

public class AlarmReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Intent in = new Intent(context, CheckNotif.class);
        context.startService(in);
        setAlarm(context);
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    public void setAlarm(Context context)
    {
        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, AlarmReceiver.class);
        PendingIntent pi = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pi = PendingIntent.getBroadcast(context, 2, i, PendingIntent.FLAG_MUTABLE);
        } else {
            try {
                pi = PendingIntent.getBroadcast(context, 2, i, PendingIntent.FLAG_UPDATE_CURRENT);
            } catch (Exception ignored) {

            }
        }
        assert am != null;

        if (Account.isPushNotif()) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, (System.currentTimeMillis()/1000L + Account.pushNotifTime()) *1000L, pi); //Next alarm in 15s
        }
    }
}