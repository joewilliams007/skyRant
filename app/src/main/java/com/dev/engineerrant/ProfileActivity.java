package com.dev.engineerrant;

import static android.text.format.DateUtils.getRelativeTimeSpanString;
import static com.dev.engineerrant.ProfileReactionActivity.profile_reactions;
import static com.dev.engineerrant.app.toast;
import static com.dev.engineerrant.auth.Account.vibrate;
import static com.dev.engineerrant.network.RetrofitClient.BASE_URL;
import static com.dev.engineerrant.network.RetrofitClient.SKY_SERVER_URL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.dev.engineerrant.adapters.FeedAdapter;
import com.dev.engineerrant.adapters.FeedItem;
import com.dev.engineerrant.animations.ScaleImageView;
import com.dev.engineerrant.animations.Tools;
import com.dev.engineerrant.auth.Account;
import com.dev.engineerrant.auth.GitHubAccount;
import com.dev.engineerrant.auth.MyApplication;
import com.dev.engineerrant.classes.dev.Counts;
import com.dev.engineerrant.classes.dev.Rants;
import com.dev.engineerrant.classes.dev.User_avatar;
import com.dev.engineerrant.network.DownloadImageTaskAlter;
import com.dev.engineerrant.network.methods.git.MethodsGithub;
import com.dev.engineerrant.network.methods.dev.MethodsProfile;
import com.dev.engineerrant.network.methods.sky.MethodsSkyProfile;
import com.dev.engineerrant.network.models.git.ModelGithub;
import com.dev.engineerrant.network.models.dev.ModelProfile;
import com.dev.engineerrant.network.models.sky.ModelSkyProfile;
import com.dev.engineerrant.network.models.sky.ModelSuccess;
import com.dev.engineerrant.network.RetrofitClient;
import com.dev.engineerrant.network.post.VoteClient;

import com.github.sahasbhop.apngview.ApngImageLoader;

import com.google.android.material.tabs.TabLayout;


