package com.dev.engineerrant;

import static android.text.format.DateUtils.getRelativeTimeSpanString;
import static com.dev.engineerrant.app.toast;
import static com.dev.engineerrant.network.RetrofitClient.SKY_SERVER_URL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dev.engineerrant.animations.Tools;
import com.dev.engineerrant.auth.Account;
import com.dev.engineerrant.classes.sky.SkyProfile;
import com.dev.engineerrant.classes.sky.SkyProfileAuth;
import com.dev.engineerrant.network.RetrofitClient;
import com.dev.engineerrant.network.methods.sky.MethodsSkyProfileAuth;
import com.dev.engineerrant.network.models.sky.ModelSkyProfileAuth;
import com.dev.engineerrant.network.models.sky.ModelSuccess;
import com.dev.engineerrant.network.post.sky.DeleteAccountClient;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SkyActivity extends AppCompatActivity {
    TextView textViewUsedScoreAmount, textViewUsername, textViewDate;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sky);
        textViewUsedScoreAmount = findViewById(R.id.textViewUsedScoreAmount);
        textViewUsername = findViewById(R.id.textViewUsername);
        progressBar = findViewById(R.id.progressBar);
        textViewDate = findViewById(R.id.textViewDate);
        getProfile();
    }

    private void getProfile() {
        try {
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
                        textViewUsedScoreAmount.setText(String.valueOf(profile.getUsed_score()));
                        textViewUsername.setText(profile.getUsername());
                        textViewDate.setText("registered to sky "+getRelativeTimeSpanString(profile.getTimestamp()* 1000L));
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
                    progressBar.setVisibility(View.GONE);
                    finish();
                    toast("either you have no data or server is offline :')");
                }
            });
        } catch (Exception e) {

        }
    }

    public void verifyAgain(View view) {
        Intent intent = new Intent(SkyActivity.this, SkyLoginActivity.class);
        startActivity(intent);
    }

    public void backupRestore(View view) {
        Intent intent = new Intent(SkyActivity.this, BackupActivity.class);
        startActivity(intent);
    }

    public void deleteSky(View view) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this)
                .setTitle("Delete sky account")
                .setMessage("Delete all data (profile, reactions, data, etc..) associated with your devRant user id ("+Account.user_id()+") from sky servers")
                .setCancelable(true)

                .setPositiveButton(
                        "Yes, delete all",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startDeleteAllData();
                            }
                        })

                .setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder1.create();
        alert.show();
    }

    private void startDeleteAllData() {
        progressBar.setVisibility(View.VISIBLE);
        try {
            RequestBody user_id = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(Account.user_id()));
            RequestBody session_id = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(Account.id()));

            Retrofit.Builder builder = new Retrofit.Builder().baseUrl(SKY_SERVER_URL).addConverterFactory(GsonConverterFactory.create());
            Retrofit retrofit = builder.build();
            DeleteAccountClient client = retrofit.create(DeleteAccountClient.class);

            Call<ModelSuccess> call = client.upload(session_id, user_id);
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
                            toast("all data deleted");
                            Account.setSessionSkyVerified(false);
                            finish();
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
}