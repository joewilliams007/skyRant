package com.dev.engineerrant;

import static android.text.format.DateUtils.getRelativeTimeSpanString;
import static com.dev.engineerrant.app.toast;
import static com.dev.engineerrant.network.RetrofitClient.BASE_URL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dev.engineerrant.animations.Tools;
import com.dev.engineerrant.auth.Account;
import com.dev.engineerrant.network.methods.dev.MethodsProfile;
import com.dev.engineerrant.network.models.dev.ModelProfile;
import com.dev.engineerrant.network.models.sky.ModelSuccess;
import com.dev.engineerrant.network.RetrofitClient;
import com.dev.engineerrant.network.post.AccountClient;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditProfileActivity extends AppCompatActivity {

    ConstraintLayout details;
    ProgressBar progressBar, progressBarBig;
    TextView account, textViewSave;
    EditText editTextAbout, editTextSkills, editTextWebsite, editTextGithub, editTextLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        initialize();
        setDetails();
    }

    private void setDetails() {
        MethodsProfile methods = RetrofitClient.getRetrofitInstance().create(MethodsProfile.class);
        String total_url = BASE_URL + "users/"+Account.user_id()+"?app=3";

        Call<ModelProfile> call = methods.getAllData(total_url);
        call.enqueue(new Callback<ModelProfile>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ModelProfile> call, @NonNull Response<ModelProfile> response) {
                if (response.isSuccessful()) {
                    // Do awesome stuff
                    assert response.body() != null;

                    String about = response.body().getProfile().getAbout();
                    String location = response.body().getProfile().getLocation();
                    String skills = response.body().getProfile().getSkills();
                    String github = response.body().getProfile().getGithub();
                    String website = response.body().getProfile().getWebsite();

                    editTextAbout.setText(about);
                    editTextLocation.setText(location);
                    editTextSkills.setText(skills);
                    editTextGithub.setText(github);
                    editTextWebsite.setText(website);

                    progressBarBig.setVisibility(View.GONE);

                } else if (response.code() == 429) {
                    // Handle unauthorized
                } else {
                    toast(response.message());
                }

            }

            @Override
            public void onFailure(@NonNull Call<ModelProfile> call, @NonNull Throwable t) {
                Log.d("error_contact", t.toString());
                toast(t.toString());
            }
        });
    }

    private void initialize() {
        details = findViewById(R.id.details);
        editTextAbout = findViewById(R.id.editTextAbout);
        editTextGithub = findViewById(R.id.editTextGithub);
        editTextLocation = findViewById(R.id.editTextLocation);
        editTextSkills = findViewById(R.id.editTextSkills);
        editTextWebsite = findViewById(R.id.editTextWebsite);
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

    public void saveAccountBtn(View view) {
        textViewSave.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        sendEdit(editTextAbout.getText().toString(),editTextSkills.getText().toString(),editTextLocation.getText().toString(),editTextWebsite.getText().toString(),editTextGithub.getText().toString());
    }

    private void sendEdit(String about, String skills, String location, String website, String github) {

        try {
            RequestBody app = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), "3");
            RequestBody token_id = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), String.valueOf(Account.id()));
            RequestBody token_key = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), Account.key());
            RequestBody user_id = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), String.valueOf(Account.user_id()));

            RequestBody profile_about = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), about);
            RequestBody profile_skills = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), skills);
            RequestBody profile_location = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), location);
            RequestBody profile_website = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), website);
            RequestBody profile_github = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), github);

            Retrofit.Builder builder = new Retrofit.Builder().baseUrl(BASE_URL + "users/me/").addConverterFactory(GsonConverterFactory.create());
            Retrofit retrofit = builder.build();

            AccountClient client = retrofit.create(AccountClient.class);
            // finally, execute the request

            Call<ModelSuccess> call = client.edit(app, token_id, token_key, user_id,profile_about,profile_skills,profile_location,profile_website,profile_github);
            call.enqueue(new Callback<ModelSuccess>() {
                @Override
                public void onResponse(@NonNull Call<ModelSuccess> call, @NonNull Response<ModelSuccess> response) {
                    Log.v("Upload", response + " ");

                    if (response.isSuccessful()) {
                        // Do awesome stuff
                        assert response.body() != null;
                        Boolean success = response.body().getSuccess();

                        if (success) {
                            Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                            intent.putExtra("user_id",String.valueOf(Account.user_id()));
                            startActivity(intent);
                            finish();
                        } else {
                            toast("failed");
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

                    textViewSave.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }

            });
        } catch (Exception e) {
            toast(e.toString());

            textViewSave.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }
}