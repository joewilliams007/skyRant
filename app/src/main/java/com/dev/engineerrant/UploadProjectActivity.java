package com.dev.engineerrant;

import static android.text.format.DateUtils.getRelativeTimeSpanString;
import static com.dev.engineerrant.app.toast;
import static com.dev.engineerrant.network.RetrofitClient.BASE_URL;
import static com.dev.engineerrant.network.RetrofitClient.SKY_SERVER_URL;

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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dev.engineerrant.adapters.CommunityPostItem;
import com.dev.engineerrant.animations.Tools;
import com.dev.engineerrant.auth.Account;
import com.dev.engineerrant.auth.MyApplication;
import com.dev.engineerrant.network.RetrofitClient;
import com.dev.engineerrant.network.methods.dev.MethodsProfile;
import com.dev.engineerrant.network.methods.git.MethodsRepo;
import com.dev.engineerrant.network.models.dev.ModelProfile;
import com.dev.engineerrant.network.models.git.ModelRepo;
import com.dev.engineerrant.network.models.sky.ModelSuccess;
import com.dev.engineerrant.network.post.RantClient;
import com.dev.engineerrant.network.post.sky.UploadProjectClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UploadProjectActivity extends AppCompatActivity {

    EditText editTextTitle, editTextType, editTextDesc, editTextGithub, editTextWebsite, editTextOwner, editTextOwnerUserId, editTextOs, editTextLanguage,editTextDRUrl;
    SwitchCompat active, archived;
    ProgressBar progressBar;
    TextView upload;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_project);

        if (!Account.isLoggedIn()) {
            Intent intent = new Intent(UploadProjectActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        if (!Account.isSessionSkyVerified()) {
            Intent intent = new Intent(UploadProjectActivity.this, SkyLoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        initialize();
        checkScore();
    }

    private void checkScore() {
        MethodsProfile methods = RetrofitClient.getRetrofitInstance().create(MethodsProfile.class);
        String total_url = BASE_URL + "users/"+Account.user_id()+"?app=3";
        progressBar.setVisibility(View.VISIBLE);
        Call<ModelProfile> call = methods.getAllData(total_url);
        call.enqueue(new Callback<ModelProfile>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ModelProfile> call, @NonNull Response<ModelProfile> response) {
                if (response.isSuccessful()) {
                    // Do awesome stuff
                    assert response.body() != null;

                    if (response.body().getProfile().getScore()<500) {
                        finish();
                        toast("500 devRant points required");
                    }
                } else if (response.code() == 429) {
                    // Handle unauthorized
                } else {
                    toast(response.message());
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<ModelProfile> call, @NonNull Throwable t) {
                Log.d("error_contact", t.toString());
                toast(t.toString());
                progressBar.setVisibility(View.GONE);
            }
        });
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
        editTextDRUrl = findViewById(R.id.editTextDRUrl);
        active = findViewById(R.id.switchActive);
        archived = findViewById(R.id.switchArchived);
        upload = findViewById(R.id.upload);
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

    public void uploadProject(View view) {
        String title = editTextTitle.getText().toString();
        String type = editTextType.getText().toString();
        String desc = editTextDesc.getText().toString();
        String website = editTextWebsite.getText().toString();
        String github = editTextGithub.getText().toString();
        String owner = editTextOwner.getText().toString();
        String ownerUserId = editTextOwnerUserId.getText().toString();
        String os = editTextOs.getText().toString();
        String language = editTextLanguage.getText().toString();
        String relevant_dr_url = editTextDRUrl.getText().toString();

        Boolean isActive = active.isChecked();
        Boolean isArchived = active.isChecked();

        if (title.length()<4||title.length()>2000) {
            toast("title must be 4-2000 characters long");
            return;
        }
        if (os.length()>2000) {
            toast("os must be max 2000 characters long");
            return;
        }
        if (type.length()<3||type.length()>2000) {
            toast("type must be at least 3 characters long");
            return;
        }
        if (desc.length()<3||desc.length()>2000) {
            toast("description must be 3-2000 characters long");
            return;
        }
        if (relevant_dr_url.length()>2000) {
            toast("relevant_dr_url must be max 2000 characters long");
            return;
        }
        if (website.length()>2000) {
            toast("website must be max 2000 characters long");
            return;
        }
        String invalid_message = "enter valid GitHub Repository URL";
        if (github.length()<12||github.length()>2000) {
            toast(invalid_message);
            return;
        }

        if (!github.contains("github.com")) {
            toast(invalid_message);
            return;
        }
        if (owner.length()<3||owner.length()>2000) {
            toast("owner name must be at least 3 characters long");
            return;
        }

        upload.setVisibility(View.GONE);
        try {
            RequestBody _title = RequestBody.create(MediaType.parse("multipart/form-data"), title);
            RequestBody _type= RequestBody.create(MediaType.parse("multipart/form-data"), type);
            RequestBody _desc= RequestBody.create(MediaType.parse("multipart/form-data"), desc);
            RequestBody _website= RequestBody.create(MediaType.parse("multipart/form-data"), website);
            RequestBody _github= RequestBody.create(MediaType.parse("multipart/form-data"), github);
            RequestBody _owner= RequestBody.create(MediaType.parse("multipart/form-data"), owner);
            RequestBody _ownerUserId= RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(ownerUserId));
            RequestBody _os= RequestBody.create(MediaType.parse("multipart/form-data"), os);
            RequestBody _language= RequestBody.create(MediaType.parse("multipart/form-data"), language);
            RequestBody _relevant_dr_url= RequestBody.create(MediaType.parse("multipart/form-data"), relevant_dr_url);

            RequestBody _active= RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(isActive));
            RequestBody _archived= RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(isArchived));

            RequestBody _user_id = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(Account.user_id()));
            RequestBody _session_id = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(Account.id()));

            Retrofit.Builder builder = new Retrofit.Builder().baseUrl(SKY_SERVER_URL).addConverterFactory(GsonConverterFactory.create());
            Retrofit retrofit = builder.build();

            UploadProjectClient client = retrofit.create(UploadProjectClient.class);
            // finally, execute the request

            Call<ModelSuccess> call = client.upload(_session_id,_user_id,_title,_os,_type,null,_desc,_relevant_dr_url,_website,_github,_language,_active,_archived,_ownerUserId,_owner);
            call.enqueue(new Callback<ModelSuccess>() {
                @Override
                public void onResponse(@NonNull Call<ModelSuccess> call, @NonNull Response<ModelSuccess> response) {
                    Log.v("Upload", response + " ");

                    if (response.isSuccessful()) {
                        // Do awesome stuff
                        assert response.body() != null;
                        Boolean success = response.body().getSuccess();

                        if (success) {
                            toast("success!");

                            Intent intent = new Intent(UploadProjectActivity.this, CommunityActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            toast("failed.");
                            upload.setVisibility(View.VISIBLE);
                            // editTextPost.setText(response.message()+" "+response.toString());
                        }
                    } else if (response.code() == 400) {
                        toast("Invalid login credentials entered. Please try again. :(");
                        // editTextPost.setText(response.message());
                        upload.setVisibility(View.VISIBLE);
                    } else if (response.code() == 429) {
                        // Handle unauthorized
                        toast("You are not authorized :P");
                        upload.setVisibility(View.VISIBLE);
                    } else {
                        toast(response.message());
                        upload.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ModelSuccess> call, @NonNull Throwable t) {
                    toast("Request failed! " + t.getMessage());
                    upload.setVisibility(View.VISIBLE);
                }

            });
        } catch (Exception e) {
            toast("failed");
            upload.setVisibility(View.VISIBLE);
        }
    }
}