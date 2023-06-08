package com.dev.engineerrant;

import static android.text.format.DateUtils.getRelativeTimeSpanString;
import static com.dev.engineerrant.app.toast;
import static com.dev.engineerrant.network.RetrofitClient.BASE_URL;
import static com.dev.engineerrant.network.RetrofitClient.SKY_SERVER_URL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dev.engineerrant.animations.Tools;
import com.dev.engineerrant.auth.Account;
import com.dev.engineerrant.network.RetrofitClient;
import com.dev.engineerrant.network.methods.git.MethodsVerifyGithub;
import com.dev.engineerrant.network.methods.sky.MethodsVerifySkyKey;
import com.dev.engineerrant.network.models.sky.ModelSuccess;
import com.dev.engineerrant.network.models.git.ModelVerifyGithub;
import com.dev.engineerrant.network.models.sky.ModelVerifySkyKey;
import com.dev.engineerrant.network.post.CommentClient;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SkyLoginActivity extends AppCompatActivity {
    TextView textViewLogin,textViewSign;
    ProgressBar progressBar;
    CheckBox checkboxSign;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sky_login);
        textViewLogin = findViewById(R.id.textViewLogin);
        textViewSign = findViewById(R.id.textViewSign);
        progressBar = findViewById(R.id.progressBar);
        checkboxSign = findViewById(R.id.checkboxSign);
        progressBar.setVisibility(View.GONE);
    }

    public void readMore(View view) {
        if (textViewLogin.getVisibility()==View.VISIBLE) {
            textViewLogin.setVisibility(View.GONE);
            textViewSign.setText("third-party servers are accessed [read more]");
        } else {
            textViewLogin.setVisibility(View.VISIBLE);
            textViewSign.setText("third-party servers are accessed [read less]");
        }
    }

    public void verify(View view) {
        if (checkboxSign.isChecked()) {
            progressBar.setVisibility(View.VISIBLE);
            getGithubServerInfo();
        } else {
            toast("please confirm the following action");
        }
    }

    private void getGithubServerInfo() {
        String total_url = "https://raw.githubusercontent.com/joewilliams007/jsonapi/gh-pages/skyserver.json";
        MethodsVerifyGithub methods = RetrofitClient.getRetrofitInstance().create(MethodsVerifyGithub.class);
        String header = null;
        if (Account.githubKey()!=null) {
            header = "token "+Account.githubKey();
        }
        Call<ModelVerifyGithub> call = methods.getAllData(header,total_url);
        call.enqueue(new Callback<ModelVerifyGithub>() {
            @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
            @Override
            public void onResponse(@NonNull Call<ModelVerifyGithub> call, @NonNull Response<ModelVerifyGithub> response) {
                if (response.isSuccessful()) {

                    // Do  awesome stuff
                    assert response.body() != null;

                   String rant_id = response.body().getRant_id();
                   String notice = response.body().getNotice();
                   Boolean active = response.body().getActive();

                   if (active) {
                       getSkyVerifyKey();
                   } else {
                       message(notice);
                   }


                } else if (response.code() == 429) {
                    // Handle unauthorized
                    toast("error contacting github error 429");
                } else {

                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelVerifyGithub> call, @NonNull Throwable t) {
                Log.d("error_contact", t.toString());
                toast("no network github");
                getSkyVerifyKey();
            }
        });

    }

    private void getSkyVerifyKey() {
        try {
            String total_url = SKY_SERVER_URL+"verify_key/"+Account.user_id()+"/"+Account.id();
            MethodsVerifySkyKey methods = RetrofitClient.getRetrofitInstance().create(MethodsVerifySkyKey.class);

            Call<ModelVerifySkyKey> call = methods.getAllData(total_url);
            call.enqueue(new Callback<ModelVerifySkyKey>() {
                @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
                @Override
                public void onResponse(@NonNull Call<ModelVerifySkyKey> call, @NonNull Response<ModelVerifySkyKey> response) {
                    if (response.isSuccessful()) {

                        // Do  awesome stuff
                        assert response.body() != null;

                        Boolean success = response.body().getSuccess();
                        Boolean error = response.body().getError();
                        String verify_key = response.body().getVerify_key();
                        String message = response.body().getMessage();
                        String verify_post_id = response.body().getVerify_post_id();

                        if (error || !success) {
                            message(message);
                        } else {
                           uploadC(verify_post_id,verify_key);
                        }


                    } else if (response.code() == 429) {
                        // Handle unauthorized
                        toast("error contacting github error 429");
                    } else {

                    }
                }

                @Override
                public void onFailure(@NonNull Call<ModelVerifySkyKey> call, @NonNull Throwable t) {
                    Log.d("error_contact", t.toString());
                    toast("no network");
                    message(t.toString());
                }
            });
        } catch (Exception e) {
            toast("error connecting to sky. please retry");
            message(e.toString());
        }
    }

    private void message(String message) {
        textViewLogin.setText(message);
        textViewLogin.setVisibility(View.VISIBLE);
    }

    private void uploadC(String rant_id, String c) {

        try {
            RequestBody app = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), "3");
            RequestBody comment = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), c);
            RequestBody token_id = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), String.valueOf(Account.id()));
            RequestBody token_key = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), Account.key());
            RequestBody user_id = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), String.valueOf(Account.user_id()));

            Retrofit.Builder builder = new Retrofit.Builder().baseUrl(BASE_URL + "devrant/rants/" + rant_id + "/").addConverterFactory(GsonConverterFactory.create());
            Retrofit retrofit = builder.build();

            CommentClient client = retrofit.create(CommentClient.class);
            // finally, execute the request

            Call<ModelSuccess> call = client.upload(app, comment, token_id, token_key, user_id);
            call.enqueue(new Callback<ModelSuccess>() {
                @Override
                public void onResponse(@NonNull Call<ModelSuccess> call, @NonNull Response<ModelSuccess> response) {
                    Log.v("Upload", response + " ");

                    if (response.isSuccessful()) {
                        // Do awesome stuff
                        assert response.body() != null;
                        Boolean success = response.body().getSuccess();

                        if (success) {
                            askSkyToVerifyComment();
                        } else {
                            toast("failed");
                            message("failed to upload comment to devRant");
                        }

                    } else if (response.code() == 400) {
                        toast("Invalid login credentials entered. Please try again. :(");
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
                    message(t.toString());
                }

            });
        } catch (Exception e) {
            toast(e.toString());
        }
    }

    private void askSkyToVerifyComment() {
        try {
            String total_url = SKY_SERVER_URL+"verify_comment/"+Account.user_id()+"/"+Account.id();
            MethodsVerifySkyKey methods = RetrofitClient.getRetrofitInstance().create(MethodsVerifySkyKey.class);

            Call<ModelVerifySkyKey> call = methods.getAllData(total_url);
            call.enqueue(new Callback<ModelVerifySkyKey>() {
                @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
                @Override
                public void onResponse(@NonNull Call<ModelVerifySkyKey> call, @NonNull Response<ModelVerifySkyKey> response) {
                    if (response.isSuccessful()) {

                        // Do  awesome stuff
                        assert response.body() != null;

                        Boolean success = response.body().getSuccess();
                        Boolean error = response.body().getError();
                        String message = response.body().getMessage();

                        if (error || !success) {
                            message(message);
                        } else {
                            message("success! session has been verified. you can now chat, react as a verified user\n\n"+message);
                            toast("verified session");
                            Account.setSessionSkyVerified(true);

                            Intent intent = new Intent(SkyLoginActivity.this, SkyActivity.class);
                            startActivity(intent);
                            finish();
                        }


                    } else if (response.code() == 429) {
                        // Handle unauthorized
                        toast("error contacting github error 429");
                    } else {

                    }
                }

                @Override
                public void onFailure(@NonNull Call<ModelVerifySkyKey> call, @NonNull Throwable t) {
                    Log.d("error_contact", t.toString());
                    toast("no network");
                    message(t.toString());
                }
            });
        } catch (Exception e) {
            toast("error connecting to sky. please retry");
            message(e.toString());
        }
    }

    public void finish(View view) {
        finish();
    }

    public void appCode(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/joewilliams007/skyRant"));
        startActivity(browserIntent);
    }

    public void severCode(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/joewilliams007/skyAPI"));
        startActivity(browserIntent);
    }
}