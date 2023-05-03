package com.dev.engineerrant.notifcenter;

import static com.dev.engineerrant.app.toast;
import static com.dev.engineerrant.network.RetrofitClient.BASE_URL;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.dev.engineerrant.NotifActivity;
import com.dev.engineerrant.R;
import com.dev.engineerrant.RantActivity;
import com.dev.engineerrant.auth.Account;
import com.dev.engineerrant.classes.NotifItems;
import com.dev.engineerrant.classes.NotifUnread;
import com.dev.engineerrant.methods.MethodsNotif;
import com.dev.engineerrant.models.ModelNotif;
import com.dev.engineerrant.network.RetrofitClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CheckNotif extends Service {

    // This method run only one time. At the first time of service created and running
    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        Log.d("onCreate()s", "After service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (Account.isPushNotif()) {
            ConnectivityManager connMgr =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            boolean isWifiConn = false;
            boolean isMobileConn = false;
            for (Network network : connMgr.getAllNetworks()) {
                NetworkInfo networkInfo = connMgr.getNetworkInfo(network);
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    isWifiConn |= networkInfo.isConnected();
                }
                if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    isMobileConn |= networkInfo.isConnected();
                }
            }

            if (isWifiConn || isMobileConn) {
                startReq();
            }
        }

        return START_STICKY;
    }

    private void startReq() {
        System.out.println("LOOKING FOR NOTIFICATIONS");
        MethodsNotif methods = RetrofitClient.getRetrofitInstance().create(MethodsNotif.class);
        String total_url = BASE_URL
                + "users/me/notif-feed?"+"token_id="+ Account.id()+"&token_key="+Account.key()+"&user_id="+Account.user_id()+"&app=3"+"/";

        System.out.println(total_url);
        Call<ModelNotif> call = methods.getAllData(total_url);
        call.enqueue(new Callback<ModelNotif>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ModelNotif> call, @NonNull Response<ModelNotif> response) {
                if (response.isSuccessful()) {

                    // Do awesome stuff
                    assert response.body() != null;
                    Boolean success = response.body().getSuccess();
                    List<NotifItems> items = response.body().getData().getItems();
                    NotifUnread unread = response.body().getData().getUnread();
                    Map<Integer, String> username_map = response.body().getData().getUsername_map();
                    createFeedList(items, unread, username_map);
                } else if (response.code() == 429) {
                    // Handle unauthorized
                    toast(response.message());
                } else {
                    toast(response+" ");

                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelNotif> call, @NonNull Throwable t) {
                Log.d("error_contact", t.toString());
            }
        });
    }


    public void createFeedList(List<NotifItems> items, NotifUnread unread, Map<Integer, String> username_map){
        int all = unread.getAll();
        int upvotes = unread.getUpvotes();
        int mentions = unread.getMentions();
        int comments = unread.getComments();
        int subs = unread.getSubs();
        int total = unread.getTotal();

        for (NotifItems item : items){
            if (item.getCreated_time()>Account.lastPushNotifTime())
            {
                Account.setLastPushNotifTime(item.getCreated_time());

                String text = username_map.get(item.getUid());
                String title = null;
                String id = String.valueOf(item.getRant_id());
                switch (item.getType()) {
                    case "comment_vote":
                        text+= " ++'d your comment!";
                        title = "New Like!";

                        if (Account.isPushNotifCommentVote()) {
                            sendNotification(text, title, id);
                        }
                        break;
                    case "comment_content":
                        text+= " commented on your rant!";
                        title = "New Comment!";
                        if (Account.isPushNotifComment()) {
                            sendNotification(text, title, id);
                        }
                        break;
                    case "comment_mention":
                        text+= " mentioned you in a comment!";
                        title = "New Mention!";
                        if (Account.isPushNotifMention()) {
                            sendNotification(text, title, id);
                        }
                        break;
                    case "comment_discuss":
                        text+= " (or more) new comments on a rant you commented on!";
                        title = "New Comments!";
                        if (Account.isPushNotifCommentDiscuss()) {
                            sendNotification(text, title, id);
                        }
                        break;
                    case "content_vote":
                        text+= " ++'d your rant!";
                        title = "New Like!";
                        if (Account.isPushNotifRantVote()) {
                            sendNotification(text, title, id);
                        }
                        break;
                    case "rant_sub":
                        text+= " posted a new rant!";
                        title = "New Post!";
                        if (Account.isPushNotifSub()) {
                            sendNotification(text, title, id);
                        }
                        break;
                }
            }
        }
    }

    private void sendNotification(String text, String title, String id) {
        System.out.println("CREATE NOTIF: "+text);
        String CHANNEL_ID="skyRant-Notif";
        long notificationId = System.currentTimeMillis();

        NotificationChannel notificationChannel= null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ID,"skyRant", NotificationManager.IMPORTANCE_DEFAULT);
        }

        PendingIntent pendingIntent= null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(getApplicationContext(),1,new Intent(this, NotifActivity.class).putExtra("id",id).putExtra("info","false"),PendingIntent.FLAG_MUTABLE);
        } else {
            Intent intent = new Intent(getApplicationContext(), NotifActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("id",id).putExtra("info","false");
            pendingIntent = PendingIntent.getActivity(this, (int) (Math.random() * 100), intent, PendingIntent.FLAG_IMMUTABLE);
        }
        Notification notification= null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new Notification.Builder(getApplicationContext(),CHANNEL_ID)
                    .setContentText(text)
                    .setContentTitle(title)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setChannelId(CHANNEL_ID)
                    .setSmallIcon(R.drawable.icon)
                    .build();
        }

        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(notificationChannel);
        }
        notificationManager.notify((int) notificationId,notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding
        return null;
    }

}
