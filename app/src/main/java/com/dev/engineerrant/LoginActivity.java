package com.dev.engineerrant;

import static com.dev.engineerrant.app.toast;
import static com.dev.engineerrant.network.RetrofitClient.BASE_URL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.dev.engineerrant.animations.Tools;
import com.dev.engineerrant.auth.Account;
import com.dev.engineerrant.models.ModelLogin;
import com.dev.engineerrant.post.LoginClient;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    ProgressBar progressBar;
    EditText editTextPassword, editTextUsername;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar = findViewById(R.id.progressBar);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextUsername = findViewById(R.id.editTextUsername);
        progressBar.setVisibility(View.GONE);
    }

    public void login(View view) {
        if (editTextUsername.getText().toString().length()<1) {
            toast("enter username");
            return;
        }
        if (editTextPassword.getText().toString().length()<1) {
            toast("enter password");
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        RequestBody app = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), "3");
        RequestBody username = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), editTextUsername.getText().toString());
        RequestBody password = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), editTextPassword.getText().toString());

        // Service
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(BASE_URL) // SERVER IP
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        LoginClient client = retrofit.create(LoginClient.class);
        // finally, execute the request

        Call<ModelLogin> call = client.login(app,username,password);
        call.enqueue(new Callback<ModelLogin>() {
            @Override
            public void onResponse(@NonNull Call<ModelLogin> call, @NonNull Response<ModelLogin> response) {
                Log.v("Upload", response+" ");

                if (response.isSuccessful()) {
                    // Do awesome stuff
                    assert response.body() != null;

                    String key = response.body().getAuth_token().getKey();
                    int id = response.body().getAuth_token().getId();
                    int user_id = response.body().getAuth_token().getUser_id();
                    int expire_time = response.body().getAuth_token().getExpire_time();

                    Account.setKey(key);
                    Account.setId(id);
                    Account.setUser_id(user_id);
                    Account.setExpire_time(expire_time);
                    Account.setUsername(editTextUsername.getText().toString());

                    toast("login approved");

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else if (response.code() == 400) {
                    toast("Invalid login credentials entered. Please try again. :(");
                    progressBar.setVisibility(View.GONE);
                } else if (response.code() == 429) {
                    // Handle unauthorized
                    toast("You are not authorized :P");
                } else {
                    toast(response.message()+response.body().toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelLogin> call, @NonNull Throwable t) {
                toast("Request failed! "+t.getMessage());
            }

        });
    }

    public void register(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://devrant.com/feed/top/month?signup=1"));
        startActivity(browserIntent);
    }
}