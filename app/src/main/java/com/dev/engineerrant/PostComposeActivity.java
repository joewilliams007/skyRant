package com.dev.engineerrant;

import static com.dev.engineerrant.app.toast;
import static com.dev.engineerrant.network.RetrofitClient.BASE_URL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dev.engineerrant.animations.Tools;
import com.dev.engineerrant.auth.Account;
import com.dev.engineerrant.models.ModelSuccess;
import com.dev.engineerrant.post.RantClient;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PostComposeActivity extends AppCompatActivity {

    TextView textViewTop, upload;
    EditText editTextPost, editTextTags;

    String _type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_compose);

        textViewTop = findViewById(R.id.top);
        editTextPost = findViewById(R.id.editTextPost);
        editTextTags = findViewById(R.id.editTextTags);
        upload = findViewById(R.id.upload);

        Intent intent = getIntent();
        textViewTop.setText(intent.getStringExtra("typeName"));
        _type = intent.getStringExtra("type");
    }

    public void uploadPost(View view) {
        String c = editTextPost.getText().toString();
        if (c.length()<1) {
            toast("enter rant");
        } else {
            upload.setVisibility(View.GONE);
            toast("uploading ...");
            uploadC(c);
        }
    }

    private void uploadC(String c) {

        try {
            RequestBody app = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), "3");
            RequestBody rant = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), c);
            RequestBody type = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), _type);
            RequestBody tags;
            if (editTextTags.getText().toString().length()<1) {
                tags = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), "");
            } else {
                tags = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), editTextTags.getText().toString());
            }
            RequestBody token_id = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), String.valueOf(Account.id()));
            RequestBody token_key = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), Account.key());
            RequestBody user_id = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), String.valueOf(Account.user_id()));

            Retrofit.Builder builder = new Retrofit.Builder().baseUrl(BASE_URL + "devrant/").addConverterFactory(GsonConverterFactory.create());
            Retrofit retrofit = builder.build();

            RantClient client = retrofit.create(RantClient.class);
            // finally, execute the request

            Call<ModelSuccess> call = client.upload(app, rant, type, tags, token_id, token_key, user_id);
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
                            finish();
                            editTextPost.setText("");

                            /* Intent intent = new Intent(PostComposeActivity.this, RantActivity.class);
                            intent.putExtra("id",String.valueOf(response.body().getRant_id())); // open the posted rant
                            startActivity(intent); */
                        } else {
                            toast("failed. please wait until you upload a new rant");
                            // editTextPost.setText(response.message()+" "+response.toString());
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
                }

            });
        } catch (Exception e) {
            editTextPost.setText(e.toString());
        }
    }
}