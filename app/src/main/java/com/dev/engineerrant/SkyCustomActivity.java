package com.dev.engineerrant;

import static com.dev.engineerrant.app.toast;
import static com.dev.engineerrant.network.RetrofitClient.BASE_URL;
import static com.dev.engineerrant.network.RetrofitClient.SKY_SERVER_URL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dev.engineerrant.animations.Tools;
import com.dev.engineerrant.auth.Account;
import com.dev.engineerrant.classes.dev.Profile;
import com.dev.engineerrant.classes.sky.SkyProfile;
import com.dev.engineerrant.classes.sky.SkyProfileAuth;
import com.dev.engineerrant.network.RetrofitClient;
import com.dev.engineerrant.network.methods.dev.MethodsProfile;
import com.dev.engineerrant.network.methods.sky.MethodsSkyProfileAuth;
import com.dev.engineerrant.network.models.dev.ModelProfile;
import com.dev.engineerrant.network.models.sky.ModelSkyProfileAuth;
import com.dev.engineerrant.network.models.sky.ModelSuccess;
import com.dev.engineerrant.network.post.sky.BackupClient;
import com.dev.engineerrant.network.post.sky.CustomClient;

import java.nio.charset.StandardCharsets;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SkyCustomActivity extends AppCompatActivity {
    ConstraintLayout details;
    ProgressBar progressBar, progressBarBig;
    TextView account, textViewSave;
    EditText editTextAvatarFrameUrl, editTextAvatarBgColor, editTextAvatarBgUrl, editTextProfileBgUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sky_custom);
        initialize();
        setDetails();
    }


    private void setDetails() {
        progressBar.setVisibility(View.VISIBLE);
        MethodsSkyProfileAuth methods = RetrofitClient.getRetrofitInstance().create(MethodsSkyProfileAuth.class);
        String total_url = SKY_SERVER_URL+"my_profile/"+ Account.user_id()+"/"+Account.id();

        Call<ModelSkyProfileAuth> call = methods.getAllData(total_url);

        call.enqueue(new Callback<ModelSkyProfileAuth>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ModelSkyProfileAuth> call, @NonNull Response<ModelSkyProfileAuth> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    SkyProfileAuth profile = response.body().getProfile();

                    editTextAvatarFrameUrl.setText(profile.getAvatar_frame_url());
                    editTextAvatarBgColor.setText(profile.getAvatar_bg_hex());
                    editTextAvatarBgUrl.setText(profile.getAvatar_bg_url());
                    editTextProfileBgUrl.setText(profile.getProfile_bg_url());

                    progressBar.setVisibility(View.GONE);
                } else if (response.code() == 429) {
                    // Handle unauthorized
                    toast("you are not authorized");
                } else {
                    toast("no success "+response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelSkyProfileAuth> call, @NonNull Throwable t) {
                Log.d("error_contact", t.toString());
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void initialize() {
        details = findViewById(R.id.details);

        editTextAvatarFrameUrl = findViewById(R.id.editTextAvatarFrameUrl);
        editTextAvatarBgColor = findViewById(R.id.editTextAvatarBgColor);
        editTextAvatarBgUrl = findViewById(R.id.editTextAvatarBgUrl);
        editTextProfileBgUrl = findViewById(R.id.editTextProfileBgUrl);
        progressBar = findViewById(R.id.progressBar);
        textViewSave = findViewById(R.id.textViewSave);
        progressBarBig = findViewById(R.id.progressBarBig);
    }

    public void showDetails(View view) {
        if (details.getVisibility() == View.GONE) {
            details.setVisibility(View.VISIBLE);
        } else {
            details.setVisibility(View.GONE);
        }
    }

    public void sendCustom(View view) {
        progressBar.setVisibility(View.VISIBLE);

        String _avatar_frame_url = editTextAvatarFrameUrl.getText().toString();
        String _avatar_bg_hex = editTextAvatarBgColor.getText().toString();
        String _avatar_bg_url = editTextAvatarBgUrl.getText().toString();
        String _profile_bg_url = editTextProfileBgUrl.getText().toString();

        try {
            RequestBody user_id = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(Account.user_id()));
            RequestBody session_id = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(Account.id()));
            RequestBody avatar_frame_url = RequestBody.create(MediaType.parse("multipart/form-data"), _avatar_frame_url);
            RequestBody avatar_bg_hex = RequestBody.create(MediaType.parse("multipart/form-data"), _avatar_bg_hex);
            RequestBody avatar_bg_url = RequestBody.create(MediaType.parse("multipart/form-data"), _avatar_bg_url);
            RequestBody profile_bg_url = RequestBody.create(MediaType.parse("multipart/form-data"), _profile_bg_url);

            Retrofit.Builder builder = new Retrofit.Builder().baseUrl(SKY_SERVER_URL).addConverterFactory(GsonConverterFactory.create());
            Retrofit retrofit = builder.build();
            CustomClient client = retrofit.create(CustomClient.class);

            Call<ModelSuccess> call = client.upload(session_id, user_id,avatar_frame_url,avatar_bg_hex,avatar_bg_url,profile_bg_url);
            call.enqueue(new Callback<ModelSuccess>() {
                @Override
                public void onResponse(@NonNull Call<ModelSuccess> call, @NonNull Response<ModelSuccess> response) {
                    Log.v("Upload", response + " ");
                    progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        // Do awesome stuff
                        assert response.body() != null;
                        Boolean success = response.body().getSuccess();

                        if (success) {
                            toast("profile customized!");

                            if (Account.isLoggedIn()) {
                                Intent intent = new Intent(SkyCustomActivity.this, ProfileActivity.class);
                                intent.putExtra("user_id",String.valueOf(Account.user_id()));
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(SkyCustomActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }
                        } else {
                            toast("failed");
                        }
                    } else if (response.code() == 400) {
                        toast("Invalid login credentials entered. Please try again. :(");
                        // editTextPost.setText(response.message());
                    } else if (response.code() == 429) {
                        // Handle unauthorized
                        toast("You are not authorized :P");
                    } else {
                        toast(response.message());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ModelSuccess> call, @NonNull Throwable t) {
                    toast("Request failed! " + t.getMessage());
                    progressBar.setVisibility(View.GONE);
                }

            });
        } catch (Exception e) {
            toast("error "+e);
        }
    }

    public void openSteam(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://store.steampowered.com/points/shop/c/avatar/cluster/1"));
        startActivity(browserIntent);
    }
}