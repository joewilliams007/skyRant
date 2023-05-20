package com.dev.engineerrant;

import static com.dev.engineerrant.app.toast;
import static com.dev.engineerrant.network.RetrofitClient.BASE_URL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.dev.engineerrant.animations.Tools;
import com.dev.engineerrant.auth.Account;
import com.dev.engineerrant.auth.MyApplication;
import com.dev.engineerrant.classes.dev.News;
import com.dev.engineerrant.network.RetrofitClient;
import com.dev.engineerrant.network.methods.dev.MethodsFeed;
import com.dev.engineerrant.network.models.dev.ModelFeed;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeeklyActivity extends AppCompatActivity {

    TextView textViewWeekly;
    News news;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly);
        textViewWeekly = findViewById(R.id.textViewDesc);
        news = null;
        startReq();
    }
    private void startReq() {
        MethodsFeed methods = RetrofitClient.getRetrofitInstance().create(MethodsFeed.class);
        String total_url;
            total_url = BASE_URL
                    + "devrant/rants?app=3&limit=1&sort=recent&range=all&skip=0/";

        Call<ModelFeed> call = methods.getAllData(total_url);
        call.enqueue(new Callback<ModelFeed>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ModelFeed> call, @NonNull Response<ModelFeed> response) {
                if (response.isSuccessful()) {

                    // Do awesome stuff
                    assert response.body() != null;
                    news = response.body().getNews();

                    if (news == null) {
                        toast("no weekly available (null)");
                    } else {
                        textViewWeekly.setText(news.getBody()+"\n\n"+response.body().getNews().getFooter());
                    }

                    if (!response.body().getNews().getAction().equals("grouprant")) {
                        toast("this announcement is not a weekly and not supported");
                        textViewWeekly.setText("this announcement type is not a weekly and not yet supported by skyRant.\n\nBody: \n\n"+news.getBody()+"\n\nFooter:\n\n"+response.body().getNews().getFooter());
                    }

                } else if (response.code() == 429) {
                    // Handle unauthorized
                    toast("you are not authorized");
                } else {
                    toast("please logout and in again - "+response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelFeed> call, @NonNull Throwable t) {
                Log.d("error_contact", t.toString());
                toast("no network");
            }
        });
    }

    public void compose(View view) {
        if (Account.isLoggedIn()) {
            if (news!=null) {
                Intent intent = new Intent(WeeklyActivity.this, PostComposeActivity.class);
                intent.putExtra("weekly",news.getFooter().split("'")[1]);
                intent.putExtra("type", "1");
                intent.putExtra("typeName", "R a n t / S t o r y");
                startActivity(intent);
            } else {
                toast("please wait until page loaded");
            }
        } else {
            Intent intent = new Intent(WeeklyActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    public void others(View view) {
        if (news!=null) {
            Intent i = new Intent(MyApplication.getAppContext(), MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("searchTag", news.getFooter().split("'")[1]);
            MyApplication.getAppContext().startActivity(i);
        } else {
            toast("please wait until page loaded");
        }
    }
}