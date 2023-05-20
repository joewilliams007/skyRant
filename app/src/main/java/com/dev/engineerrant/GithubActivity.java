package com.dev.engineerrant;

import static android.text.format.DateUtils.getRelativeTimeSpanString;
import static com.dev.engineerrant.app.toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dev.engineerrant.animations.Tools;
import com.dev.engineerrant.auth.Account;
import com.dev.engineerrant.auth.MyApplication;
import com.dev.engineerrant.network.methods.git.MethodsGithub;
import com.dev.engineerrant.network.models.git.ModelGithub;
import com.dev.engineerrant.network.RetrofitClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GithubActivity extends AppCompatActivity {
    RecyclerView recyclerView, link_view;
    ImageView imageViewProfile;
    TextView textViewUsername, top, textViewFollowers, textViewFollowing, textViewBio, textViewBlog, textViewCreated, textViewLocation, textViewGithub, textViewRepos;
    ProgressBar progressBar;
    Intent intent;
    String ownerGithub, image;
    String blog = null;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Tools.setTheme(this);
        setContentView(R.layout.activity_github);
        recyclerView = findViewById(R.id.repo_view);
        progressBar = findViewById(R.id.progressBar);
        textViewUsername = findViewById(R.id.textViewUsername);
        imageViewProfile = findViewById(R.id.imageViewProfile);
        link_view = findViewById(R.id.link_view);
        top = findViewById(R.id.top);
        textViewBio = findViewById(R.id.textViewBio);
        textViewBlog = findViewById(R.id.textViewBlog);
        textViewCreated = findViewById(R.id.textViewCreated);
        textViewFollowers = findViewById(R.id.textViewFollowers);
        textViewFollowing = findViewById(R.id.textViewFollowing);
        textViewLocation = findViewById(R.id.textViewLocation);
        textViewGithub = findViewById(R.id.textViewGithub);
        textViewRepos = findViewById(R.id.textViewRepos);
        getProfile();
    }

    private void getProfile() {
        intent = getIntent();
        String total_url;
        textViewGithub.setText(intent.getStringExtra("github_url"));
        total_url = "https://api.github.com/users/"+intent.getStringExtra("github_url").split("com/")[1];

        if (total_url.endsWith("/")) {
            total_url = total_url.substring(0, total_url.length() - 1);
        }

        MethodsGithub methods = RetrofitClient.getRetrofitInstance().create(MethodsGithub.class);

        progressBar.setVisibility(View.VISIBLE);

        String header = null;
        if (Account.githubKey()!=null) {
            header = "token "+Account.githubKey();
        }

        Call<ModelGithub> call = methods.getAllData(header,total_url);
        call.enqueue(new Callback<ModelGithub>() {
            @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
            @Override
            public void onResponse(@NonNull Call<ModelGithub> call, @NonNull Response<ModelGithub> response) {
                if (response.isSuccessful()) {

                    // Do  awesome stuff
                    assert response.body() != null;

                    ownerGithub = response.body().getHtml_url();
                    imageViewProfile.setImageDrawable(null);
                    top.setText(response.body().getLogin());
                    textViewUsername.setText(response.body().getName());

                    textViewFollowers.setText(response.body().getFollowers()+" followers");
                    textViewFollowing.setText(response.body().getFollowing()+" following");
                    textViewRepos.setText(response.body().getPublic_repos()+" repositories");

                    if (response.body().getBio()!=null && !response.body().getBio().equals("")) {
                        textViewBio.setText(response.body().getBio());
                    } else {
                        textViewBio.setVisibility(View.GONE);
                    }
                    if (response.body().getBlog()!=null && !response.body().getBlog().equals("")) {
                        textViewBlog.setText(response.body().getBlog());
                        blog = response.body().getBlog();
                    } else {
                        textViewBlog.setVisibility(View.GONE);
                    }
                    if (response.body().getLocation()!=null) {
                        textViewLocation.setText(response.body().getLocation());
                    } else {
                        textViewLocation.setVisibility(View.GONE);
                    }
                    String user_avatar = response.body().getAvatar_url();
                    image = user_avatar.replace("https","http");
                    if (user_avatar!=null) {
                        Glide.with(MyApplication.getAppContext()).load(user_avatar).into(imageViewProfile);
                    }
                    Date date = null;
                    try {
                        date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(response.body().getCreated_at());
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    String formattedDate= new SimpleDateFormat("dd/MM/yyyy").format(date);
                    textViewCreated.setText(formattedDate);
                } else if (response.code() == 429) {
                    // Handle unauthorized
                    toast("error contacting github error 429");
                } else {
                    toast("error contacting github "+response.code());
                }
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NonNull Call<ModelGithub> call, @NonNull Throwable t) {
                Log.d("error_contact", t.toString());
                toast("no network");
            }
        });
    }

    public void shareProfile(View view) {
        /*Create an ACTION_SEND Intent*/
        Intent i = new Intent(android.content.Intent.ACTION_SEND);
        /*The type of the content is text, obviously.*/
        i.setType("text/plain");
        /*Applying information Subject and Body.*/
        i.putExtra(android.content.Intent.EXTRA_SUBJECT, "Github profile");
        i.putExtra(android.content.Intent.EXTRA_TEXT, intent.getStringExtra("github_url"));
        /*Fire!*/
        startActivity(Intent.createChooser(i, "devRant"));
    }

    public void openBlog(View view) {
        Intent browserIntent;
        if (blog.contains("http")) {
            browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(blog));
        } else {
            browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + blog));
        }
        startActivity(browserIntent);
    }

    public void openGithub(View view) {

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(intent.getStringExtra("github_url").replace("https","http")));
        startActivity(browserIntent);
    }

    public void openGithubImage(View view) {
        if (image!=null) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(image));
            startActivity(browserIntent);
        }
    }
}