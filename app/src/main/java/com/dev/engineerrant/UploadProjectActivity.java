package com.dev.engineerrant;

import static android.text.format.DateUtils.getRelativeTimeSpanString;
import static com.dev.engineerrant.app.toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.dev.engineerrant.adapters.CommunityPostItem;
import com.dev.engineerrant.animations.Tools;
import com.dev.engineerrant.auth.Account;
import com.dev.engineerrant.auth.MyApplication;
import com.dev.engineerrant.network.RetrofitClient;
import com.dev.engineerrant.network.methods.git.MethodsRepo;
import com.dev.engineerrant.network.models.git.ModelRepo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadProjectActivity extends AppCompatActivity {

    EditText editTextTitle, editTextType, editTextDesc, editTextGithub, editTextWebsite, editTextOwner, editTextOwnerUserId, editTextOs, editTextLanguage;
    SwitchCompat active, archived;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_project);
        initialize();
    }

    private void initialize() {
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextType = findViewById(R.id.editTextType);
        editTextDesc = findViewById(R.id.editTextDesc);
        editTextWebsite = findViewById(R.id.editTextWebsite);
        editTextGithub = findViewById(R.id.editTextGithub);
        editTextOwner = findViewById(R.id.editTextOwner);
        editTextOwnerUserId = findViewById(R.id.editTextOwnerUserId);
        editTextOs = findViewById(R.id.editTextOs);
        editTextLanguage = findViewById(R.id.editTextLanguage);
        active = findViewById(R.id.switchActive);
        archived = findViewById(R.id.switchArchived);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
    }

    public void fetchGithub(View view) {
        String invalid_message = "enter valid GitHub Repository URL";
        String github_url = editTextGithub.getText().toString();
        if (github_url.length()<12) {
            toast(invalid_message);
            return;
        }

        if (!github_url.contains("github.com")) {
            toast(invalid_message);
            return;
        }

        getRepo(github_url);
    }

    private void getRepo(String github_url) {
        String total_url = "https://api.github.com/repos/"+github_url.split("com/")[1];

        if (total_url.endsWith("/")) {
            total_url = total_url.substring(0, total_url.length() - 1);
        }

        MethodsRepo methods = RetrofitClient.getRetrofitInstance().create(MethodsRepo.class);
        progressBar.setVisibility(View.VISIBLE);

        String header = null;
        if (Account.githubKey()!=null) {
            header = "token "+Account.githubKey();
        }
        Call<ModelRepo> call = methods.getAllData(header,total_url);
        call.enqueue(new Callback<ModelRepo>() {
            @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
            @Override
            public void onResponse(@NonNull Call<ModelRepo> call, @NonNull Response<ModelRepo> response) {
                if (response.isSuccessful()) {

                    // Do  awesome stuff
                    assert response.body() != null;


                    editTextTitle.setText(response.body().getName());
                    editTextDesc.setText(response.body().getDescription());
                    editTextOwner.setText(response.body().getOwner().getLogin());
                    editTextOs = findViewById(R.id.editTextOs);
                    archived.setChecked(response.body().getArchived());
                    editTextLanguage.setText(response.body().getLanguage());

                    toast("done");

                } else if (response.code() == 429) {
                    // Handle unauthorized
                    toast("error contacting github error 429");
                } else {
                    toast("error contacting github");
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<ModelRepo> call, @NonNull Throwable t) {
                Log.d("error_contact", t.toString());
                toast("no network");
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}