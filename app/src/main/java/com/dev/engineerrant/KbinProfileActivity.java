package com.dev.engineerrant;

import static com.dev.engineerrant.app.toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dev.engineerrant.animations.Tools;
import com.dev.engineerrant.auth.Account;
import com.dev.engineerrant.auth.MyApplication;
import com.dev.engineerrant.classes.kbin.Items;
import com.dev.engineerrant.network.DownloadImageTask;
import com.dev.engineerrant.network.RetrofitClient;
import com.dev.engineerrant.network.methods.kbin.MethodsEntries;
import com.dev.engineerrant.network.methods.kbin.MethodsUser;
import com.dev.engineerrant.network.models.kbin.ModelEntries;
import com.dev.engineerrant.network.models.kbin.ModelUser;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KbinProfileActivity extends AppCompatActivity {

    ImageView imageViewCover, imageViewProfile;
    TextView textViewUsername, textViewAbout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kbin_profile);
        initialize();
        Intent i = getIntent();
        getUser(i.getStringExtra("userId"));
    }

    private void initialize() {
        imageViewCover = findViewById(R.id.imageViewCover);
        imageViewProfile = findViewById(R.id.imageViewProfile);
        textViewAbout = findViewById(R.id.textViewAbout);
        textViewUsername = findViewById(R.id.textViewUsername);
    }

    private void getUser(String userId) {
        MethodsUser methods = RetrofitClient.getRetrofitInstance().create(MethodsUser.class);
        String total_url = "https://kbin.melroy.org/api/users/"+userId;


        Call<ModelUser> call = methods.getAllData(total_url);
        call.enqueue(new Callback<ModelUser>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ModelUser> call, @NonNull Response<ModelUser> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;

                    textViewAbout.setText(response.body().getAbout());
                    textViewUsername.setText(response.body().getUsername());

                    if (response.body().getAvatar()!=null) {
                        Glide.with(MyApplication.getAppContext()).load(response.body().getAvatar().getStorageUrl()).into(imageViewProfile);
                    }
                    if (response.body().getCover() != null) {
                        if (Account.autoLoad()) {
                            new DownloadImageTask(imageViewCover)
                                    .execute(response.body().getCover().getStorageUrl());
                        }
                    }

                } else if (response.code() == 429) {
                    // Handle unauthorized
                    toast("you are not authorized");
                } else {
                    toast(response.code()+" ");
                }
                System.out.println(total_url);
            }

            @Override
            public void onFailure(@NonNull Call<ModelUser> call, @NonNull Throwable t) {
                Log.d("error_contact", t.toString());
                toast("no network");
                System.out.println(total_url);
            }
        });
    }
}