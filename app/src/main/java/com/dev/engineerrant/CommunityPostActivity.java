package com.dev.engineerrant;

import static android.text.format.DateUtils.getRelativeTimeSpanString;
import static com.dev.engineerrant.app.toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.dev.engineerrant.adapters.CommunityItem;
import com.dev.engineerrant.adapters.CommunityPostAdapter;
import com.dev.engineerrant.adapters.CommunityPostItem;
import com.dev.engineerrant.adapters.LinkAdapter;
import com.dev.engineerrant.adapters.LinkItem;
import com.dev.engineerrant.animations.Tools;
import com.dev.engineerrant.auth.Account;
import com.dev.engineerrant.auth.MyApplication;
import com.dev.engineerrant.network.methods.git.MethodsRepo;
import com.dev.engineerrant.network.models.git.ModelRepo;
import com.dev.engineerrant.network.RetrofitClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommunityPostActivity extends AppCompatActivity {


    public static CommunityItem communityItem;
    String ownerGithub = null;
    Intent intent;
    ImageView imageViewProfile;
    TextView repo, textViewStars, textViewUsername, textViewUploaded;
    ProgressBar progressBar;
    RecyclerView recyclerView, link_view;
    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_post);
        recyclerView = findViewById(R.id.community_post_view);
        progressBar = findViewById(R.id.progressBar);
        textViewStars = findViewById(R.id.textViewStars);
        textViewUsername = findViewById(R.id.textViewUsername);
        imageViewProfile = findViewById(R.id.imageViewProfile);
        textViewUploaded = findViewById(R.id.textViewUploaded);
        link_view = findViewById(R.id.link_view);
        repo = findViewById(R.id.repo);

        createList();
    }

    public void createList(){
        intent = getIntent();
        String total_url;
        if (intent.getStringExtra("repo_url")!=null) {
            total_url = "https://api.github.com/repos/"+intent.getStringExtra("repo_url").split("com/")[1];
        } else {
            total_url = "https://api.github.com/repos/"+communityItem.getGithub().split("com/")[1];
            textViewUploaded.setText("added "+getRelativeTimeSpanString(+communityItem.getTimestamp_added()* 1000L));
        }

        if (total_url.endsWith("/")) {
            total_url = total_url.substring(0, total_url.length() - 1);
        }

            MethodsRepo methods = RetrofitClient.getRetrofitInstance().create(MethodsRepo.class);



            progressBar.setVisibility(View.VISIBLE);

            String header = null;
            if (Account.githubKey()!=null) {
                header = "token "+Account.githubKey();
            }
            Call<ModelRepo> call = methods.getAllData(header,total_url);
            call.enqueue(new Callback<ModelRepo>() {
                @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
                @Override
                public void onResponse(@NonNull Call<ModelRepo> call, @NonNull Response<ModelRepo> response) {
                    if (response.isSuccessful()) {

                        // Do  awesome stuff
                        assert response.body() != null;


                        ArrayList<CommunityPostItem> menuItems = new ArrayList<>();

                        textViewStars.setText(String.valueOf(response.body().getStargazers_count()));
                        textViewUsername.setText(response.body().getOwner().getLogin());
                        repo.setText(response.body().getName());

                        ownerGithub = response.body().getOwner().getHtml_url();
                        imageViewProfile.setImageDrawable(null);
                        String user_avatar = response.body().getOwner().getAvatar_url();
                        if (user_avatar!=null) {
                            Glide.with(MyApplication.getAppContext()).load(user_avatar).into(imageViewProfile);
                        }


                        if (intent.getStringExtra("repo_url")==null) {
                            menuItems.add(new CommunityPostItem(communityItem.getDesc(),""));
                            menuItems.add(new CommunityPostItem("type",communityItem.getType()));
                            if (!communityItem.getOs().equals("")) {
                                menuItems.add(new CommunityPostItem("operating systems",communityItem.getOs()));
                            }
                        } else {
                            menuItems.add(new CommunityPostItem(response.body().getDescription(),""));
                        }


                        if (response.body().getLanguage()!=null) {
                            menuItems.add(new CommunityPostItem("language",response.body().getLanguage()));
                        }

                        if (response.body().getArchived()) {
                            menuItems.add(new CommunityPostItem("archived","true"));
                        }

                        menuItems.add(new CommunityPostItem("forks", response.body().getForks_count()+""));
                        Date date = null;
                        try {
                            date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(response.body().getCreated_at());
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                        String formattedDate = new SimpleDateFormat("dd/MM/yyyy").format(date);
                        menuItems.add(new CommunityPostItem("created",formattedDate));

                        Date datePushed = null;
                        try {
                            datePushed = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(response.body().getPushed_at());
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                        String formattedDatePushed = new SimpleDateFormat("dd/MM/yyyy").format(datePushed);
                        menuItems.add(new CommunityPostItem("last updated",formattedDatePushed));

                        showLinks();

                        build(menuItems);
                    } else if (response.code() == 429) {
                        // Handle unauthorized
                        toast("error contacting github error 429");
                    } else {
                        toast("error contacting github "+response.code()+", launching browser");
                        if (intent.getStringExtra("repo_url")==null) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(intent.getStringExtra(communityItem.getGithub().replace("https","http"))));
                            browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            MyApplication.getAppContext().startActivity(browserIntent);
                        } else {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(intent.getStringExtra("repo_url").replace("https","http")));
                            browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                           MyApplication.getAppContext().startActivity(browserIntent);
                        }
                    }
                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(@NonNull Call<ModelRepo> call, @NonNull Throwable t) {
                    Log.d("error_contact", t.toString());
                    toast("no network");
                }
            });

    }

    private void showLinks() {
        ArrayList<LinkItem> linkItems = new ArrayList<>();

        if (intent.getStringExtra("repo_url")==null) {
            linkItems.add(new LinkItem(communityItem.getGithub(),true));
            if (!communityItem.getWebsite().equals("")) {
                linkItems.add(new LinkItem(communityItem.getWebsite(),true));
            }
            if (!communityItem.getRelevant_dr_url().equals("")) {
                linkItems.add(new LinkItem(communityItem.getRelevant_dr_url(),true));
            }
        } else {
            linkItems.add(new LinkItem(intent.getStringExtra("repo_url"),true));
        }


        link_view.setHasFixedSize(false);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MyApplication.getAppContext());
        LinkAdapter mAdapter = new LinkAdapter(MyApplication.getAppContext(), linkItems, new LinkAdapter.AdapterCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClicked(Integer menuPosition) {
                LinkItem menuItem = linkItems.get(menuPosition);
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(menuItem.getLink().replace("https","http")));
                    browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MyApplication.getAppContext().startActivity(browserIntent);

            }
        }) {
            @Override
            public void onItemClicked(Integer feedPosition) {

            }
        };

        link_view.setLayoutManager(mLayoutManager);
        link_view.setAdapter(mAdapter);
    }

    private void build(ArrayList<CommunityPostItem> menuItems) {
        recyclerView.setHasFixedSize(false);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0,0);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(CommunityPostActivity.this);

        CommunityPostAdapter mAdapter = new CommunityPostAdapter(this, menuItems, new CommunityPostAdapter.AdapterCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClicked(Integer menuPosition) {
                CommunityPostItem menuItem = menuItems.get(menuPosition);
            }
        }) {
            @Override
            public void onItemClicked(Integer feedPosition) {

            }
        };

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);
    }

    public void sharePost(View view) {
        /*Create an ACTION_SEND Intent*/
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        /*The type of the content is text, obviously.*/
        intent.setType("text/plain");
        /*Applying information Subject and Body.*/
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "DevRant Project");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, communityItem.getGithub());
        /*Fire!*/
        startActivity(Intent.createChooser(intent, "devRant"));
    }

    public void openGithubProfile(View view) {
        Intent i = new Intent(CommunityPostActivity.this, GithubActivity.class);
        i.putExtra("github_url",ownerGithub);
        startActivity(i);
    }
}