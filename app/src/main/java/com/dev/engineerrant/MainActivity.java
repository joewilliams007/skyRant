package com.dev.engineerrant;

import static android.text.format.DateUtils.getRelativeTimeSpanString;
import static com.dev.engineerrant.app.toast;
import static com.dev.engineerrant.auth.Account.username;
import static com.dev.engineerrant.auth.Account.vibrate;
import static com.dev.engineerrant.network.RetrofitClient.BASE_URL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dev.engineerrant.adapters.FeedAdapter;
import com.dev.engineerrant.adapters.FeedItem;
import com.dev.engineerrant.animations.RantLoadingAnimation;
import com.dev.engineerrant.animations.Tools;
import com.dev.engineerrant.auth.Account;
import com.dev.engineerrant.classes.NotifItems;
import com.dev.engineerrant.classes.NotifUnread;
import com.dev.engineerrant.classes.Rants;
import com.dev.engineerrant.methods.MethodsFeed;
import com.dev.engineerrant.methods.MethodsId;
import com.dev.engineerrant.methods.MethodsNotif;
import com.dev.engineerrant.methods.ModelNotif;
import com.dev.engineerrant.models.ModelFeed;
import com.dev.engineerrant.models.ModelId;
import com.dev.engineerrant.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Context context;
    TextView textViewUsername, textViewSetting, textViewNotif;
    private int i = 1;
    private long duration = 800;
    private Handler animHandler;
    String sort = "recent";
    RantLoadingAnimation rantLoadingAnimation;
    ConstraintLayout scrollLayout;
    private static final String NAME = "ThemeColors", KEY = "color";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scrollLayout = findViewById(R.id.scroll);

        textViewUsername = findViewById(R.id.username);
        textViewNotif = findViewById(R.id.notif);
        textViewSetting = findViewById(R.id.setting);


        handleDeepLinkIntent();

        String text = username();
        String[] t = text.toString().split("");
        text = "";
        for (String l:t) {
            text+=l+"\n";
        }
        textViewUsername.setText(text);

        setUpFadeAnimation(textViewUsername);
        setUpFadeAnimation(textViewNotif);
        setUpFadeAnimation(textViewSetting);

        //com.dev.engineerrant.animations.typeWriter writer = new com.dev.engineerrant.animations.typeWriter();
        //writer.typeWrite(textViewBottom,this, "> n o t i f\n> s e t t i n g s", 100L);

        requestFeed();


    }

    private void handleDeepLinkIntent() {
        Intent intent = getIntent();
        if(intent != null) {
            Uri data = intent.getData();
            if(data != null) {
                if (data.getPath().split("/")[1].equals("rants")) {
                    Intent i = new Intent(MainActivity.this, RantActivity.class);
                    i.putExtra("id",data.getPath().split("/")[2]);
                    i.putExtra("info","false");
                    startActivity(i);
                } else if (data.getPath().split("/")[1].equals("users")) {
                    getUserIdFromNameAndOpenProfile(data.getPath().split("/")[2]);
                }
            }
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
    }


    private void requestFeed() {
        scrollLayout.setVisibility(View.INVISIBLE);
        if (Account.animate()) {
            RelativeLayout relContainer = findViewById(R.id.relContainer);
            relContainer.setVisibility(View.VISIBLE);
            rantLoadingAnimation = new RantLoadingAnimation((RelativeLayout) findViewById(R.id.relContainer));
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
                    + "devrant/rants?token_id="+Account.id()+"&token_key="+Account.key()+"&user_id="+Account.user_id()+"&limit="+Account.limit()+"&sort="+sort+"&app=3&range=day&skip=0/";

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
                        String[] t = text.toString().split("");
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
                    toast("no success "+response.message());
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

           /* if (rant.getAttached_image().toString().contains("http")) {
                menuItems.add(new FeedItem(null,s,rant.getId(),"feed",rant.getScore(), rant.getNum_comments(),0,null,rant.getVote_state(),rant.getUser_avatar().getB(),rant.getUser_avatar().getI()));
                menuItems.add(new FeedItem(null,s,rant.getId(),"image",rant.getScore(), rant.getNum_comments(),0,null,rant.getVote_state(),rant.getUser_avatar().getB(),rant.getUser_avatar().getI()));
            } else {
            }*/

        }
        build(menuItems);
    }

    private void build(ArrayList<FeedItem> feedItems) {
        RecyclerView recyclerView = findViewById(R.id.feed_view);
        recyclerView.setHasFixedSize(false);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0,0);
        scrollLayout.setVisibility(View.VISIBLE);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this);


        FeedAdapter mAdapter = new FeedAdapter(this, feedItems, new FeedAdapter.AdapterCallback() {
            @Override
            public void onItemClicked(Integer menuPosition) {
                FeedItem menuItem = feedItems.get(menuPosition);
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
                    intent.putExtra("vote_state",String.valueOf("0"));
                }

                if (menuItem.getUser_score()<0) {
                    intent.putExtra("user_score",String.valueOf(menuItem.getUser_score()));
                } else {
                    intent.putExtra("user_score","+"+String.valueOf(menuItem.getUser_score()));
                }

                if (menuItem.getScore()<0) {
                    intent.putExtra("score",String.valueOf(menuItem.getScore()));
                } else {
                    intent.putExtra("score","+"+String.valueOf(menuItem.getScore()));
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
        requestFeed();
    }

    public void topFeed(View view) {
        sort = "top";
        requestFeed();
    }

    public void algoFeed(View view) {
        sort = "algo";
        requestFeed();
    }



}
