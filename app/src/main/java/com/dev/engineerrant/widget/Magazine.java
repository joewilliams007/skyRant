package com.dev.engineerrant.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.dev.engineerrant.MagazineActivity;
import com.dev.engineerrant.MatrixChatActivity;
import com.dev.engineerrant.R;

/**
 * Implementation of App Widget functionality.
 */
public class Magazine extends AppWidgetProvider {

        @Override
        public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
            // There may be multiple widgets active, so update all of them
            for (int appWidgetId : appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId);

                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_magazine);
                // views.setTextViewText(R.id.appwidget_text, widgetText);


                Intent intent = new Intent(context, MagazineActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

                views.setOnClickPendingIntent(R.id.imageViewWidget, pendingIntent);

                // Instruct the widget manager to update the widget
                appWidgetManager.updateAppWidget(appWidgetId, views);
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

        static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                    int appWidgetId) {

            // CharSequence widgetText = context.getString(R.string.appwidget_text);
            // Construct the RemoteViews object
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_magazine);
            // views.setTextViewText(R.id.appwidget_text, widgetText);


            Intent intent = new Intent(context, MagazineActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            views.setOnClickPendingIntent(R.id.imageViewWidget, pendingIntent);

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }