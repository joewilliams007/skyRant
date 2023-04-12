package com.dev.engineerrant;

import static android.text.format.DateUtils.getRelativeTimeSpanString;
import static com.dev.engineerrant.app.toast;
import static com.dev.engineerrant.auth.Account.vibrate;
import static com.dev.engineerrant.network.RetrofitClient.BASE_URL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dev.engineerrant.adapters.FeedAdapter;
import com.dev.engineerrant.adapters.FeedItem;
import com.dev.engineerrant.animations.Tools;
import com.dev.engineerrant.auth.Account;
import com.dev.engineerrant.auth.MyApplication;
import com.dev.engineerrant.classes.Counts;
import com.dev.engineerrant.classes.Rants;
import com.dev.engineerrant.classes.User_avatar;
import com.dev.engineerrant.methods.MethodsProfile;
import com.dev.engineerrant.models.ModelProfile;
import com.dev.engineerrant.models.ModelSuccess;
import com.dev.engineerrant.network.DownloadImageTask;
import com.dev.engineerrant.network.RetrofitClient;
import com.dev.engineerrant.post.VoteClient;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileActivity extends AppCompatActivity {

    String id, github, website, image = null, _username = null;
    TextView textViewAbout, textViewGithub, textViewUsername, textViewSkills, textViewScore, textViewWebsite, textViewJoined,textViewLocation;
    ImageView imageViewProfile, imageViewShare;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initialize();
        imageViewShare.setVisibility(View.INVISIBLE);
        Intent intent = getIntent();
        id = intent.getStringExtra("user_id");
        requestProfile();
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
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ModelProfile> call, @NonNull Response<ModelProfile> response) {
                if (response.isSuccessful()) {

                    // Do awesome stuff
                    assert response.body() != null;

                    String user_avatar = response.body().getProfile().getAvatar().getI();
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

                    _username = username;

                    String text = username;
                    String[] t = text.split("");
                    text = "";
                    for (String l:t) {
                        text+=l+" ";
                    }
                    textViewUsername.setText(text);

                    if (location.length()>0) {
                        textViewLocation.setText(location);
                    } else {
                        textViewLocation.setVisibility(View.GONE);
                    }

                    SpannableString content = new SpannableString(github);
                    content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                    textViewGithub.setText(content);

                    SpannableString contentWeb = new SpannableString(website);
                    contentWeb.setSpan(new UnderlineSpan(), 0, contentWeb.length(), 0);
                    textViewWebsite.setText(contentWeb);

                    // textViewAbout.setText(about);

                    com.dev.engineerrant.animations.typeWriter writer = new com.dev.engineerrant.animations.typeWriter();
                    writer.typeWrite(textViewAbout,ProfileActivity.this, about, 100L);


                    textViewSkills.setText(skills);

                    textViewJoined.setText(getRelativeTimeSpanString(created_time*1000L));

                    if (github.equals("")) {
                        textViewGithub.setVisibility(View.GONE);
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

                    imageViewProfile.setImageDrawable(null);
                    if (user_avatar!=null) {
                        Glide.with(MyApplication.getAppContext()).load("https://avatars.devrant.com/"+user_avatar).into(imageViewProfile);
                        image = "https://avatars.devrant.com/"+user_avatar;
                    } else {
                        imageViewProfile.setBackgroundColor(Color.parseColor("#"+avatar.getB()));
                    }

                    List<Rants> profile_rants = response.body().getProfile().getContent().getContent().getRants();

                    createFeedList(profile_rants);

                    imageViewShare.setVisibility(View.VISIBLE);
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


    public void createFeedList(List<Rants> rants){
        ArrayList<FeedItem> menuItems = new ArrayList<>();

        for (Rants rant : rants){
            String s = rant.getText();

            String url = null;
            if (rant.getAttached_image().toString().contains("http")) {
                url = rant.getAttached_image().toString().replace("{url=","").split(", width")[0];
            }

            menuItems.add(new FeedItem(url,s,rant.getId(),"feed",
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

        build(menuItems);
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
                switch (type) {
                    case "upvote":
                        votePost(1, menuItem.getId());
                        break;
                    case "downVote":
                        votePost(-1, menuItem.getId());
                        break;
                    case "removeVote":
                        votePost(0, menuItem.getId());
                        break;
                    case "rant":
                        Intent intent = new Intent(ProfileActivity.this, RantActivity.class);
                        intent.putExtra("id", String.valueOf(menuItem.getId()));
                        intent.putExtra("text", menuItem.getText());
                        intent.putExtra("username", menuItem.getUsername());
                        intent.putExtra("user_id", String.valueOf(menuItem.getUser_id()));
                        intent.putExtra("num_comments", String.valueOf(menuItem.getNumComments()));
                        intent.putExtra("date", getRelativeTimeSpanString(menuItem.getCreated_time() * 1000L));
                        if (Account.isLoggedIn()) {
                            intent.putExtra("vote_state", String.valueOf(menuItem.getVote_state()));
                        } else {
                            intent.putExtra("vote_state", "0");
                        }

                        if (menuItem.getUser_score() < 0) {
                            intent.putExtra("user_score", String.valueOf(menuItem.getUser_score()));
                        } else {
                            intent.putExtra("user_score", "+" + menuItem.getUser_score());
                        }

                        if (menuItem.getScore() < 0) {
                            intent.putExtra("score", String.valueOf(menuItem.getScore()));
                        } else {
                            intent.putExtra("score", "+" + menuItem.getScore());
                        }

                        intent.putExtra("user_id", String.valueOf(menuItem.getUser_id()));
                        intent.putExtra("b", menuItem.getB());
                        intent.putExtra("i", menuItem.getI());
                        intent.putExtra("info", "true");
                        intent.putExtra("image", menuItem.getImage());

                        String[] tags = menuItem.getTags();
                        String t = "";
                        for (String tag : tags) {
                            t += tag + ", ";
                        }
                        intent.putExtra("tags", t.substring(0, t.length() - 2));

                        startActivity(intent);
                        break;
                }
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
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/"+github));
        startActivity(browserIntent);
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
}