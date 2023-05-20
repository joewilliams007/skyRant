package com.dev.engineerrant;

import static android.text.format.DateUtils.getRelativeTimeSpanString;
import static com.dev.engineerrant.app.toast;
import static com.dev.engineerrant.network.RetrofitClient.BASE_URL;
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
import com.dev.engineerrant.network.RetrofitClient;
import com.dev.engineerrant.network.methods.sky.MethodsSkyProfile;
import com.dev.engineerrant.network.models.sky.ModelSkyProfile;
import com.dev.engineerrant.network.models.sky.ModelSuccess;
import com.dev.engineerrant.network.post.RantClient;
import com.dev.engineerrant.network.post.sky.BackupClient;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BackupActivity extends AppCompatActivity {

    ProgressBar progressBar;
    SkyProfile profile;
    TextView textViewRestore, textViewBackup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);
        progressBar = findViewById(R.id.progressBar);
        textViewRestore = findViewById(R.id.textViewRestore);
        textViewBackup = findViewById(R.id.textViewBackup);
        getProfile();
    }

    public void restore(View view) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this)
                .setTitle("Restore")
                .setMessage("Restore your data (following, blocked users, blocked words) from sky servers")
                .setCancelable(true)

                .setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getProfile();
                                if (profile!=null) {
                                    if (profile.getFollowing()!=null || profile.getBlocked_words()!=null || profile.getBlocked_users()!=null) {
                                        Account.setFollowing(profile.getFollowing());
                                        Account.setBlockedUsers(profile.getBlocked_users());
                                        Account.setBlockedWords(profile.getBlocked_words());
                                        toast("restored to backup");
                                    } else {
                                        toast("no data to restore");
                                    }
                                }
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
    private void getProfile() {
        progressBar.setVisibility(View.VISIBLE);
        MethodsSkyProfile methods = RetrofitClient.getRetrofitInstance().create(MethodsSkyProfile.class);
        String total_url = SKY_SERVER_URL+"my_profile/"+ Account.user_id()+"/"+Account.id();

        Call<ModelSkyProfile> call = methods.getAllData(total_url);

        call.enqueue(new Callback<ModelSkyProfile>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ModelSkyProfile> call, @NonNull Response<ModelSkyProfile> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    profile = response.body().getProfile();

                    String restore_data = profile.getFollowing()+profile.getBlocked_users()+profile.getBlocked_words();
                    byte[] restore_b = restore_data.getBytes(StandardCharsets.UTF_8);
                    if (profile.getFollowing()!=null || profile.getBlocked_words()!=null || profile.getBlocked_users()!=null) {
                        textViewRestore.setText("restore "+restore_b.length+" bytes");
                    } else {
                        textViewRestore.setText("no data to restore");
                    }

                    String backup_data = Account.following()+Account.blockedUsers()+Account.blockedWords();
                    byte[] backup_b = backup_data.getBytes(StandardCharsets.UTF_8);
                    if (Account.following()!=null || Account.blockedWords()!=null || Account.blockedUsers()!=null) {
                        textViewBackup.setText("backup "+backup_b.length+" bytes");
                    } else {
                        textViewBackup.setText("no data to backup");
                    }
                    progressBar.setVisibility(View.GONE);
                } else if (response.code() == 429) {
                    // Handle unauthorized
                    toast("you are not authorized");
                } else {
                    toast("no success "+response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelSkyProfile> call, @NonNull Throwable t) {
                Log.d("error_contact", t.toString());
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void backup(View view) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this)
                .setTitle("Backup")
                .setMessage("Backup your data (following, blocked users, blocked words) to sky servers")
                .setCancelable(true)

                .setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startBackup();
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

    private void startBackup() {
        progressBar.setVisibility(View.VISIBLE);
        try {
            RequestBody user_id = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(Account.user_id()));
            RequestBody session_id = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(Account.id()));
            RequestBody following = RequestBody.create(MediaType.parse("multipart/form-data"), Account.following());
            RequestBody blocked_words = RequestBody.create(MediaType.parse("multipart/form-data"), Account.blockedWords());
            RequestBody blocked_users = RequestBody.create(MediaType.parse("multipart/form-data"), Account.blockedUsers());

            Retrofit.Builder builder = new Retrofit.Builder().baseUrl(SKY_SERVER_URL).addConverterFactory(GsonConverterFactory.create());
            Retrofit retrofit = builder.build();
            BackupClient client = retrofit.create(BackupClient.class);

            Call<ModelSuccess> call = client.upload(session_id, user_id,following,blocked_words,blocked_users);
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
                            toast("data backed up");
                            getProfile();
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

    public void finish(View view) {
        finish();
    }
}