package com.dev.engineerrant;

import static android.text.format.DateUtils.getRelativeTimeSpanString;
import static com.dev.engineerrant.app.hideKeyboard;
import static com.dev.engineerrant.app.toast;
import static com.dev.engineerrant.auth.Account.username;
import static com.dev.engineerrant.auth.Account.vibrate;
import static com.dev.engineerrant.network.RetrofitClient.BASE_URL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dev.engineerrant.adapters.FeedAdapter;
import com.dev.engineerrant.adapters.FeedItem;
import com.dev.engineerrant.adapters.UsersAdapter;
import com.dev.engineerrant.adapters.UsersItem;
import com.dev.engineerrant.animations.RantLoadingAnimation;
import com.dev.engineerrant.animations.Tools;
import com.dev.engineerrant.auth.Account;
import com.dev.engineerrant.auth.MyApplication;
import com.dev.engineerrant.classes.Rants;
import com.dev.engineerrant.network.methods.MethodsFeed;
import com.dev.engineerrant.network.methods.MethodsId;
import com.dev.engineerrant.network.methods.MethodsRant;
import com.dev.engineerrant.network.methods.MethodsSearch;
import com.dev.engineerrant.network.models.ModelFeed;
import com.dev.engineerrant.network.models.ModelId;
import com.dev.engineerrant.network.models.ModelRant;
import com.dev.engineerrant.network.models.ModelSearch;
import com.dev.engineerrant.network.models.ModelSuccess;
import com.dev.engineerrant.network.RetrofitClient;
import com.dev.engineerrant.notifcenter.AlarmReceiver;
import com.dev.engineerrant.network.post.VoteClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private Context context;
    TextView textViewUsername, textViewSetting, textViewNotif, textViewSurprise,textViewSearch,follow_feed;
    EditText editTextSearch;
    String sort = "recent";
    Boolean follow = false;
    Rants surpriseRant = null;
    RantLoadingAnimation rantLoadingAnimation;
    RecyclerView users_view;
    ConstraintLayout scrollLayout, search;
    private static final String NAME = "ThemeColors", KEY = "color";
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scrollLayout = findViewById(R.id.scroll);

        textViewUsername = findViewById(R.id.username);
        textViewNotif = findViewById(R.id.notif);
        textViewSetting = findViewById(R.id.setting);
        textViewSurprise = findViewById(R.id.textViewSurprise);
        editTextSearch = findViewById(R.id.editTextSearch);
        search = findViewById(R.id.search);
        search.setVisibility(View.GONE);
        textViewSearch = findViewById(R.id.textViewSearch);
        textViewSearch.setText(Account.search());
        users_view = findViewById(R.id.users_view);
        users_view.setVisibility(View.GONE);
        follow_feed = findViewById(R.id.follow_feed);

        follow = false;
        handleDeepLinkIntent(); // feed request comes afterwards

        if (!Account.followBtn()) {
            follow_feed.setVisibility(View.GONE);
        }

        if (!Account.isFeedUsername()) {
            String text = username();
            String[] t = text.split("");
            text = "";
            for (String l:t) {
                text+=l+"\n";
            }
            textViewUsername.setText(text);
        } else {
            textViewUsername.setText("M\nE");
        }


        System.out.println("LOGGING SESSION\n\n\n\n"+Account.key()+"\n"+Account.id()+"\n"+Account.user_id());

        setUpFadeAnimation(textViewUsername);
        setUpFadeAnimation(textViewNotif);
        setUpFadeAnimation(textViewSetting);

        if (Account.surprise()) {
            getSurpriseId();
        }

        if (Account.isLoggedIn()) { // Why do we need Alarm Receiver? To handle notifications obviously!!! No firebase, no Google ****, just a simple Alarm at which when it triggers tries to search for Notifs
            // Notif center
            AlarmReceiver alarm = new AlarmReceiver();
            alarm.setAlarm(this);
        }

        editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // Your piece of code on keyboard search click
                    String searchText = editTextSearch.getText().toString();
                    if (searchText.length()>0) {
                        requestSearch(searchText);
                        hideKeyboard(MainActivity.this);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void handleDeepLinkIntent() {
        try {
            Intent intent = getIntent();
            if(intent != null) {
                Uri data = intent.getData();
                if(data != null) {
                    if (data.getPath().split("/")[1].equals("rants") || data.getPath().split("/")[1].equals("collabs")) {
                        Intent i = new Intent(MainActivity.this, RantActivity.class);
                        i.putExtra("id",data.getPath().split("/")[2]);
                        i.putExtra("info","false");
                        startActivity(i);
                        requestFeed();
                    } else if (data.getPath().split("/")[1].equals("users")) {
                        getUserIdFromNameAndOpenProfile(data.getPath().split("/")[2]);
                    } else if (data.getHost().contains("github")) {
                        if (data.getPath().replaceFirst("/","").contains("/")) {
                            intent = new Intent(MyApplication.getAppContext(), CommunityPostActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("repo_url","https://github.com"+data.getPath());
                        } else {
                            intent = new Intent(MyApplication.getAppContext(), GithubActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("github_url","https://github.com"+data.getPath());
                        }
                        MyApplication.getAppContext().startActivity(intent);
                        requestFeed();
                    } else {
                        toast("URL format not supported");
                        requestFeed();
                    }
                } else if (intent.getStringExtra("searchTag")!=null) {
                    requestSearch(intent.getStringExtra("searchTag"));
                    search.setVisibility(View.VISIBLE);
                    editTextSearch.setText(intent.getStringExtra("searchTag"));
                    intent.removeExtra("searchTag");
                } else {
                    requestFeed();
                }
            }
        } catch (Exception ignored) {
            requestFeed();
        }
    }

    private void getUserIdFromNameAndOpenProfile(String username) {
        MethodsId methods = RetrofitClient.getRetrofitInstance().create(MethodsId.class);
        String total_url = BASE_URL
                + "get-user-id?username="+username+"&app=3"+"/";

        System.out.println(total_url);
        Call<ModelId> call = methods.getAllData(total_url);
        call.enqueue(new Callback<ModelId>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ModelId> call, @NonNull Response<ModelId> response) {
                if (response.isSuccessful()) {

                    // Do awesome stuff
                    assert response.body() != null;
                    Boolean success = response.body().getSuccess();
                    Intent i = new Intent(MainActivity.this, ProfileActivity.class);
                    i.putExtra("user_id",String.valueOf(response.body().getUser_id()));
                    startActivity(i);
                } else if (response.code() == 429) {
                    // Handle unauthorized
                    toast(response.message());
                } else {
                    toast(response+" ");

                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelId> call, @NonNull Throwable t) {
                Log.d("error_contact", t.toString());
                toast("no network");
            }
        });
    }

    private void setUpFadeAnimation(final TextView textView) {
        // Start from 0.1f if you desire 90% fade animation
        final Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(1000);
        fadeIn.setStartOffset(1000);
        // End to 0.1f if you desire 90% fade animation
        final Animation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(1000);
        fadeOut.setStartOffset(3000);

        fadeIn.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationEnd(Animation arg0) {
                // start fadeOut when fadeIn ends (continue)
                // textView.startAnimation(fadeOut);
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationStart(Animation arg0) {
            }
        });

        textView.startAnimation(fadeIn);
    }

    public void buttonClick(View view) {
        int red = new Random().nextInt(255);
        int green = new Random().nextInt(255);
        int blue = new Random().nextInt(255);
        ThemeColors.setNewThemeColor(MainActivity.this, red, green, blue);
        search.setVisibility(View.GONE);
    }


    private void requestFeed() {
        users_view.setVisibility(View.GONE);
        scrollLayout.setVisibility(View.INVISIBLE);
        if (search.getVisibility() == View.VISIBLE) {
            app.hideKeyboard(MainActivity.this);
            editTextSearch.setText(null);
            search.setVisibility(View.GONE);
            textViewNotif.setVisibility(View.VISIBLE);
            textViewSetting.setVisibility(View.VISIBLE);
        }

        if (Account.animate()) {
            RelativeLayout relContainer = findViewById(R.id.relContainer);
            relContainer.setVisibility(View.VISIBLE);
            rantLoadingAnimation = new RantLoadingAnimation(relContainer);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startReq();
                }
            }, 1000);
        } else {
            RelativeLayout relContainer = findViewById(R.id.relContainer);
            relContainer.setVisibility(View.GONE);
            startReq();
        }
    }

    private void startReq() {
        MethodsFeed methods = RetrofitClient.getRetrofitInstance().create(MethodsFeed.class);
        String total_url;

        if (Account.isLoggedIn()) {
            total_url = BASE_URL
                    + "devrant/rants?token_id="+Account.id()+"&user_id="+Account.user_id()+"&token_key="+Account.key()+"&limit="+Account.limit()+"&sort="+sort+"&app=3&range=day&skip=0/";

        } else {
            total_url = BASE_URL
                    + "devrant/rants?app=3&limit="+Account.limit()+"&sort="+sort+"&range=day&skip=0/";
        }

        Call<ModelFeed> call = methods.getAllData(total_url);
        call.enqueue(new Callback<ModelFeed>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ModelFeed> call, @NonNull Response<ModelFeed> response) {
                if (response.isSuccessful()) {

                    // Do awesome stuff
                    assert response.body() != null;
                    Boolean success = response.body().getSuccess();
                    List<Rants> rants = response.body().getRants();
                    //   toast("success: "+success+" size: "+rants.size());

                    if (Account.isLoggedIn()) {
                        String text = response.body().getUnread().getTotal() + " NOTIF";
                        String[] t = text.split("");
                        text = "";
                        for (String l:t) {
                            text+=l+"\n";
                        }
                        textViewNotif.setText(text);
                    }
                    createFeedList(rants);
                } else if (response.code() == 429) {
                    // Handle unauthorized
                    toast("you are not authorized");
                } else {
                    toast("please logout and in again - "+response.message());
                }

                if (rantLoadingAnimation != null)
                    rantLoadingAnimation.stop();


            }

            @Override
            public void onFailure(@NonNull Call<ModelFeed> call, @NonNull Throwable t) {
                Log.d("error_contact", t.toString());
                toast("no network");

                if (rantLoadingAnimation != null)
                    rantLoadingAnimation.stop();
            }
        });
    }


    ArrayList<FeedItem> menuItems;
    public void createFeedList(List<Rants> rants){
        menuItems = new ArrayList<>();

        ArrayList<UsersItem> profiles = new ArrayList<>();
        String[] blockedWords = Account.blockedWords().split(",");
        String[] blockedUsers = Account.blockedUsers().split(",");
        for (Rants rant : rants){
            String s = rant.getText();
            String username = rant.getUser_username().toLowerCase();

            String s_check = s.toLowerCase();
            boolean containsBlocked = false;

            if (Account.blockedWords()!=null&&!Account.blockedWords().equals("")) {
                for (String word : blockedWords) {
                    if (s_check.contains(word)) {
                        containsBlocked = true;
                        break;
                    }
                }
            }
            if (Account.blockedUsers()!=null&&!Account.blockedUsers().equals("")) {
                for (String user : blockedUsers) {
                    if (username.equals(user.toLowerCase())) {
                        containsBlocked = true;
                        break;
                    }
                }
            }


            if (!containsBlocked) {
                String url = null;
                if (rant.getAttached_image().toString().contains("http")) {
                    url = rant.getAttached_image().toString().replace("{url=","").split(", width")[0];
                }

                if (follow) {
                    if (Account.isFollow(String.valueOf(rant.getUser_id()))) {
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
                } else {
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

            }
        }

        if (search.getVisibility() == View.VISIBLE) {
            buildProfileSearch(profiles);
        }

        if (follow && menuItems.size()==0) {
            toast("no recent rants by users you follow");
        }
        build(menuItems);
    }

    private void buildProfileSearch(ArrayList<UsersItem> profiles) {
        users_view.setHasFixedSize(false);
        users_view.setVisibility(View.VISIBLE);
        users_view.getRecycledViewPool().setMaxRecycledViews(0,0);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this,LinearLayoutManager.HORIZONTAL,false);


        UsersAdapter mAdapter = new UsersAdapter(this, profiles, new UsersAdapter.AdapterCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClicked(Integer menuPosition) {
                UsersItem menuItem = profiles.get(menuPosition);
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("user_id",String.valueOf(menuItem.getProfile().getUser_id()));
                startActivity(intent);
            }
        }) {
            @Override
            public void onItemClicked(Integer feedPosition) {

            }
        };


        users_view.setAlpha(0);
        users_view.setTranslationY(Tools.dpToPx(40));
        users_view.animate().alpha(1).translationY(0).setDuration(300).withLayer();

        users_view.setLayoutManager(mLayoutManager);
        users_view.setAdapter(mAdapter);
    }

    private void build(ArrayList<FeedItem> feedItems) {
        RecyclerView recyclerView = findViewById(R.id.feed_view);
        recyclerView.setHasFixedSize(false);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0,0);
        scrollLayout.setVisibility(View.VISIBLE);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this);


        FeedAdapter mAdapter = new FeedAdapter(this, feedItems, new FeedAdapter.AdapterCallback() {
            @Override
            public void onItemClicked(Integer menuPosition, String type) {
                FeedItem menuItem = feedItems.get(menuPosition);
                switch (type) {
                    case "upvote":
                        votePost(1,menuItem.getId());
                        break;
                    case "downVote":
                        votePost(-1,menuItem.getId());
                        break;
                    case "removeVote":
                        votePost(0,menuItem.getId());
                        break;
                    case "rant":
                        Intent intent = new Intent(MainActivity.this, RantActivity.class);
                        intent.putExtra("id", String.valueOf(menuItem.getId()));
                        intent.putExtra("text",menuItem.getText());
                        intent.putExtra("username", menuItem.getUsername());
                        intent.putExtra("user_id", String.valueOf(menuItem.getUser_id()));
                        intent.putExtra("num_comments",String.valueOf(menuItem.getNumComments()));
                        intent.putExtra("date",getRelativeTimeSpanString(menuItem.getCreated_time()* 1000L));
                        if (Account.isLoggedIn()) {
                            intent.putExtra("vote_state",String.valueOf(menuItem.getVote_state()));
                        } else {
                            intent.putExtra("vote_state", "0");
                        }

                        if (menuItem.getUser_score()<0) {
                            intent.putExtra("user_score",String.valueOf(menuItem.getUser_score()));
                        } else {
                            intent.putExtra("user_score","+"+ menuItem.getUser_score());
                        }

                        if (menuItem.getScore()<0) {
                            intent.putExtra("score",String.valueOf(menuItem.getScore()));
                        } else {
                            intent.putExtra("score","+"+ menuItem.getScore());
                        }

                        intent.putExtra("user_id",String.valueOf(menuItem.getUser_id()));
                        intent.putExtra("b",menuItem.getB());
                        intent.putExtra("i",menuItem.getI());
                        intent.putExtra("info","true");
                        intent.putExtra("image",menuItem.getImage());

                        String[] tags = menuItem.getTags();
                        String t = "";
                        for (String tag: tags) {
                            t+=tag+", ";
                        }
                        intent.putExtra("tags",t.substring(0, t.length()-2));

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

    public void openMyProfile(View view) {
        if (Account.isLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            intent.putExtra("user_id",String.valueOf(Account.user_id()));
            startActivity(intent);
        } else {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    public void newPost(View view) {
        if (Account.isLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, PostActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    public void openSettings(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    public void openNotifs(View view) {
        if (Account.isLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, NotifActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    public void latestFeed(View view) {
        sort = "recent";
        follow = false;
        requestFeed();
    }

    public void topFeed(View view) {
        sort = "top";
        follow = false;
        requestFeed();
    }

    public void algoFeed(View view) {
        sort = "algo";
        follow = false;
        requestFeed();
    }

    public void algoFollow(View view) {
        sort = "latest";
        follow = true;
        requestFeed();
    }

    private void getSurpriseId() { // Get random Rant. Need to make 2 api calls cuz comments don't come with the surprise sadly
        MethodsRant methods = RetrofitClient.getRetrofitInstance().create(MethodsRant.class);
        String total_url;
        if (Account.isLoggedIn()) {
            total_url = BASE_URL + "devrant/rants/surprise?app=3&token_id="+Account.id()+"&token_key="+Account.key()+"&user_id="+Account.user_id();
        } else {
            total_url = BASE_URL + "devrant/rants/surprise?app=3";
        }

        Call<ModelRant> call = methods.getAllData(total_url);
        call.enqueue(new Callback<ModelRant>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ModelRant> call, @NonNull Response<ModelRant> response) {
                if (response.isSuccessful()) {

                    // Do awesome stuff
                    assert response.body() != null;
                    surpriseRant = response.body().getRant();

                    String s =  surpriseRant.getText().split("\n")[0];
                    if (s.length()>20) {
                        s = s.substring(0, 20);
                    }


                    com.dev.engineerrant.animations.typeWriter writer = new com.dev.engineerrant.animations.typeWriter();
                    textViewSurprise.setVisibility(View.VISIBLE);
                    writer.typeWrite(textViewSurprise,MainActivity.this, s+"... [read more]", 100L);
                } else if (response.code() == 429) {
                    // Handle unauthorized
                } else {

                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelRant> call, @NonNull Throwable t) {
                Log.d("no network", t.toString());
            }
        });
    }

    public void openSurpriseRant(View view) {
        Intent intent = new Intent(MainActivity.this, RantActivity.class);
        intent.putExtra("id", String.valueOf(surpriseRant.getId()));
        intent.putExtra("text",surpriseRant.getText());
        intent.putExtra("username", surpriseRant.getUser_username());
        intent.putExtra("user_id", String.valueOf(surpriseRant.getUser_id()));
        intent.putExtra("num_comments",String.valueOf(surpriseRant.getNum_comments()));
        intent.putExtra("date",getRelativeTimeSpanString(surpriseRant.getCreated_time()* 1000L));
        if (Account.isLoggedIn()) {
            intent.putExtra("vote_state",String.valueOf(surpriseRant.getVote_state()));
        } else {
            intent.putExtra("vote_state", "0");
        }

        if (surpriseRant.getUser_score()<0) {
            intent.putExtra("user_score",String.valueOf(surpriseRant.getUser_score()));
        } else {
            intent.putExtra("user_score","+"+ surpriseRant.getUser_score());
        }

        if (surpriseRant.getScore()<0) {
            intent.putExtra("score",String.valueOf(surpriseRant.getScore()));
        } else {
            intent.putExtra("score","+"+ surpriseRant.getScore());
        }

        intent.putExtra("user_id",String.valueOf(surpriseRant.getUser_id()));
        intent.putExtra("b",surpriseRant.getUser_avatar().getB());
        intent.putExtra("i",surpriseRant.getUser_avatar().getI());
        intent.putExtra("info","true");
        intent.putExtra("surprise","true");
        String url = null;
        if (surpriseRant.getAttached_image().toString().contains("http")) {
            url = surpriseRant.getAttached_image().toString().replace("{url=","").split(", width")[0];
        }
        intent.putExtra("image",url);

        String[] tags = surpriseRant.getTags();
        String t = "";
        for (String tag: tags) {
            t+=tag+", ";
        }
        intent.putExtra("tags",t.substring(0, t.length()-2));

        startActivity(intent);
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

    public void clearSearch(View view) {
        editTextSearch.setText(null);
        search.setVisibility(View.GONE);
        textViewNotif.setVisibility(View.VISIBLE);
        textViewSetting.setVisibility(View.VISIBLE);
        app.hideKeyboard(MainActivity.this);
    }

    public void showSearch(View view) {
        if (search.getVisibility() == View.GONE) {
            textViewNotif.setVisibility(View.GONE);
            textViewSetting.setVisibility(View.GONE);
            search.setVisibility(View.VISIBLE);
            textViewSearch.setText(Account.search());
        } else {
            search.setVisibility(View.GONE);
            editTextSearch.setText(null);
            textViewNotif.setVisibility(View.VISIBLE);
            textViewSetting.setVisibility(View.VISIBLE);
            app.hideKeyboard(MainActivity.this);
        }
    }


    public void searchBtnStart(View view) {
        String searchText = editTextSearch.getText().toString();
        if (searchText.length()>0) {
            requestSearch(searchText);
            hideKeyboard(MainActivity.this);
        }
    }

    private void requestSearch(String searchText) {
        if (Account.animate()) {
            RelativeLayout relContainer = findViewById(R.id.relContainer);
            relContainer.setVisibility(View.VISIBLE);
            rantLoadingAnimation = new RantLoadingAnimation(relContainer);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startSearchReq(searchText);
                }
            }, 1000);
        } else {
            RelativeLayout relContainer = findViewById(R.id.relContainer);
            relContainer.setVisibility(View.GONE);
            startSearchReq(searchText);
        }
    }

    private void startSearchReq(String searchText) {
        MethodsSearch methods = RetrofitClient.getRetrofitInstance().create(MethodsSearch.class);
        String total_url = BASE_URL + "devrant/search?app=3&term="+searchText;

        Call<ModelSearch> call = methods.getAllData(total_url);
        call.enqueue(new Callback<ModelSearch>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ModelSearch> call, @NonNull Response<ModelSearch> response) {
                if (response.isSuccessful()) {

                    // Do awesome stuff
                    assert response.body() != null;
                    Boolean success = response.body().getSuccess();
                    List<Rants> rants = response.body().getRants();
                    //   toast("success: "+success+" size: "+rants.size());

                    createFeedList(rants);
                }

                if (rantLoadingAnimation != null)
                    rantLoadingAnimation.stop();
            }

            @Override
            public void onFailure(@NonNull Call<ModelSearch> call, @NonNull Throwable t) {
                Log.d("error_contact", t.toString());
                toast("no network");
                if (rantLoadingAnimation != null)
                    rantLoadingAnimation.stop();
            }
        });
    }


}
