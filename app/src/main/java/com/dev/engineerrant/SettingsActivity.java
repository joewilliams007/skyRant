package com.dev.engineerrant;

import static com.dev.engineerrant.app.hideKeyboard;
import static com.dev.engineerrant.app.toast;
import static com.dev.engineerrant.app.toastLong;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dev.engineerrant.animations.Tools;
import com.dev.engineerrant.auth.Account;
import com.dev.engineerrant.classes.Changelog;
import com.dev.engineerrant.network.methods.MethodsUpdate;
import com.dev.engineerrant.network.models.ModelUpdate;
import com.dev.engineerrant.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity {

    TextView textViewLogin,textViewCurrentNr,textViewNewestNr;
    ConstraintLayout theme, profile, update, features, feed, about, notif, github;
    ProgressBar progressBar;
    EditText editTextRantsAmount, editTextSearchText, editTextKey;
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
        feed = findViewById(R.id.feed);
        about = findViewById(R.id.about);
        github = findViewById(R.id.github);
        notif = findViewById(R.id.notif);
        textViewCurrentNr = findViewById(R.id.textViewCurrentNr);
        textViewNewestNr = findViewById(R.id.textViewNewestNr);
        progressBar = findViewById(R.id.progressBar);
        editTextRantsAmount = findViewById(R.id.editTextRantsAmount);
        editTextSearchText = findViewById(R.id.editTextSearchText);
        editTextKey = findViewById(R.id.editTextKey);
        setSwitches();

        if (Account.isLoggedIn()) {
            textViewLogin.setText(R.string.logout);
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

    public void themeCoffee(View view) {
        Account.setTheme("coffee");
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

    public void themeGreen(View view) {
        Account.setTheme("green");
        Intent intent;
        Tools.setTheme(SettingsActivity.this);
        intent = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void themeDiscord(View view) {
        Account.setTheme("discord");
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

    public void switchSurprise(View view) {
        Account.setSurprise(!Account.surprise());
        setSwitches();
    }


    public void switchUsername(View view) {
        Account.setFeedUsername(!Account.isFeedUsername());
        setSwitches();
    }

    private void setSwitches() {
        SwitchCompat switchAuto = findViewById(R.id.switchLoad);
        SwitchCompat switchInfo = findViewById(R.id.switchInfoOnFeed);
        SwitchCompat switchHighlight = findViewById(R.id.switchHighlight);
        SwitchCompat switchSurprise = findViewById(R.id.switchSurprise);
        SwitchCompat switchAnimation = findViewById(R.id.switchAnimation);
        SwitchCompat switchUsername = findViewById(R.id.switchUsername);
        switchUsername.setChecked(Account.isFeedUsername());
        switchSurprise.setChecked(Account.surprise());
        switchAnimation.setChecked(Account.animate());
        switchHighlight.setChecked(Account.highlighter());
        switchAuto.setChecked(Account.autoLoad());
        switchInfo.setChecked(Account.userInfo());

        SwitchCompat switchNotif = findViewById(R.id.switchNotifications);
        switchNotif.setChecked(Account.isPushNotif());

        SwitchCompat switchNotifCommentVote = findViewById(R.id.switchNotifCommentVote);
        switchNotifCommentVote.setChecked(Account.isPushNotifCommentVote());

        SwitchCompat switchNotifMention = findViewById(R.id.switchNotifMention);
        switchNotifMention.setChecked(Account.isPushNotifMention());

        SwitchCompat switchNotifComment = findViewById(R.id.switchNotifComment);
        switchNotifComment.setChecked(Account.isPushNotifComment());

        SwitchCompat switchNotifRantVote = findViewById(R.id.switchNotifRantVote);
        switchNotifRantVote.setChecked(Account.isPushNotifRantVote());

        SwitchCompat switchNotifCommentDiscuss = findViewById(R.id.switchNotifCommentDiscuss);
        switchNotifCommentDiscuss.setChecked(Account.isPushNotifCommentDiscuss());

        SwitchCompat switchNotifSub = findViewById(R.id.switchNotifSub);
        switchNotifSub.setChecked(Account.isPushNotifSub());
    }


    public void showTheme(View view) {
        if (theme.getVisibility() == View.GONE) {
            editTextSearchText.setText(Account.search());
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

    public void showAbout(View view) {
        if (about.getVisibility() == View.GONE) {
            about.setVisibility(View.VISIBLE);
        } else {
            about.setVisibility(View.GONE);
        }
    }

    public void showGithub(View view) {
        if (github.getVisibility() == View.GONE) {
            github.setVisibility(View.VISIBLE);
        } else {
            github.setVisibility(View.GONE);
        }
    }

    public void showFeed(View view) {
        if (feed.getVisibility() == View.GONE) {
            editTextRantsAmount.setText(String.valueOf(Account.limit()));
            feed.setVisibility(View.VISIBLE);
        } else {
            feed.setVisibility(View.GONE);
        }
    }

    public void showNotif(View view) {
        if (notif.getVisibility() == View.GONE) {
            notif.setVisibility(View.VISIBLE);
        } else {
            notif.setVisibility(View.GONE);
        }
    }

    public void saveSearch(View view) {
        String t = editTextSearchText.getText().toString();
        if (t.length()>1) {
            Account.setSearch(t);
            theme.setVisibility(View.GONE);
            hideKeyboard(SettingsActivity.this);
        } else {
            toast("enter a term");
        }
    }

    public void saveGithubKey(View view) {
        String t = editTextKey.getText().toString();
        if (t.length()>1) {
            Account.setGithubKey(t);
            github.setVisibility(View.GONE);
            hideKeyboard(SettingsActivity.this);
            editTextKey.setText("");
        } else {
            toast("enter a valid key");
        }
    }

    public void profileOptions(View view) {
        if (Account.isLoggedIn()) {
            Intent intent = new Intent(SettingsActivity.this, EditProfileActivity.class);
            intent.putExtra("user_id",String.valueOf(Account.user_id()));
            startActivity(intent);
        } else {
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    public void updateLog(View view) {
        Intent intent = new Intent(SettingsActivity.this, ChangelogActivity.class);
        startActivity(intent);
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
                    List<Changelog> logs = response.body().getChangelog();

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
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://github.com/joewilliams007/skyRant/blob/master/README.md"));
        startActivity(browserIntent);
        // String url = "https://github.com/Piashsarker/AndroidAppUpdateLibrary/raw/master/app-debug.apk";
    }

    public void saveRantsAmount(View view) {
        int amount = Integer.parseInt(editTextRantsAmount.getText().toString());
        if (amount < 10 || amount > 50) {
            toast("min 10 max 50 ");
        } else {
            Account.setLimit(amount);
            hideKeyboard(SettingsActivity.this);
            feed.setVisibility(View.GONE);
        }
    }


    public void openCommunity(View view) {
        Intent intent = new Intent(SettingsActivity.this, CommunityActivity.class);
        startActivity(intent);
    }

    public void openSupporters(View view) {
        Intent intent = new Intent(SettingsActivity.this, SupporterActivity.class);
        startActivity(intent);
    }

    public void openFollowing(View view) {
        Intent intent = new Intent(SettingsActivity.this, FollowingActivity.class);
        startActivity(intent);
    }

    public void githubSkyRant(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/joewilliams007/skyRant"));
        startActivity(browserIntent);
    }


    public void generateKey(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://github.com/settings/tokens"));
        startActivity(browserIntent);
    }

    public void skyRantIssueTracker(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://github.com/joewilliams007/skyRant/issues"));
        startActivity(browserIntent);
    }

    public void addCommunityProject(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://github.com/joewilliams007/jsonapi/issues"));
        startActivity(browserIntent);
    }

    public void devrantWebsite(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://devrant.com/"));
        startActivity(browserIntent);
    }

    public void watchRantLink(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.dev.watchrant"));
        startActivity(browserIntent);
    }

    public void avatar(View view) {
        if (Account.isLoggedIn()) {
            Intent intent = new Intent(SettingsActivity.this, BuilderActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    public void block(View view) {
        Intent intent = new Intent(SettingsActivity.this, BlockActivity.class);
        startActivity(intent);
    }

    public void appNotifSettings(View view) {
        Intent intent = new Intent();
        Context context = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
        } else {
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
        }
        context.startActivity(intent);
    }

    public void appInfo(View view) {
        String packageName = this.getPackageName();
        try {

            Intent intent = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                intent = new Intent(Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS);
            } else {
                intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            }
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


    public void removeKey(View view) {
        Account.setGithubKey(null);
        github.setVisibility(View.GONE);
        toast("removed key");
    }


    public void langEn(View view) {
        Account.setLanguage(null);
        Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }

    public void langDe(View view) {
        Account.setLanguage("de");
        Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }

    public void switchNotif(View view) {
        Account.setPushNotif(!Account.isPushNotif());
        Account.setPushNotifComment(Account.isPushNotif());
        Account.setPushNotifSub(Account.isPushNotif());
        Account.setPushNotifRantVote(Account.isPushNotif());
        Account.setPushNotifCommentDiscuss(Account.isPushNotif());
        Account.setPushNotifCommentVote(Account.isPushNotif());
        Account.setPushNotifMention(Account.isPushNotif());
        setSwitches();
    }

    public void switchNotifCommentVote(View view) {
        Account.setPushNotifCommentVote(!Account.isPushNotifCommentVote());
        Account.setPushNotif(true);
        setSwitches();
    }

    public void switchNotifMention(View view) {
        Account.setPushNotifMention(!Account.isPushNotifMention());
        Account.setPushNotif(true);
        setSwitches();
    }

    public void switchNotifComment(View view) {
        Account.setPushNotifComment(!Account.isPushNotifComment());
        Account.setPushNotif(true);
        setSwitches();
    }

    public void switchNotifRantVote(View view) {
        Account.setPushNotifRantVote(!Account.isPushNotifRantVote());
        Account.setPushNotif(true);
        setSwitches();
    }

    public void switchNotifCommentDiscuss(View view) {
        Account.setPushNotifCommentDiscuss(!Account.isPushNotifCommentDiscuss());
        Account.setPushNotif(true);
        setSwitches();
    }

    public void switchNotifSub(View view) {
        Account.setPushNotifSub(!Account.isPushNotifSub());
        Account.setPushNotif(true);
        setSwitches();
    }


}