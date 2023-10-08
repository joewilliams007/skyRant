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
import android.widget.TextView;
import android.widget.Toast;

import com.dev.engineerrant.adapters.ChangelogItem;
import com.dev.engineerrant.animations.Tools;
import com.dev.engineerrant.auth.Account;
import com.dev.engineerrant.auth.MatrixAccount;
import com.dev.engineerrant.classes.dev.Changelog;
import com.dev.engineerrant.classes.dev.NotifItems;
import com.dev.engineerrant.classes.dev.NotifUnread;
import com.dev.engineerrant.classes.matrix.Chunk;
import com.dev.engineerrant.classes.matrix.Identifier;
import com.dev.engineerrant.classes.matrix.LoginData;
import com.dev.engineerrant.classes.matrix.Messages;
import com.dev.engineerrant.network.RetrofitClient;
import com.dev.engineerrant.network.methods.dev.MethodsNotif;
import com.dev.engineerrant.network.methods.matrix.MethodsChat;
import com.dev.engineerrant.network.models.dev.ModelLogin;
import com.dev.engineerrant.network.models.dev.ModelNotif;
import com.dev.engineerrant.network.models.matrix.ModelJoin;
import com.dev.engineerrant.network.models.matrix.ModelMatrixChat;
import com.dev.engineerrant.network.models.matrix.ModelMatrixLogin;
import com.dev.engineerrant.network.models.matrix.ModelToken;
import com.dev.engineerrant.network.post.matrix.JoinClient;
import com.dev.engineerrant.network.post.matrix.LoginClient;
import com.dev.engineerrant.network.post.matrix.RegisterClient;
import com.dev.engineerrant.network.post.matrix.TokenRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MatrixLoginActivity extends AppCompatActivity {

    ProgressBar progressBar;
    EditText editTextPassword, editTextUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matrix_login);
        initialize();
    }

    private void initialize() {
        progressBar = findViewById(R.id.progressBar);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextUsername = findViewById(R.id.editTextUsername);
        progressBar.setVisibility(View.GONE);
    }

    public void login(View view) {
        if (editTextUsername.getText().toString().length() < 1) {
            toast("enter username");
            return;
        }
        if (editTextPassword.getText().toString().length() < 1) {
            toast("enter password");
            return;
        }
        userLogin();
    }

    public void userLogin() {
        progressBar.setVisibility(View.VISIBLE);


        // Service
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://matrix-client.matrix.org") // SERVER IP
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        LoginClient client = retrofit.create(LoginClient.class);
        // finally, execute the request

        Call<ModelMatrixLogin> call = client.getStringScalar(
                new LoginData(
                        new Identifier("m.id.user",editTextUsername.getText().toString()),
                        "Jungle Phone",
                        editTextPassword.getText().toString(),
                        "m.login.password"
                ));
        call.enqueue(new Callback<ModelMatrixLogin>() {
            @Override
            public void onResponse(@NonNull Call<ModelMatrixLogin> call, @NonNull Response<ModelMatrixLogin> response) {
                Log.v("Upload", response+" ");

                if (response.isSuccessful()) {
                    // Do awesome stuff
                    assert response.body() != null;

                    String access_token = response.body().getAccess_token();
                    String user_id = response.body().getUser_id();

                    MatrixAccount.setAccessToken(access_token);
                    MatrixAccount.setUser_id(user_id);
                    MatrixAccount.setUsername(user_id.split(":")[0].split("@")[1]);

                    joinRoom(access_token,user_id);
                } else if (response.code() == 400) {
                    toast("Invalid login credentials entered. Please try again. :(");
                    progressBar.setVisibility(View.GONE);
                } else if (response.code() == 429) {
                    // Handle unauthorized
                    toast("You are not authorized :P");
                } else {
                    toast("Invalid login credentials entered. Please try again. :(");
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelMatrixLogin> call, @NonNull Throwable t) {
                toast("Request failed! "+t.getMessage());
                progressBar.setVisibility(View.GONE);
            }

        });
    }
    public void guestLogin(View view) {
        progressBar.setVisibility(View.VISIBLE);

        TokenRequest tokenRequest = new TokenRequest("{\"initial_device_display_name\":\"app.element.io: Firefox on Linux\"}");
        RequestBody kind = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), "guest");


        // Service
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://matrix-client.matrix.org") // SERVER IP
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RegisterClient client = retrofit.create(RegisterClient.class);
        // finally, execute the request

        Call<ModelToken> call = client.getToken(tokenRequest);
        call.enqueue(new Callback<ModelToken>() {
            @Override
            public void onResponse(@NonNull Call<ModelToken> call, @NonNull Response<ModelToken> response) {
                Log.v("Upload", response+" ");

                if (response.isSuccessful()) {
                    // Do awesome stuff
                    assert response.body() != null;

                    String access_token = response.body().getAccess_token();
                    String user_id = response.body().getUser_id();
                    toast("Token: "+access_token);
                    
                    joinRoom(access_token, user_id);
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
            public void onFailure(@NonNull Call<ModelToken> call, @NonNull Throwable t) {
                toast("Request failed! "+t.getMessage());
            }

        });
    }

    private void joinRoom(String _access_token, String _user_id) {

        Log.println(Log.DEBUG,"login matrix",_user_id+"\n"+_access_token);
        RequestBody reason = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), "guest in skyRant");
        RequestBody user_id = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), _user_id);
        RequestBody access_token = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), _access_token);

        // Service
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://matrix-client.matrix.org/") // SERVER IP
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        JoinClient client = retrofit.create(JoinClient.class);
        // finally, execute the request

        Call<ModelJoin> call = client.join(reason,user_id,access_token);
        call.enqueue(new Callback<ModelJoin>() {
            @Override
            public void onResponse(@NonNull Call<ModelJoin> call, @NonNull Response<ModelJoin> response) {
                Log.v("Upload", response+" ");

                if (response.isSuccessful()) {
                    // Do awesome stuff
                    assert response.body() != null;
                    toast("joined devRant room");
                } else if (response.code() == 400) {

                    progressBar.setVisibility(View.GONE);
                } else if (response.code() == 429) {
                    // Handle unauthorized
                    toast("You are not authorized :P");
                } else {
                    toast(response.message()+" "+response.code());
                }
                Intent intent = new Intent(MatrixLoginActivity.this, MatrixChatActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(@NonNull Call<ModelJoin> call, @NonNull Throwable t) {
                toast("Request failed! "+t.getMessage());
            }

        });

    }


    public void finish(View view) {
        finish();
    }

    public void register(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://app.element.io/#/register"));
        startActivity(browserIntent);
    }
}