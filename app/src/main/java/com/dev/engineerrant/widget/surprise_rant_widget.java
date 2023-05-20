package com.dev.engineerrant.widget;

import static com.dev.engineerrant.network.RetrofitClient.BASE_URL;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.widget.RemoteViews;

import com.dev.engineerrant.MainActivity;
import com.dev.engineerrant.R;
import com.dev.engineerrant.auth.Account;
import com.dev.engineerrant.classes.dev.Counts;
import com.dev.engineerrant.classes.dev.User_avatar;
import com.dev.engineerrant.network.RetrofitClient;
import com.dev.engineerrant.network.methods.dev.MethodsProfile;
import com.dev.engineerrant.network.models.dev.ModelProfile;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Implementation of App Widget functionality.
 */
public class surprise_rant_widget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.surprise_rant_widget);

        getSurprise(views);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
    private static final String SYNC_CLICKED    = "automaticWidgetSyncButtonClick";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.surprise_rant_widget);

            ComponentName widget = new ComponentName(context, surprise_rant_widget.class);
            // views.setOnClickPendingIntent(R.id.appwidget_text, getPendingSelfIntent(context, SYNC_CLICKED));
            appWidgetManager.updateAppWidget(widget, views);

            getSurprise(views);


            Intent configIntent = new Intent(context, MainActivity.class);
            PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.appwidget_text, configPendingIntent);
            appWidgetManager.updateAppWidget(appWidgetIds, views);

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);

            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.onReceive(context, intent);

        if (SYNC_CLICKED.equals(intent.getAction())) {

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.surprise_rant_widget);
            ComponentName widget = new ComponentName(context, surprise_rant_widget.class);

            getSurprise(views);

            appWidgetManager.updateAppWidget(widget, views);

        }
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
    }
    private static void getSurprise(RemoteViews view) {

        view.setTextViewText(R.id.appwidget_text, "fetching ...");

        MethodsProfile methods = RetrofitClient.getRetrofitInstance().create(MethodsProfile.class);
        String total_url;
        if (Account.isLoggedIn()){
            total_url = BASE_URL + "users/"+Account.user_id()+"?app=3&token_id="+Account.id()+"&token_key="+Account.key()+"&user_id="+Account.user_id();
        } else {
            view.setTextViewText(R.id.appwidget_text, "please login");
           return;
        }

        Call<ModelProfile> call = methods.getAllData(total_url);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        try
        {
            Response<ModelProfile> response = call.execute();
            ModelProfile apiResponse = response.body();

            String user_avatar = response.body().getProfile().getAvatar().getI();
            String username = response.body().getProfile().getUsername();
            int score = response.body().getProfile().getScore();
            String about = response.body().getProfile().getAbout();
            String location = response.body().getProfile().getLocation();
            int created_time = response.body().getProfile().getCreated_time();
            String skills = response.body().getProfile().getSkills();
            String github = response.body().getProfile().getGithub();
            String website = response.body().getProfile().getWebsite();
            User_avatar avatar = response.body().getProfile().getAvatar();
            Counts counts = response.body().getProfile().getContent().getCounts();
            int rants_count = counts.getRants();
            int upvoted_count = counts.getUpvoted();
            int comments_count = counts.getComments();

            view.setTextViewText(R.id.appwidget_text, username+"\n\n"+about);
            //API response
            System.out.println(apiResponse);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            view.setTextViewText(R.id.appwidget_text, ex.toString());
        }

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}