import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    String id, github = null, website = null, image = null, _username = null, user_avatar = null ,color;
    TextView textViewAbout, textViewGithub, textViewUsername, textViewSkills, textViewScore, textViewWebsite, textViewJoined,textViewLocation;
    ImageView imageViewProfile, imageViewShare, imageViewGithub, imageViewOptions, imageViewFrame,imageViewProfileBg;
    VideoView imageViewBackground;
    ScaleImageView imageViewBackgroundStill;
    TabLayout tabLayout;
    Integer reactions_count = null;
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initialize();
        handleIntent();
        profile_reactions = null;
        requestSkyProfile();
        requestProfile();
    }

    private void handleIntent() {
        Intent intent = getIntent();
        id = intent.getStringExtra("user_id");

        if (id == null) {
            if (Account.isLoggedIn()) {
                id = String.valueOf(Account.user_id());
            } else {
                finish();
            }
        }
    }

    String uri_profile_background;

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (uri_profile_background!=null) {
                imageViewBackground.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.setLooping(true);
                    }
                });

                Uri _uri = Uri.parse(uri_profile_background);
                imageViewBackground.setVideoURI(_uri);
                imageViewBackground.start();
                imageViewBackground.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            imageViewBackground.setVisibility(View.GONE);
        }

    }

    private void requestSkyProfile() {
        try {
            MethodsSkyProfile methods = RetrofitClient.getRetrofitInstance().create(MethodsSkyProfile.class);
            String total_url = SKY_SERVER_URL+"profile/"+id;

            Call<ModelSkyProfile> call = methods.getAllData(total_url);

            call.enqueue(new Callback<ModelSkyProfile>() {
                @SuppressLint({"SetTextI18n", "SetJavaScriptEnabled"})
                @Override
                public void onResponse(@NonNull Call<ModelSkyProfile> call, @NonNull Response<ModelSkyProfile> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        profile_reactions = response.body().getProfile().getReactions();
                        reactions_count = response.body().getProfile().getReactions_count();

                        // String uri = "https://cdn.cloudflare.steamstatic.com/steamcommunity/public/images/items/322330/46461aaea39b18a4a3da2e6d3cf253006f2d6193.png";
                        String uri_bg = response.body().getProfile().getAvatar_bg_url();
                        String uri = response.body().getProfile().getAvatar_frame_url();
                        String _uri_profile_background = response.body().getProfile().getProfile_bg_url();
                        try {
                            if (_uri_profile_background.contains(".gif") || _uri_profile_background.contains(".png") || _uri_profile_background.contains(".jpg")|| _uri_profile_background.contains(".jpeg")) {
                                try {
                                    if (_uri_profile_background.contains(".png")) {
                                        try {
                                            ApngImageLoader.ApngConfig apngConfig = new ApngImageLoader.ApngConfig(999999999, true);
                                            ApngImageLoader.getInstance().init(ProfileActivity.this);
                                            ApngImageLoader.getInstance().displayApng(_uri_profile_background, imageViewBackgroundStill, apngConfig);
                                        } catch (Exception e) {
                                            Glide
                                                    .with(MyApplication.getAppContext())
                                                    .load(_uri_profile_background)
                                                    .into(new DrawableImageViewTarget(imageViewBackgroundStill));
                                        }
                                    }  else {
                                        Glide
                                                .with(MyApplication.getAppContext())
                                                .load(_uri_profile_background)
                                                .into(new DrawableImageViewTarget(imageViewBackgroundStill));
                                    }

                                } catch (Exception ignored){

                                }
                            } else {
                                uri_profile_background = response.body().getProfile().getProfile_bg_url();
                            }
                        } catch (Exception ignored){

                        }
                        try {
                            if (uri_bg.contains(".png")) {
                                try {
                                    ApngImageLoader.ApngConfig apngConfig = new ApngImageLoader.ApngConfig(999999999, true);
                                    ApngImageLoader.getInstance().init(ProfileActivity.this);
                                    ApngImageLoader.getInstance().displayApng(uri_bg, imageViewProfileBg, apngConfig);
                                } catch (Exception e) {
                                    Glide
                                            .with(MyApplication.getAppContext())
                                            .load(uri_bg)
                                            .into(new DrawableImageViewTarget(imageViewProfileBg));
                                }
                            }  else {
                                Glide
                                        .with(MyApplication.getAppContext())
                                        .load(uri_bg)
                                        .into(new DrawableImageViewTarget(imageViewProfileBg));
                            }

                        } catch (Exception ignored){

                        }
                        try {
                            if (uri.contains(".png")) {
                                try {
                                    ApngImageLoader.ApngConfig apngConfig = new ApngImageLoader.ApngConfig(999999999, true);
                                    ApngImageLoader.getInstance().init(ProfileActivity.this);
                                    ApngImageLoader.getInstance().displayApng(uri, imageViewFrame, apngConfig);
                                } catch (Exception e) {
                                    Glide
                                            .with(MyApplication.getAppContext())
                                            .load(uri)
                                            .into(new DrawableImageViewTarget(imageViewFrame));
                                }
                            }  else {
                                Glide
                                .with(MyApplication.getAppContext())
                                .load(uri)
                                .into(new DrawableImageViewTarget(imageViewFrame));
                            }
                        } catch (Exception ignored){

                        }

                        try {
                            imageViewBackground.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    mp.setLooping(true);
                                }
                            });

                            Uri _uri = Uri.parse(uri_profile_background);
                            imageViewBackground.setVideoURI(_uri);
                            imageViewBackground.start();
                            imageViewBackground.setVisibility(View.VISIBLE);
                        } catch (Exception ignored){

                        }

                    } else if (response.code() == 429) {
                        // Handle unauthorized

                    } else {

                    }
                }

                @Override
                public void onFailure(@NonNull Call<ModelSkyProfile> call, @NonNull Throwable t) {
                    Log.d("error_contact", t.toString());
                }
            });
        } catch (Exception ignored) {

        }
    }

    private void initialize() {
        textViewAbout = findViewById(R.id.textViewAbout);
        textViewGithub = findViewById(R.id.textViewGithub);
        textViewUsername = findViewById(R.id.profile);
        textViewScore = findViewById(R.id.textViewScore);
        textViewSkills = findViewById(R.id.textViewSkills);
        imageViewProfile = findViewById(R.id.imageViewProfile);
        textViewWebsite = findViewById(R.id.textViewWebsite);
        textViewJoined = findViewById(R.id.textViewJoined);
        imageViewShare = findViewById(R.id.imageViewShare);
        textViewLocation = findViewById(R.id.textViewLocation);
        imageViewGithub = findViewById(R.id.imageViewGithub);
        tabLayout = findViewById(R.id.tabLayout);
        imageViewOptions = findViewById(R.id.imageViewOptions);
        imageViewFrame = findViewById(R.id.imageViewFrame);
        imageViewProfileBg = findViewById(R.id.imageViewProfileBg);
        imageViewBackground = findViewById(R.id.videoViewBackground);
        imageViewBackgroundStill = findViewById(R.id.imageViewBackgroundStill);

        imageViewShare.setVisibility(View.INVISIBLE);
        imageViewOptions.setVisibility(View.INVISIBLE);
        tabLayout.setVisibility(View.GONE);
    }

    private void requestProfile() {
        MethodsProfile methods = RetrofitClient.getRetrofitInstance().create(MethodsProfile.class);
        String total_url;
        if (Account.isLoggedIn()){
            total_url = BASE_URL + "users/"+id+"?app=3&token_id="+Account.id()+"&token_key="+Account.key()+"&user_id="+Account.user_id();
        } else {
            total_url = BASE_URL + "users/"+id+"?app=3";
        }
        Call<ModelProfile> call = methods.getAllData(total_url);
        call.enqueue(new Callback<ModelProfile>() {
            @Override
            public void onResponse(@NonNull Call<ModelProfile> call, @NonNull Response<ModelProfile> response) {
                if (response.isSuccessful()) {

                    // Do awesome stuff
                    assert response.body() != null;

                    user_avatar = response.body().getProfile().getAvatar().getI();
                    String username = response.body().getProfile().getUsername();
                    int score = response.body().getProfile().getScore();
                    String about = response.body().getProfile().getAbout();
                    String location = response.body().getProfile().getLocation();
                    int created_time = response.body().getProfile().getCreated_time();
                    String skills = response.body().getProfile().getSkills();
                    github = response.body().getProfile().getGithub();
                    website = response.body().getProfile().getWebsite();
                    User_avatar avatar = response.body().getProfile().getAvatar();
                    Counts counts = response.body().getProfile().getContent().getCounts();
                    int rants_count = counts.getRants();
                    int upvoted_count = counts.getUpvoted();
                    int comments_count = counts.getComments();
                    int favorites_count = counts.getFavorites();
                    int collabs_count = counts.getCollabs();

                    List<Rants> profile_rants = response.body().getProfile().getContent().getContent().getRants();
                    List<Rants> favorites_rants = response.body().getProfile().getContent().getContent().getFavorites();
                    List<Rants> upVoted_rants = response.body().getProfile().getContent().getContent().getUpvoted();
                    List<Rants> comments_rants = response.body().getProfile().getContent().getContent().getComments();


                    tabLayout.addTab(tabLayout.newTab().setText(rants_count+"\nRants").setId(0));
                    tabLayout.addTab(tabLayout.newTab().setText(comments_count+"\nComments").setId(1));
                    tabLayout.addTab(tabLayout.newTab().setText(upvoted_count+"\n++'s").setId(2));
                    // tabLayout.addTab(tabLayout.newTab().setText(favorites_count+"\nFavorites").setId(3));

                    tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                    tabLayout.setVisibility(View.VISIBLE);

                    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                        @Override
                        public void onTabSelected(TabLayout.Tab tab) {
                            if (tab.getId()==0) {
                                createFeedList(profile_rants);
                            } else if (tab.getId()==1) {
                                createFeedList(comments_rants);
                            } else if (tab.getId()==2) {
                                createFeedList(upVoted_rants);
                            }
                        }

                        @Override
                        public void onTabUnselected(TabLayout.Tab tab) {

                        }

                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {

                        }
                    });

                    _username = username;

                    String text = username;
                    String[] t = text.split("");
                    text = "";
                    for (String l:t) {
                        text+=l+" ";
                    }
                    textViewUsername.setText(text);



                    SpannableString contentWeb = new SpannableString(website);
                    contentWeb.setSpan(new UnderlineSpan(), 0, contentWeb.length(), 0);
                    textViewWebsite.setText(contentWeb);

                    textViewAbout.setText(about);

                    /*com.dev.engineerrant.animations.typeWriter writer = new com.dev.engineerrant.animations.typeWriter();
                    writer.typeWrite(textViewAbout,ProfileActivity.this, about, 100L);*/


                    textViewSkills.setText(skills);

                    textViewJoined.setText(getRelativeTimeSpanString(created_time*1000L));

                    color = "#"+avatar.getB();
                    if (user_avatar!=null) {
                        // Glide.with(MyApplication.getAppContext()).load("https://avatars.devrant.com/"+user_avatar).into(imageViewProfileAlone);
                        String img = ("https://avatars.devrant.com/"+user_avatar.replaceAll("b-","b-1_"));
                        if (id.equals(String.valueOf(Account.user_id()))) {
                            Account.setAvatar(img);
                        }
                        new DownloadImageTaskAlter(imageViewProfile)
                                .execute(img);
                        image = "https://avatars.devrant.com/"+user_avatar;
                    } else {
                        imageViewProfile.setBackgroundColor(Color.parseColor("#"+avatar.getB()));
                    }
                    if (github.equals("") || github.contains(" ")) {
                        textViewGithub.setVisibility(View.GONE);

                        if (location.length()>0) {
                            textViewLocation.setText(location);
                        } else {
                            textViewLocation.setVisibility(View.GONE);
                        }
                    } else {
                        SpannableString content = new SpannableString(github);
                        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                        textViewGithub.setText(content);

                        // imageViewProfile.setBackgroundColor(Color.parseColor("#"+avatar.getB()));

                        getGithubProfile(github,user_avatar,location);
                    }


                    if (about.equals("")) {
                        textViewAbout.setVisibility(View.GONE);
                    }
                    if (skills.equals("")) {
                        textViewSkills.setVisibility(View.GONE);
                    }
                    if (website.equals("")) {
                        textViewWebsite.setVisibility(View.GONE);
                    }

                    if (score<0) {
                        textViewScore.setText(String.valueOf(score));
                    } else {
                        textViewScore.setText("+"+ score);
                    }



                    createFeedList(profile_rants);

                    imageViewShare.setVisibility(View.VISIBLE);
                    imageViewOptions.setVisibility(View.VISIBLE);
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

    private void getGithubProfile(String github, String user_avatar, String location) {
            String total_url;
            total_url = "https://api.github.com/users/"+github;

            if (total_url.endsWith("/")) {
                total_url = total_url.substring(0, total_url.length() - 1);
            }

            MethodsGithub methods = RetrofitClient.getRetrofitInstance().create(MethodsGithub.class);


            String header = null;
            if (GitHubAccount.githubKey()!=null) {
                header = "token "+GitHubAccount.githubKey();
            }

            Call<ModelGithub> call = methods.getAllData(header,total_url);
            call.enqueue(new Callback<ModelGithub>() {
                @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
                @Override
                public void onResponse(@NonNull Call<ModelGithub> call, @NonNull Response<ModelGithub> response) {

                    if (response.isSuccessful()) {

                        // Do  awesome stuff
                        assert response.body() != null;


                        if (response.body().getLocation()!=null) {
                            if (location.length()>0) {
                                textViewLocation.setText(location);
                            } else {
                                textViewLocation.setText(response.body().getLocation());
                            }
                        } else {
                            if (location.length()>0) {
                                textViewLocation.setText(location);
                            } else {
                                textViewLocation.setVisibility(View.GONE);
                            }
                        }

                        Glide.with(MyApplication.getAppContext()).load(response.body().getAvatar_url()).into(imageViewGithub);
                    } else {
                        textViewGithub.setVisibility(View.GONE);

                        if (location.length()>0) {
                            textViewLocation.setText(location);
                        } else {
                            textViewLocation.setVisibility(View.GONE);
                        }
                    }

                }

                @Override
                public void onFailure(@NonNull Call<ModelGithub> call, @NonNull Throwable t) {
                    textViewGithub.setVisibility(View.GONE);
                    if (user_avatar!=null) {
                        // Glide.with(MyApplication.getAppContext()).load("https://avatars.devrant.com/"+user_avatar).into(imageViewProfileAlone);
                        String img = ("https://avatars.devrant.com/"+user_avatar.replaceAll("b-","b-1_"));
                        new DownloadImageTaskAlter(imageViewProfile)
                                .execute(img);
                        image = "https://avatars.devrant.com/"+user_avatar;
                    }

                    if (location.length()>0) {
                        textViewLocation.setText(location);
                    } else {
                        textViewLocation.setVisibility(View.GONE);
                    }
                }
            });
    }


    public static List<Rants> selected;
    public void createFeedList(List<Rants> rants){
        selected = rants;

        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.execute(rants);
    }



    private class AsyncTaskRunner extends AsyncTask<List<Rants>, List<Rants>, String> {

        ArrayList<FeedItem> menuItems;

        @Override
        protected String doInBackground(List<Rants>... params) {
            try {
                menuItems = new ArrayList<>();
                for (Rants rant : selected){
                    String s = rant.getText();
                    if (s == null) {
                        s = rant.getBody();
                    }
                    Integer id = rant.getRant_id();
                    if (rant.getRant_id() == null) {
                        id = rant.getId();
                    }

                    String url = null;
                    if (rant.getAttached_image()!=null) {
                        if (rant.getAttached_image().toString().contains("http")) {
                            url = rant.getAttached_image().toString().replace("{url=","").split(", width")[0];
                        }
                    }


                    menuItems.add(new FeedItem(url,s,id,"feed",
                            rant.getScore(),
                            rant.getNum_comments(),
                            rant.getCreated_time(),
                            rant.getUser_username(),
                            rant.getVote_state(),
                            rant.getUser_avatar().getB(),
                            rant.getUser_avatar().getI(),
                            rant.getTags(),
                            rant.getUser_score(),
                            rant.getUser_id()
                    ));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "true";
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            build(menuItems);
        }


        @Override
        protected void onPreExecute() {

        }
    }

    private void build(ArrayList<FeedItem> feedItems) {
        RecyclerView recyclerView = findViewById(R.id.profile_view);
        recyclerView.setHasFixedSize(false);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0,0);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ProfileActivity.this);


        FeedAdapter mAdapter = new FeedAdapter(this, feedItems, new FeedAdapter.AdapterCallback() {
            @Override
            public void onItemClicked(Integer menuPosition, String type) {
                FeedItem menuItem = feedItems.get(menuPosition);
                Intent intent = new Intent(ProfileActivity.this, RantActivity.class);
                intent.putExtra("id", String.valueOf(menuItem.getId()));
                intent.putExtra("info", "false");

                startActivity(intent);
            }
        }) {
            @Override
            public void onItemClicked(Integer feedPosition) {

            }
        };



        recyclerView.setAlpha(0);
        recyclerView.setTranslationY(Tools.dpToPx(40));
        recyclerView.animate().alpha(1).translationY(0).setDuration(300).withLayer();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {
                    Log.d("-----","end");
                    vibrate();
                }
            }
        });

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);

        tabLayout.setVisibility(View.VISIBLE);
    }

    public void openWebsite(View view) {
        Intent browserIntent;
        if (website.contains("http")) {
            browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
        } else {
            browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + website));
        }
        startActivity(browserIntent);
    }

    public void openGithub(View view) {
        Intent i = new Intent(ProfileActivity.this, GithubActivity.class);
        i.putExtra("github_url","https://github.com/"+github);
        startActivity(i);
    }

    public void openProfileIcon(View view) {
        if (image!=null) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(image));
            startActivity(browserIntent);
        }
    }

    public void sharePost(View view) {
        /*Create an ACTION_SEND Intent*/
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        /*The type of the content is text, obviously.*/
        intent.setType("text/plain");
        /*Applying information Subject and Body.*/
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "DevRant");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "https://devrant.com/users/"+_username);
        /*Fire!*/
        startActivity(Intent.createChooser(intent, "devRant"));
    }

    private void votePost(int i, int rant_id) {
        vibrate();
        try {
            RequestBody app = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), "3");
            RequestBody vote = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), String.valueOf(i));
            RequestBody token_id = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), String.valueOf(Account.id()));
            RequestBody token_key = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), Account.key());
            RequestBody user_id = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), String.valueOf(Account.user_id()));

            Retrofit.Builder builder = new Retrofit.Builder().baseUrl(BASE_URL + "devrant/rants/" + rant_id + "/").addConverterFactory(GsonConverterFactory.create());
            Retrofit retrofit = builder.build();

            VoteClient client = retrofit.create(VoteClient.class);
            // finally, execute the request

            Call<ModelSuccess> call = client.vote(app, vote, token_id, token_key, user_id);
            call.enqueue(new Callback<ModelSuccess>() {
                @Override
                public void onResponse(@NonNull Call<ModelSuccess> call, @NonNull Response<ModelSuccess> response) {
                    Log.v("Upload", response + " ");

                    if (response.isSuccessful()) {
                        // Do awesome stuff
                        assert response.body() != null;
                        Boolean success = response.body().getSuccess();

                        if (success) {
                            // requestFeed();
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
                }

            });
        } catch (Exception e) {
            toast(e.toString());
        }
    }

    public void showOptions(View view) {
        PopupMenu popupMenu = new PopupMenu(this,view);
        popupMenu.setOnMenuItemClickListener(this);

        String[] blockedUsers = Account.blockedUsers().toLowerCase().split(",");


        if (Account.blockedUsers()!=null&&!Account.blockedUsers().equals("")) {
            Boolean blocked = false;
            for (String user : blockedUsers) {
                if (_username.toLowerCase().equals(user)) {
                    popupMenu.getMenu().add(0,3,9,"unblock"); // groupId, itemId, order, title
                    blocked = true;
                    break;
                }
            }
            if (!blocked) {
                popupMenu.getMenu().add(0,2,9,"block"); // groupId, itemId, order, title
            }
        } else {
            popupMenu.getMenu().add(0,2,9,"block"); // groupId, itemId, order, title
        }

        if (Account.isFollow(id)) {
            popupMenu.getMenu().add(0,1,0,"unfollow"); // groupId, itemId, order, title
        } else {
            popupMenu.getMenu().add(0,0,0,"follow"); // groupId, itemId, order, title
        }

        if (github!=null && !github.equals("")) {
            popupMenu.getMenu().add(0,4,4,"copy GitHub link"); // groupId, itemId, order, title
        }
        if (website!=null && !website.equals("")) {
            popupMenu.getMenu().add(0,5,5,"copy website link"); // groupId, itemId, order, title
        }
        if (user_avatar!=null && !user_avatar.equals("")) {
            popupMenu.getMenu().add(0,6,6,"copy avatar link"); // groupId, itemId, order, title
            popupMenu.getMenu().add(0,7,7,"download avatar image"); // groupId, itemId, order, title
        }
        if (profile_reactions !=null) {
            popupMenu.getMenu().add(0,8,8,"view reactions"); // groupId, itemId, order, title
        }
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip;
        switch (menuItem.getItemId()) {
            case 0:
                Account.follow(id, _username, color, user_avatar);
                toast("following");
                return true;
            case 1:
                Account.unfollow(id);
                toast("unfollowed");
                return true;
            case 2: // block
                toast("blocked");
                if (Account.blockedUsers()==null || Account.blockedUsers().equals("")) {
                    Account.setBlockedUsers(_username);
                } else {
                    Account.setBlockedUsers(Account.blockedUsers()+","+_username);
                }
                return true;
            case 3: // unblock
                String[] blockedUsers = Account.blockedUsers().split(",");
                String new_blocked_users = null;
                for (String user : blockedUsers) {
                    if (!_username.equals(user.toLowerCase())) {
                        new_blocked_users += user.toLowerCase()+",";
                    }
                }
                if (new_blocked_users!=null) {
                    Account.setBlockedUsers(new_blocked_users.substring(0, new_blocked_users.length() - 1).replaceFirst("null",""));
                } else {
                    Account.setBlockedUsers(null);
                }
                toast("unblocked");
                return true;
            case 4: // copy github
                clip = ClipData.newPlainText("GitHub of "+_username, "https://github.com/"+github);
                clipboard.setPrimaryClip(clip);
                return true;
            case 5: // copy website
                clip = ClipData.newPlainText("website of "+_username, website);
                clipboard.setPrimaryClip(clip);
                return true;
            case 6: // copy avatar
                clip = ClipData.newPlainText("avatar link of "+_username, "https://avatars.devrant.com/"+user_avatar);
                clipboard.setPrimaryClip(clip);
                return true;
            case 7: // download avatar
                    DownloadManager downloadManager =
                        (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse("https://avatars.devrant.com/"+user_avatar);
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setNotificationVisibility
                            (DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setTitle("skyRant");
                    request.setDescription("image downloaded");
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,_username+".png");
                    downloadManager.enqueue(request);
                    toast("downloaded image");
                return true;
            case 8: // view reactions
                Intent intent = new Intent(ProfileActivity.this, ProfileReactionActivity.class);
                startActivity(intent);
                return true;
            default:
                return false;
        }
    }
}