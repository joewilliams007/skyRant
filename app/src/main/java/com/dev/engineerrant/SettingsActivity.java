package com.dev.engineerrant;

import static com.dev.engineerrant.app.toast;
import static com.dev.engineerrant.app.toastLong;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.dev.engineerrant.animations.Tools;
import com.dev.engineerrant.auth.Account;
import com.dev.engineerrant.methods.MethodsUpdate;
import com.dev.engineerrant.models.ModelUpdate;
import com.dev.engineerrant.network.RetrofitClient;

import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity {

    TextView textViewLogin,textViewCurrentNr,textViewNewestNr;
    ConstraintLayout theme, profile, update, features;
    ProgressBar progressBar;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        textViewLogin = findViewById(R.id.textViewLogin);

        theme = findViewById(R.id.theme);
        profile = findViewById(R.id.profile);
        update = findViewById(R.id.update);
        features = findViewById(R.id.features);
        textViewCurrentNr = findViewById(R.id.textViewCurrentNr);
        textViewNewestNr = findViewById(R.id.textViewNewestNr);
        progressBar = findViewById(R.id.progressBar);

        setSwitches();

        if (Account.isLoggedIn()) {
            textViewLogin.setText("l o g o u t");
        }
    }

    public void login(View view) {
        if (Account.isLoggedIn()) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this)
                    .setTitle("logout")
                    .setMessage("U sure u want to logout :(")
                    .setCancelable(true)

                    .setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    Account.setKey(null);
                                    Account.setExpire_time(0);
                                    Account.setUser_id(0);
                                    Account.setId(0);

                                    Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
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
        } else {
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            startActivity(intent);
        }

    }

    public void themeLight(View view) {
        Account.setTheme("light");
        Intent intent;
        Tools.setTheme(SettingsActivity.this);
        intent = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void themeAmoled(View view) {
        Account.setTheme("amoled");
        Intent intent;
        Tools.setTheme(SettingsActivity.this);
        intent = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void themeDark(View view) {
        Account.setTheme("dark");
        Intent intent;
        Tools.setTheme(SettingsActivity.this);
        intent = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void switchAutoLoad(View view) {
        Account.setAutoLoad(!Account.autoLoad());
        setSwitches();
    }
    public void switchUserInfoFeed(View view) {
        Account.setUserInfo(!Account.userInfo());
        setSwitches();
    }

    public void switchHighlight(View view) {
        Account.setHighlighter(!Account.highlighter());
        setSwitches();
    }

    public void switchAnimation(View view) {
        Account.setAnimate(!Account.animate());
        setSwitches();
    }

    private void setSwitches() {
        SwitchCompat switchAuto = findViewById(R.id.switchLoad);
        SwitchCompat switchInfo = findViewById(R.id.switchInfoOnFeed);
        SwitchCompat switchHighlight = findViewById(R.id.switchHighlight);
        SwitchCompat switchAnimation = findViewById(R.id.switchAnimation);
        switchAnimation.setChecked(Account.animate());
        switchHighlight.setChecked(Account.highlighter());
        switchAuto.setChecked(Account.autoLoad());
        switchInfo.setChecked(Account.userInfo());
    }


    public void showTheme(View view) {
        if (theme.getVisibility() == View.GONE) {
            theme.setVisibility(View.VISIBLE);
        } else {
            theme.setVisibility(View.GONE);
        }
    }

    public void showProfile(View view) {
        if (profile.getVisibility() == View.GONE) {
            profile.setVisibility(View.VISIBLE);
        } else {
            profile.setVisibility(View.GONE);
        }
    }

    public void showUpdate(View view) {
        if (update.getVisibility() == View.GONE) {
            update.setVisibility(View.VISIBLE);

            int versionCode = BuildConfig.VERSION_CODE;
            String versionName = BuildConfig.VERSION_NAME;

            textViewCurrentNr.setText(versionName);

            checkUpdate();

        } else {
            update.setVisibility(View.GONE);
        }
    }

    public void showFeatures(View view) {
        if (features.getVisibility() == View.GONE) {
            features.setVisibility(View.VISIBLE);
        } else {
            features.setVisibility(View.GONE);
        }
    }

    public void profileOptions(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://devrant.com/"));
        startActivity(browserIntent);
    }

    public void appInfo(View view) {
        String packageName = "com.dev.engineerrant";
        try {
            //Open the specific App Info page:
            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + packageName));
            startActivity(intent);

        } catch ( ActivityNotFoundException e ) {
            //e.printStackTrace();

            //Open the generic Apps page:
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
            startActivity(intent);

        }
        toastLong("enable all:\nSetAsDefault/SupportedAddresses");
    }

    private void checkUpdate() {
        MethodsUpdate methods = RetrofitClient.getRetrofitInstance().create(MethodsUpdate.class);
        String total_url = "https://raw.githubusercontent.com/joewilliams007/jsonapi/gh-pages/phoneclient.json";

        progressBar.setVisibility(View.VISIBLE);

        Call<ModelUpdate> call = methods.getAllData(total_url);
        call.enqueue(new Callback<ModelUpdate>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ModelUpdate> call, @NonNull Response<ModelUpdate> response) {
                if (response.isSuccessful()) {

                    // Do  awesome stuff
                    assert response.body() != null;

                    String version = response.body().getVersion();
                    int build = response.body().getBuild();
                    int versionCode = BuildConfig.VERSION_CODE;

                    textViewNewestNr.setText(version);

                    if (versionCode < build) {
                        // toast("Update available: Version "+version);
                    } else {
                        // toast("You have the latest version!");
                    }

                } else if (response.code() == 429) {
                    // Handle unauthorized
                    toast("error contacting github error 429");
                } else {
                    toast("error contacting github");
                }
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NonNull Call<ModelUpdate> call, @NonNull Throwable t) {
                Log.d("error_contact", t.toString());
                toast("no network");
            }
        });

    }

    public void updateOpen(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/joewilliams007/skyRant/blob/master/README.md"));
        startActivity(browserIntent);
        // String url = "https://github.com/Piashsarker/AndroidAppUpdateLibrary/raw/master/app-debug.apk";
    }
}