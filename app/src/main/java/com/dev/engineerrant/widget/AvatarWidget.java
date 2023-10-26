package com.dev.engineerrant.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.AppWidgetTarget;
import com.bumptech.glide.request.transition.Transition;
import com.dev.engineerrant.MainActivity;
import com.dev.engineerrant.MatrixChatActivity;
import com.dev.engineerrant.ProfileActivity;
import com.dev.engineerrant.R;
import com.dev.engineerrant.auth.Account;
import com.dev.engineerrant.auth.MyApplication;
import com.dev.engineerrant.network.DownloadImageTaskAlter;

import java.io.InputStream;

/**
 * Implementation of App Widget functionality.
 */
public class AvatarWidget extends AppWidgetProvider {

        @Override
        public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
            // There may be multiple widgets active, so update all of them
            for (int appWidgetId : appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId);

                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.avatar_widget);
                // views.setTextViewText(R.id.appwidget_text, widgetText);


                Intent intent = new Intent(context, ProfileActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);


                if (Account.avatarImg()!=null) {
                    AppWidgetTarget awt = new AppWidgetTarget(context, R.id.imageViewWidget, views, appWidgetId) {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            super.onResourceReady(adjust(resource), transition);
                        }
                    };

                    RequestOptions options = new RequestOptions().
                            override(300, 300).placeholder(R.drawable.ball_love).error(R.drawable.ball_angry);


                    new Thread(new Runnable() {
                        public void run() {
                            Bitmap mIcon11 = null;
                            try {
                                InputStream in = new java.net.URL(Account.avatarImg()).openStream();
                                mIcon11 = BitmapFactory.decodeStream(in);
                            } catch (Exception e) {
                                Log.e("ErrorL", e.getMessage());
                                e.printStackTrace();
                            }

                            Glide.with(context.getApplicationContext())
                                    .asBitmap()
                                    .load(adjust(mIcon11))
                                    .apply(options)
                                    .into(awt);
                        }
                    }).start();
                }


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
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.avatar_widget);
            // views.setTextViewText(R.id.appwidget_text, widgetText);


            Intent intent = new Intent(context, ProfileActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            if (Account.avatarImg()!=null) {
                AppWidgetTarget awt = new AppWidgetTarget(context, R.id.imageViewWidget, views, appWidgetId) {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        super.onResourceReady(adjust(resource), transition);
                    }
                };

                RequestOptions options = new RequestOptions().
                        override(300, 300).placeholder(R.drawable.ball_love).error(R.drawable.ball_angry);


                new Thread(new Runnable() {
                    public void run() {
                        Bitmap mIcon11 = null;
                        try {
                            InputStream in = new java.net.URL(Account.avatarImg()).openStream();
                            mIcon11 = BitmapFactory.decodeStream(in);
                        } catch (Exception e) {
                            Log.e("ErrorL", e.getMessage());
                            e.printStackTrace();
                            //   mIcon11 = BitmapFactory.decodeResource(MyApplication.getAppContext().getResources(),R.drawable.error);
                        }

                        Glide.with(context.getApplicationContext())
                                .asBitmap()
                                .load(adjust(mIcon11))
                                .apply(options)
                                .into(awt);
                    }
                }).start();
            }


            views.setOnClickPendingIntent(R.id.imageViewWidget, pendingIntent);

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

        private static Bitmap adjust(Bitmap src)
        {
            // int to = Color.BLACK;
            int to = Color.alpha(0);

            //Need to copy to ensure that the bitmap is mutable.


            Bitmap bitmap = src.copy(Bitmap.Config.ARGB_8888, true);
            for(int x = 0;x < bitmap.getWidth();x++)
                for(int y = 0;y < bitmap.getHeight();y++)
                    if(match(bitmap.getPixel(x, y)))
                        bitmap.setPixel(x, y, to);

            return bitmap;
        }
        private static final int[] FROM_COLOR = new int[]{123,200,164};
        private static final int THRESHOLD = 10;
        private static boolean match(int pixel)
        {
            //There may be a better way to match, but I wanted to do a comparison ignoring
            //transparency, so I couldn't just do a direct integer compare.
            return Math.abs(Color.red(pixel) - FROM_COLOR[0]) < THRESHOLD &&
                    Math.abs(Color.green(pixel) - FROM_COLOR[1]) < THRESHOLD &&
                    Math.abs(Color.blue(pixel) - FROM_COLOR[2]) < THRESHOLD;
        }
    }