package com.dev.engineerrant;

import static android.text.format.DateUtils.getRelativeTimeSpanString;
import static com.dev.engineerrant.ReactionActivity.reactions;
import static com.dev.engineerrant.app.hideKeyboard;
import static com.dev.engineerrant.app.toast;
import static com.dev.engineerrant.auth.Account.vibrate;
import static com.dev.engineerrant.network.RetrofitClient.BASE_URL;
import static com.dev.engineerrant.network.RetrofitClient.SKY_SERVER_URL;

import static java.lang.Integer.toBinaryString;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.dev.engineerrant.adapters.CommentAdapter;
import com.dev.engineerrant.adapters.CommentItem;
import com.dev.engineerrant.adapters.LinkAdapter;
import com.dev.engineerrant.adapters.LinkItem;
import com.dev.engineerrant.animations.Tools;
import com.dev.engineerrant.auth.Account;
import com.dev.engineerrant.auth.MyApplication;
import com.dev.engineerrant.classes.dev.Comment;
import com.dev.engineerrant.classes.dev.Links;
import com.dev.engineerrant.classes.dev.Rants;
import com.dev.engineerrant.classes.sky.Reactions;
import com.dev.engineerrant.classes.dev.Weekly;
import com.dev.engineerrant.network.delete.DeleteCommentClient;
import com.dev.engineerrant.network.methods.dev.MethodsRant;
import com.dev.engineerrant.network.methods.sky.MethodsSkyPost;
import com.dev.engineerrant.network.methods.sky.MethodsVerifySkyKey;
import com.dev.engineerrant.network.models.dev.ModelRant;
import com.dev.engineerrant.network.models.sky.ModelSkyPost;
import com.dev.engineerrant.network.models.sky.ModelSuccess;
import com.dev.engineerrant.network.DownloadImageTask;
import com.dev.engineerrant.network.RetrofitClient;
import com.dev.engineerrant.network.models.sky.ModelVerifySkyKey;
import com.dev.engineerrant.network.post.CommentClient;
import com.dev.engineerrant.network.post.ModifyCommentClient;
import com.dev.engineerrant.network.post.VoteClient;
import com.dev.engineerrant.network.post.VoteCommentClient;
import com.dev.engineerrant.network.post.sky.CustomClient;
import com.dev.engineerrant.network.post.sky.StashClient;
import com.vanniktech.emoji.EmojiPopup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RantActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    ImageView imageViewProfile, imageViewRant, imageViewSurprise, imageViewRefresh, imageViewSendEmoji;
    TextView textViewMessage, textViewUsername, textViewScore, textViewText, textViewTags, textViewComments, textViewScoreRant, textViewDate, textViewPlus, textViewMinus, chill,textViewWeekly, textViewEmojiPlus,textViewReactions;
    EditText editTextComment, editTextReaction, editTextModifyComment;
    ConstraintLayout react, constraintLayoutModify;
    ProgressBar progressBar;
    RecyclerView link_view;
    View view_container;
    int rantVote = 0;
    String id, user_id, image, _username, user_avatar, color;
    Rants stash_rant;
    Boolean stashed = false;
    Boolean stashBtnUsed = false;
    public static CommentItem modifyComment = null;

    public static Integer widget_rant_id = null;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rant);
        initialize();
        handleIntent();
        setRant();
        requestComments(true);
        getReactions();
    }

    private void handleIntent() {
        intent = getIntent();
        if (widget_rant_id != null) {
            id = String.valueOf(widget_rant_id);
            widget_rant_id = null;
        }
    }

    private void getReactions() {
        try {
            MethodsSkyPost methods = RetrofitClient.getRetrofitInstance().create(MethodsSkyPost.class);
            String total_url = SKY_SERVER_URL+"post/"+id;

            Call<ModelSkyPost> call = methods.getAllData(total_url);

            call.enqueue(new Callback<ModelSkyPost>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NonNull Call<ModelSkyPost> call, @NonNull Response<ModelSkyPost> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;

                        reactions = response.body().getReactions();
                        String s_reactions = "";
                        for (Reactions reaction : reactions){
                            s_reactions += reaction.getReaction()+" ";
                        }
                        textViewReactions.setVisibility(View.VISIBLE);
                        if (s_reactions.length()>0) {
                            textViewReactions.setText(s_reactions);
                        }
                        textViewEmojiPlus.setVisibility(View.VISIBLE);
                    } else if (response.code() == 429) {
                        // Handle unauthorized
                        toast("you are not authorized");
                    } else {
                        toast("no success "+response.message());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ModelSkyPost> call, @NonNull Throwable t) {
                    Log.d("error_contact", t.toString());
                    textViewReactions.setVisibility(View.GONE);
                    textViewEmojiPlus.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {

        }
    }

    @SuppressLint("SetTextI18n")
    private void setRant() { // If rant was already loaded in previous Activity
        id = intent.getStringExtra("id");
        if (intent.getStringExtra("surprise")==null) {
            imageViewSurprise.setVisibility(View.GONE);
        }
        if (intent.getStringExtra("info").equals("true")) {
            textViewUsername.setText(intent.getStringExtra("username"));
            textViewScore.setText(intent.getStringExtra("user_score"));

            if (Account.binary()) {
                textViewScore.setText("+"+toBinaryString(Integer.parseInt(intent.getStringExtra("user_score").replaceAll("\\+",""))));
            }

            if(Account.highlighter()) {
                Tools.highlighter(intent.getStringExtra("text"),textViewText);
            } else {
                textViewText.setText(intent.getStringExtra("text"));
            }

            rant_text = intent.getStringExtra("text");
            int upper_case = 0;
            for (char ch: intent.getStringExtra("text").replaceAll(" ","").toCharArray()) {
                if (Character.isUpperCase(ch)) {
                    upper_case++;
                }
            }
            if (intent.getStringExtra("text").replaceAll(" ","").length()/2.5 < upper_case) {
                chill.setVisibility(View.VISIBLE);
            }

            Tools.textTagsHighlighter(intent.getStringExtra("tags"),textViewTags);
            textViewDate.setText(intent.getStringExtra("date"));
            user_id = intent.getStringExtra("user_id");
            rantVote = Integer.parseInt(intent.getStringExtra("vote_state"));

            if (rantVote == 1) {
                textViewPlus.setTextColor(Color.parseColor("#FFFF0000"));
                textViewMinus.setTextColor(Color.parseColor("#FFFFFF"));
            } else if (rantVote == -1) {
                textViewPlus.setTextColor(Color.parseColor("#FFFFFF"));
                textViewMinus.setTextColor(Color.parseColor("#FFFF0000"));
            }

            _username = intent.getStringExtra("username");
            user_avatar = intent.getStringExtra("i");
            color = "#" + intent.getStringExtra("b");

            imageViewProfile.setImageDrawable(null);
            if (intent.getStringExtra("i") != null) {
                Glide.with(MyApplication.getAppContext()).load("https://avatars.devrant.com/" + intent.getStringExtra("i"))
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                // Handle loading failure here
                                // This method will be called when the image fails to load
                                imageViewProfile.setBackgroundColor(Color.parseColor("#7bc8a4"));
                                return true; // Return false if you don't want to consume the failure
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                // Handle loading success here
                                // This method will be called when the image is successfully loaded
                                return false; // Return false if you don't want to consume the success
                            }
                        })
                        .into(imageViewProfile);
            } else {
                imageViewProfile.setBackgroundColor(Color.parseColor("#" + intent.getStringExtra("b")));
            }

            image = intent.getStringExtra("image");
            if (image == null) {
                imageViewRant.setVisibility(View.GONE);
            } else {
                    new DownloadImageTask(imageViewRant)
                            .execute(image);
            }
            textViewScoreRant.setText(intent.getStringExtra("score"));
        }
    }

    Boolean chilled = false;
    public void chillRant(View view) {
        setChillRant();
    }

    private void setChillRant() {
        if (chilled) {
            textViewText.setText(rant_text);
            chilled = false;
            chill.setText(R.string.chill);
        } else {
            textViewText.setText(rant_text.toLowerCase());
            chilled = true;
            chill.setText(R.string.revert);
        }
    }

    String rant_text = null;
    @SuppressLint("SetTextI18n")
    private void setRantPulled(Rants rants) { // If Rant could not be pulled from previous activity
        textViewUsername.setText(rants.getUser_username());
        textViewText.setText(rants.getText());
        rant_text = rants.getText();


        _username = rants.getUser_username();
        user_avatar = rants.getUser_avatar().getI();
        color = "#"+rants.getUser_avatar().getB();

        int upper_case = 0;
        for (char ch: rants.getText().replaceAll(" ","").toCharArray()) {
            if (Character.isUpperCase(ch)) {
                upper_case++;
            }
        }
        if (rants.getText().replaceAll(" ","").length()/2.5 < upper_case) {
            chill.setVisibility(View.VISIBLE);
        }

        textViewDate.setText(getRelativeTimeSpanString(rants.getCreated_time()* 1000L));


        if (rants.getUser_score()<0) {
            textViewScore.setText(String.valueOf(rants.getUser_score()));
        } else {
            textViewScore.setText("+"+ rants.getUser_score());

            if (Account.binary()) {
                textViewScore.setText("+"+toBinaryString(rants.getUser_score()));
            }
        }


        String[] tags = rants.getTags();
        StringBuilder t = new StringBuilder();
        for (String tag: tags) {
            t.append(tag).append(", ");
        }
        Tools.textTagsHighlighter(t.toString(),textViewTags);

        rantVote = rants.getVote_state();

        if (rantVote == 1) {
            textViewPlus.setTextColor(Color.parseColor("#FFFF0000"));
            textViewMinus.setTextColor(Color.parseColor("#FFFFFF"));
        } else if (rantVote == -1) {
            textViewPlus.setTextColor(Color.parseColor("#FFFFFF"));
            textViewMinus.setTextColor(Color.parseColor("#FFFF0000"));
        }

        imageViewProfile.setImageDrawable(null);
        if (rants.getUser_avatar().getI()!=null) {
            Glide.with(MyApplication.getAppContext()).load("https://avatars.devrant.com/"+rants.getUser_avatar().getI())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            // Handle loading failure here
                            // This method will be called when the image fails to load
                            imageViewProfile.setBackgroundColor(Color.parseColor("#7bc8a4"));
                            return true; // Return false if you don't want to consume the failure
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            // Handle loading success here
                            // This method will be called when the image is successfully loaded
                            return false; // Return false if you don't want to consume the success
                        }
                    })
                    .into(imageViewProfile);
        } else {
            imageViewProfile.setBackgroundColor(Color.parseColor("#"+rants.getUser_avatar().getB()));
        }

        String url = null;
        if (rants.getAttached_image().toString().contains("http")) {
            url = rants.getAttached_image().toString().replace("{url=","").split(", width")[0];
        }
        if (url == null) {
            imageViewRant.setVisibility(View.GONE);
        } else {
            image = url;
            if (Account.autoLoad()) {
                new DownloadImageTask(imageViewRant)
                        .execute(url);
            }
        }
        if (rants.getScore()<0) {
            textViewScoreRant.setText(String.valueOf(rants.getScore()));
        } else {
            textViewScoreRant.setText("+"+rants.getScore());
        }


    }

    private void initialize() {
        stashBtnUsed = false;

        link_view = findViewById(R.id.link_view);
        progressBar = findViewById(R.id.progressBar);
        imageViewRefresh = findViewById(R.id.imageViewRefresh);
        imageViewSurprise = findViewById(R.id.imageViewSurprise);
        imageViewProfile = findViewById(R.id.imageViewProfile);
        textViewUsername = findViewById(R.id.textViewUsername);
        textViewScore = findViewById(R.id.textViewScore);
        textViewText = findViewById(R.id.textViewText);
        textViewTags = findViewById(R.id.textViewTags);
        textViewComments = findViewById(R.id.textViewComments);
        textViewScoreRant = findViewById(R.id.textViewScoreRant);
        textViewDate = findViewById(R.id.textViewDate);
        textViewMinus = findViewById(R.id.textViewMinus);
        textViewPlus = findViewById(R.id.textViewPlus);
        editTextComment = findViewById(R.id.editTextComment);
        imageViewRant = findViewById(R.id.imageViewRant);
        view_container = findViewById(R.id.view_container);
        chill = findViewById(R.id.chill);
        react = findViewById(R.id.react);
        textViewWeekly = findViewById(R.id.textViewWeekly);
        chill.setVisibility(View.GONE);
        textViewWeekly.setVisibility(View.GONE);
        textViewMessage = findViewById(R.id.textViewMessage);
        textViewMessage.setVisibility(View.GONE);
        constraintLayoutModify = findViewById(R.id.constraintLayoutModify);
        constraintLayoutModify.setVisibility(View.GONE);
        editTextModifyComment = findViewById(R.id.editTextModifyComment);
        editTextReaction = findViewById(R.id.editTextReaction);
        textViewReactions = findViewById(R.id.textViewReactions);
        imageViewSendEmoji = findViewById(R.id.imageViewSendEmojie);
        react.setVisibility(View.GONE);
        textViewEmojiPlus = findViewById(R.id.textViewEmojiPlus);
        textViewReactions.setVisibility(View.GONE);
        textViewEmojiPlus.setVisibility(View.GONE);
        view_container.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onSingleClick(View v) {

            }

            @Override
            public void onDoubleClick(View v) {
                upvoteRant();
            }
        });
    }


    private void requestComments(Boolean refreshComments) {
        MethodsRant methods = RetrofitClient.getRetrofitInstance().create(MethodsRant.class);
        String total_url;

        if (Account.isLoggedIn()) {
            total_url = BASE_URL + "devrant/rants/"+id+"?app=3&token_id="+Account.id()+"&token_key="+Account.key()+"&user_id="+Account.user_id();
        } else {
            total_url = BASE_URL + "devrant/rants/"+id+"?app=3";
        }

        Call<ModelRant> call = methods.getAllData(total_url);

        call.enqueue(new Callback<ModelRant>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ModelRant> call, @NonNull Response<ModelRant> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;

                    List<Comment> comments = response.body().getComments();
                    List<Links> links = response.body().getRant().getLinks();

                    Weekly weekly = response.body().getRant().getWeekly();
                    if (weekly!=null) {
                        textViewWeekly.setVisibility(View.VISIBLE);
                        textViewWeekly.setText(weekly.getTopic());
                        textViewWeekly.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (Account.isLoggedIn()) {
                                    Intent intent = new Intent(RantActivity.this, PostComposeActivity.class);
                                    intent.putExtra("weekly","wk"+weekly.getWeek());
                                    intent.putExtra("type", "1");
                                    intent.putExtra("typeName", "R a n t / S t o r y");
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(RantActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                }
                            }
                        });
                    } else {
                        textViewWeekly.setVisibility(View.GONE);
                    }

                    if (links!=null) {
                        // LINK RECYCLERVIEW
                        ArrayList<LinkItem> linkItems = new ArrayList<>();

                        for (Links link : links) {
                            linkItems.add(new LinkItem(link.getUrl(),true));
                        }

                        link_view.setHasFixedSize(false);

                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MyApplication.getAppContext());
                        LinkAdapter mAdapter = new LinkAdapter(MyApplication.getAppContext(), linkItems, new LinkAdapter.AdapterCallback() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onItemClicked(Integer menuPosition) {
                                LinkItem menuItem = linkItems.get(menuPosition);
                                if (menuItem.getLink().contains("github")) {
                                    if (menuItem.getLink().split("com/")[1].contains("/")) {
                                        Intent intent = new Intent(MyApplication.getAppContext(), CommunityPostActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("repo_url",menuItem.getLink());
                                        MyApplication.getAppContext().startActivity(intent);
                                    } else {
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(menuItem.getLink()));
                                        browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        MyApplication.getAppContext().startActivity(browserIntent);
                                    }
                                } else {
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(menuItem.getLink()));
                                    browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    MyApplication.getAppContext().startActivity(browserIntent);
                                }

                            }
                        }) {
                            @Override
                            public void onItemClicked(Integer feedPosition) {

                            }
                        };

                        link_view.setLayoutManager(mLayoutManager);
                        link_view.setAdapter(mAdapter);
                    }

                    Rants rants = response.body().getRant();
                    stash_rant = rants;
                    rantVote = rants.getVote_state();

                    if (rantVote == 1) {
                        textViewPlus.setTextColor(Color.parseColor("#FFFF0000"));
                        textViewMinus.setTextColor(Color.parseColor("#FFFFFF"));
                    } else if (rantVote == -1) {
                        textViewPlus.setTextColor(Color.parseColor("#FFFFFF"));
                        textViewMinus.setTextColor(Color.parseColor("#FFFF0000"));
                    }

                    if (response.body().getRant().getScore()<0) {
                        textViewScoreRant.setText(String.valueOf(response.body().getRant().getScore()));
                    } else {
                        textViewScoreRant.setText("+"+response.body().getRant().getScore());
                    }

                    Intent intent = getIntent();
                    if (intent.getStringExtra("info").equals("false")) {
                        setRantPulled(rants);
                    }

                    if(Account.autoStash()) {
                        if (intent.getStringExtra("surprise")!=null) {
                            sendRantToServer();
                        }
                    }

                    user_id = String.valueOf(rants.getUser_id());

                    if (refreshComments) {
                        createFeedList(comments);
                    }
                } else if (response.code() == 429) {
                    // Handle unauthorized
                    toast("you are not authorized");
                } else {
                    toast("no success "+response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelRant> call, @NonNull Throwable t) {
                Log.d("error_contact", t.toString());
                toast("no network");
            }
        });
    }

    public void createFeedList(List<Comment> comments){
        ArrayList<CommentItem> menuItems = new ArrayList<>();

        String[] blockedWords = Account.blockedWords().split(",");
        String[] blockedUsers = Account.blockedUsers().split(",");
        for (Comment comment : comments){
            String s = comment.getBody();
            String username = comment.getUser_username().toLowerCase();
            String s_check = s.toLowerCase();
            boolean containsBlocked = false;

            if (!String.valueOf(comment.getUser_id()).equals(user_id)) {
                if (Account.blockGreenDot()) {
                    if (comment.getUser_avatar().getI()==null) {
                        containsBlocked = true;
                    }
                }

                if (Account.blockWordsComments()&&Account.blockedWords()!=null&&!Account.blockedWords().equals("") && !containsBlocked) {
                    for (String word : blockedWords) {
                        if (s_check.contains(word)) {
                            containsBlocked = true;
                            break;
                        }
                    }
                }
                if (Account.blockUsersComments()&&Account.blockedUsers()!=null&&!Account.blockedUsers().equals("") && !containsBlocked) {
                    for (String user : blockedUsers) {
                        if (username.equals(user.toLowerCase())) {
                            containsBlocked = true;
                            break;
                        }
                    }
                }
            }

            if (!containsBlocked) {
                String url = null;
                if (comment.getAttached_image()!=null) {
                    url = comment.getAttached_image().toString().replace("{url=","").split(", width")[0].replace("\\\\","");
                }

                menuItems.add(new CommentItem(url,s,comment.getId(),"comment",
                        comment.getScore(),
                        comment.getCreated_time(),
                        comment.getUser_username(),
                        comment.getVote_state(),
                        comment.getUser_avatar().getB(),
                        comment.getUser_avatar().getI(),
                        comment.getUser_score(),
                        comment.getUser_id(),
                        comment.getLinks()
                ));
            }
        }
        build(menuItems);
        imageViewRefresh.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void build(ArrayList<CommentItem> commentItems) {
        RecyclerView recyclerView = findViewById(R.id.comments_view);
        recyclerView.setHasFixedSize(false);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(RantActivity.this);


        CommentAdapter mAdapter = new CommentAdapter(this, commentItems, new CommentAdapter.AdapterCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClicked(Integer menuPosition, String type) {
                CommentItem menuItem = commentItems.get(menuPosition);
                switch (type) {
                    case "reply":
                        editTextComment.setText("@" + menuItem.getUsername() + " " + editTextComment.getText().toString());
                        break;
                    case "upvote":
                        voteComment(String.valueOf(menuItem.getId()), 1);
                        break;
                    case "downVote":
                        voteComment(String.valueOf(menuItem.getId()), -1);
                        break;
                    case "profile":
                        Intent intent = new Intent(RantActivity.this, ProfileActivity.class);
                        intent.putExtra("user_id",String.valueOf(menuItem.getUser_id()));
                        startActivity(intent);
                        break;
                    case "modify":
                        textViewMessage.setVisibility(View.GONE);
                        modifyComment = menuItem;
                        editTextModifyComment.setText(modifyComment.getText());
                        constraintLayoutModify.setVisibility(View.VISIBLE);
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

    private void voteComment(String c_id, int i) {
        if (Account.isLoggedIn()) {
            vibrate();
            try {
                RequestBody app = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), "3");
                RequestBody vote = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), String.valueOf(i));
                RequestBody token_id = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), String.valueOf(Account.id()));
                RequestBody token_key = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), Account.key());
                RequestBody user_id = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), String.valueOf(Account.user_id()));

                Retrofit.Builder builder = new Retrofit.Builder().baseUrl(BASE_URL + "comments/" + c_id + "/").addConverterFactory(GsonConverterFactory.create());

                Retrofit retrofit = builder.build(); // /devrant/comments/{comment_id}/vote

                VoteCommentClient client = retrofit.create(VoteCommentClient.class);
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
                                requestComments(true);
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
        } else {
            Intent intent = new Intent(RantActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    private void votePost(int i) {
        vibrate();
        try {
            RequestBody app = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), "3");
            RequestBody vote = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), String.valueOf(i));
            RequestBody token_id = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), String.valueOf(Account.id()));
            RequestBody token_key = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), Account.key());
            RequestBody user_id = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), String.valueOf(Account.user_id()));

            Retrofit.Builder builder = new Retrofit.Builder().baseUrl(BASE_URL + "devrant/rants/" + id + "/").addConverterFactory(GsonConverterFactory.create());
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
                            requestComments(false);
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

    private void upvoteRant() {
        if (Account.isLoggedIn()) {
            if (rantVote == 1) {
                votePost(0);
                textViewPlus.setTextColor(Color.parseColor("#FFFFFF"));
                textViewMinus.setTextColor(Color.parseColor("#FFFFFF"));
            } else {
                votePost(1);
                textViewPlus.setTextColor(Color.parseColor("#FFFF0000"));
                textViewMinus.setTextColor(Color.parseColor("#FFFFFF"));
            }
        } else {
            Intent intent = new Intent(RantActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }
    public void upvote(View view) {
        upvoteRant();
    }

    public void downVote(View view) {
        if (Account.isLoggedIn()) {
            if (rantVote == -1) {
                votePost(0);
                textViewPlus.setTextColor(Color.parseColor("#FFFFFF"));
                textViewMinus.setTextColor(Color.parseColor("#FFFFFF"));
            } else {
                votePost(-1);
                textViewPlus.setTextColor(Color.parseColor("#FFFFFF"));
                textViewMinus.setTextColor(Color.parseColor("#FFFF0000"));
            }
        } else {
            Intent intent = new Intent(RantActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    public void enterComment(View view) {
        if (Account.isLoggedIn()) {
            String c = editTextComment.getText().toString();
            if (c.length()<1) {
                toast("enter comment");
            } else {
                editTextComment.setText("");
                uploadC(c);
                hideKeyboard(RantActivity.this);
            }
        } else {
            Intent intent = new Intent(RantActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    private void uploadC(String c) {

        try {
            RequestBody app = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), "3");
            RequestBody comment = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), c);
            RequestBody token_id = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), String.valueOf(Account.id()));
            RequestBody token_key = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), Account.key());
            RequestBody user_id = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), String.valueOf(Account.user_id()));

            Retrofit.Builder builder = new Retrofit.Builder().baseUrl(BASE_URL + "devrant/rants/" + id + "/").addConverterFactory(GsonConverterFactory.create());
            Retrofit retrofit = builder.build();

            CommentClient client = retrofit.create(CommentClient.class);
            // finally, execute the request

            Call<ModelSuccess> call = client.upload(app, comment, token_id, token_key, user_id);
            call.enqueue(new Callback<ModelSuccess>() {
                @Override
                public void onResponse(@NonNull Call<ModelSuccess> call, @NonNull Response<ModelSuccess> response) {
                    Log.v("Upload", response + " ");

                    if (response.isSuccessful()) {
                        // Do awesome stuff
                        assert response.body() != null;
                        Boolean success = response.body().getSuccess();

                        if (success) {
                            requestComments(true);
                        } else {
                            toast("failed");
                            editTextComment.setText(c);
                        }

                    } else if (response.code() == 400) {
                        toast("Invalid login credentials entered. Please try again. :(");
                        editTextComment.setText(c);
                    } else if (response.code() == 429) {
                        // Handle unauthorized
                        toast("You are not authorized :P");
                        editTextComment.setText(c);
                    } else {
                        toast(response.message());
                        editTextComment.setText(c);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ModelSuccess> call, @NonNull Throwable t) {
                    toast("Request failed! " + t.getMessage());
                    editTextComment.setText(c);
                }

            });
        } catch (Exception e) {
            editTextComment.setText(e.toString());
        }
    }

    public void openImage(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(image));
        startActivity(browserIntent);
    }

    public void openProfile(View view) {
        Intent intent = new Intent(RantActivity.this, ProfileActivity.class);
        intent.putExtra("user_id",user_id);
        startActivity(intent);
    }

    public void finish(View view) {
        finish();
    }

    public void sharePost(View view) {
       share();
    }

    private void share() {
        /*Create an ACTION_SEND Intent*/
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        /*The type of the content is text, obviously.*/
        intent.setType("text/plain");
        /*Applying information Subject and Body.*/
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "DevRant");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "https://devrant.com/rants/"+id);
        /*Fire!*/
        startActivity(Intent.createChooser(intent, "devRant"));
    }


    public void refresh(View view) {
       refreshRant();
    }

    private void refreshRant() {
        imageViewRefresh.setVisibility(View.GONE);
        int red = new Random().nextInt(255);
        int green = new Random().nextInt(255);
        int blue = new Random().nextInt(255);
        ThemeColors.setNewThemeColor(RantActivity.this, red, green, blue);
    }

    public void surpriseRant(View view) {
        getSurprise();
    }

    private void getSurprise() {
        progressBar.setVisibility(View.VISIBLE);
        imageViewSurprise.setVisibility(View.GONE);
        getSurpriseId();
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
                    Rants surpriseRantNew = response.body().getRant();

                    openSurpriseRant(surpriseRantNew);

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

    public void openSurpriseRant(Rants surpriseRant) {
        Intent intent = new Intent(RantActivity.this, RantActivity.class);
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
        finish();
    }


    public void showOptions(View view) {
        PopupMenu popupMenu = new PopupMenu(this,view);
        popupMenu.setOnMenuItemClickListener(this);



        String[] blockedUsers = Account.blockedUsers().toLowerCase().split(",");

        if (Account.blockedUsers()!=null&&!Account.blockedUsers().equals("")) {
            Boolean blocked = false;
            for (String user : blockedUsers) {
                if (_username.toLowerCase().equals(user)) {
                    popupMenu.getMenu().add(0,3,20,"unblock "+_username); // groupId, itemId, order, title
                    blocked = true;
                    break;
                }
            }
            if (!blocked) {
                popupMenu.getMenu().add(0,2,20,"block "+_username); // groupId, itemId, order, title
            }
        } else {
            popupMenu.getMenu().add(0,2,20,"block "+_username); // groupId, itemId, order, title
        }

        if (Account.isFollow(user_id)) {
            popupMenu.getMenu().add(0,1,0,"unfollow "+_username); // groupId, itemId, order, title
        } else {
            popupMenu.getMenu().add(0,0,0,"follow "+_username); // groupId, itemId, order, title
        }

        popupMenu.getMenu().add(0,10,1,"share"); // groupId, itemId, order, title

        popupMenu.getMenu().add(0,11,2,"surprise rant"); // groupId, itemId, order, title

        if (chilled) {
            popupMenu.getMenu().add(0,12,3,"revert chill rant"); // groupId, itemId, order, title
        } else {
            popupMenu.getMenu().add(0,12,3,"chill rant"); // groupId, itemId, order, title
        }
        popupMenu.getMenu().add(0,13,4,"refresh"); // groupId, itemId, order, title

        popupMenu.getMenu().add(0,14,5,"copy rant text"); // groupId, itemId, order, title

        popupMenu.getMenu().add(0,15,19,"stash rant"); // groupId, itemId, order, title

        if (user_avatar!=null && !user_avatar.equals("")) {
            popupMenu.getMenu().add(0,6,6,"copy avatar link"); // groupId, itemId, order, title
            popupMenu.getMenu().add(0,7,7,"download avatar image"); // groupId, itemId, order, title
        }
        if (image!=null && !image.equals("")) {
            popupMenu.getMenu().add(0,8,8,"copy rant image link"); // groupId, itemId, order, title
            popupMenu.getMenu().add(0,9,9,"download rant image"); // groupId, itemId, order, title
        }
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip;
        DownloadManager.Request request;
        DownloadManager downloadManager;
        Uri uri;
        switch (menuItem.getItemId()) {
            case 0:
                Account.follow(user_id, _username, color, user_avatar);
                toast("following");
                return true;
            case 1:
                Account.unfollow(user_id);
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
            case 6: // copy avatar
                clip = ClipData.newPlainText("avatar link of "+_username, "https://avatars.devrant.com/"+user_avatar);
                clipboard.setPrimaryClip(clip);
                return true;
            case 7: // download avatar
                downloadManager =
                        (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
                uri = Uri.parse("https://avatars.devrant.com/"+user_avatar);
                request = new DownloadManager.Request(uri);
                request.setNotificationVisibility
                        (DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setTitle("skyRant");
                request.setDescription("image downloaded");
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,_username+".png");
                downloadManager.enqueue(request);
                toast("downloaded image");
                return true;
            case 8: // copy image
                clip = ClipData.newPlainText("image link of rant "+id, image);
                clipboard.setPrimaryClip(clip);
                return true;
            case 9: // download image
                downloadManager =
                        (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
                uri = Uri.parse(image);
                request = new DownloadManager.Request(uri);
                request.setNotificationVisibility
                        (DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setTitle("skyRant");
                request.setDescription("image downloaded");
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,id+".png");
                downloadManager.enqueue(request);
                toast("downloaded image");
                return true;
            case 10: // share
                share();
                return true;
            case 11: // surprise
                getSurprise();
                return true;
            case 12: // chill
                setChillRant();
                return true;
            case 13: // refresh
                refreshRant();
                return true;
            case 14: // copy rant text
                clip = ClipData.newPlainText("text of rant "+id, textViewText.getText().toString());
                clipboard.setPrimaryClip(clip);
                return true;
            case 15: // stash rant for preservation in skyAPI
                if (Account.isLoggedIn()) {
                    if (Account.isSessionSkyVerified()) {
                        stashBtnUsed = true;
                        sendRantToServer();
                    } else {
                        Intent intent = new Intent(RantActivity.this, SkyLoginActivity.class);
                        startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(RantActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                return true;
            default:
                return false;
        }
    }

    private void sendRantToServer() {
        if (stashed) {
            if(stashBtnUsed) {
                toast("already stashed!");
            }
            return;
        }
        if (stash_rant==null) {
            toast("rant has not been pulled yet");
        } else {
            uploadRant();
        }
    }

;
    private void uploadRant() {
        stashed = true;
        progressBar.setVisibility(View.VISIBLE);

        try {
            RequestBody user_id = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(Account.user_id()));
            RequestBody session_id = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(Account.id()));

            String[] _tags = stash_rant.getTags();
            StringBuilder t = new StringBuilder();
            for (String tag: _tags) {
                t.append(tag).append(",");
            }

            RequestBody rant_id = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(id));
            RequestBody text = RequestBody.create(MediaType.parse("multipart/form-data"), rant_text);
            RequestBody score = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(stash_rant.getScore()));
            RequestBody created_time = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(stash_rant.getCreated_time()));
            RequestBody num_comments = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(stash_rant.getNum_comments()));
            RequestBody tags = RequestBody.create(MediaType.parse("multipart/form-data"), t.toString());
            RequestBody edited = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(stash_rant.getEdited()));
            RequestBody rant_user_id = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(stash_rant.getUser_id()));
            RequestBody user_username = RequestBody.create(MediaType.parse("multipart/form-data"), stash_rant.getUser_username());
            RequestBody user_score = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(stash_rant.getUser_score()));
            RequestBody b = RequestBody.create(MediaType.parse("multipart/form-data"), stash_rant.getUser_avatar().getB());
            RequestBody i = RequestBody.create(MediaType.parse("multipart/form-data"), stash_rant.getUser_avatar().getI());
            RequestBody url;
            String _url = null;
            if (stash_rant.getAttached_image().toString().contains("http")) {
                _url = stash_rant.getAttached_image().toString().replace("{url=","").split(", width")[0];
            }
            if (_url == null) {
                 url = RequestBody.create(MediaType.parse("multipart/form-data"), "");
            } else {
                 url = RequestBody.create(MediaType.parse("multipart/form-data"), _url);
            }

            Retrofit.Builder builder = new Retrofit.Builder().baseUrl(SKY_SERVER_URL).addConverterFactory(GsonConverterFactory.create());
            Retrofit retrofit = builder.build();
            StashClient client = retrofit.create(StashClient.class);

            Call<ModelSuccess> call = client.upload(session_id, user_id,url,rant_id,text,score,created_time,num_comments,tags,edited,rant_user_id,user_username,user_score,b,i);
            call.enqueue(new Callback<ModelSuccess>() {
                @Override
                public void onResponse(@NonNull Call<ModelSuccess> call, @NonNull Response<ModelSuccess> response) {
                    Log.v("Upload", response + " ");
                    progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        // Do awesome stuff
                        assert response.body() != null;
                        Boolean success = response.body().getSuccess();

                        if(stashBtnUsed) {
                            if (success) {
                                toast("rant stashed!");
                            } else {
                                toast("rant was already stashed by someone");
                            }
                        }
                    } else if (response.code() == 400) {
                        toast("Invalid login credentials entered. Please try again. :(");
                        // editTextPost.setText(response.message());
                    } else if (response.code() == 429) {
                        // Handle unauthorized
                        toast("You are not authorized :P");
                    } else {
                        if (stashBtnUsed) {
                            toast(response.message());
                        }
                    }
                    stashBtnUsed = false;
                }

                @Override
                public void onFailure(@NonNull Call<ModelSuccess> call, @NonNull Throwable t) {
                    stashed = false;
                    progressBar.setVisibility(View.GONE);
                    if(stashBtnUsed) {
                        toast("Request failed! " + t.getMessage());
                    }
                    stashBtnUsed = false;
                }

            });
        } catch (Exception e) {
            stashed = false;
            progressBar.setVisibility(View.GONE);
            if(stashBtnUsed) {
                toast("error " + e);
            }
            stashBtnUsed = false;
        }
    }

    EmojiPopup emojiPopup = null;
    public void addEmoji(View view) {
        if (emojiPopup!=null && emojiPopup.isShowing()) {
            emojiPopup.toggle();
            react.setVisibility(View.GONE);
        } else {
            if (Account.isLoggedIn()) {
                if (Account.isSessionSkyVerified()) {
                    ConstraintLayout rootView = findViewById(R.id.rootView);
                    emojiPopup = new EmojiPopup(rootView,
                            editTextReaction
                    );

                    emojiPopup.toggle(); // Toggles visibility of the Popup.
                    //emojiPopup.dismiss(); // Dismisses the Popup.
                    //emojiPopup.isShowing(); // Returns true when Popup is showing.
                    react.setVisibility(View.VISIBLE);
                } else {
                    Intent intent = new Intent(RantActivity.this, SkyLoginActivity.class);
                    startActivity(intent);
                }
            } else {
                Intent intent = new Intent(RantActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }
    }

    public void sendEmoji(View view) {
        String reaction = editTextReaction.getText().toString();

        if (reaction.length()<1) {
            toast("enter emoji");
            return;
        }

        if (reaction.length()>3) {
            toast("enter one emoji only");
            return;
        }

        if (reaction.contains("u")) {
            if (reaction.replaceFirst("u","").contains("u")) {
                toast("enter only one emoji");
                return;
            }
        }

        uploadEmoji(reaction);
    }

    private void uploadEmoji(String reaction) {

        react.setVisibility(View.GONE);

        emojiPopup.dismiss();

        editTextReaction.setText(null);

        String total_url = SKY_SERVER_URL+"react_post/"+Account.user_id()+"/"+Account.id()+"/"+id+"/"+reaction;
        MethodsVerifySkyKey methods = RetrofitClient.getRetrofitInstance().create(MethodsVerifySkyKey.class);

        Call<ModelVerifySkyKey> call = methods.getAllData(total_url);
        call.enqueue(new Callback<ModelVerifySkyKey>() {
            @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
            @Override
            public void onResponse(@NonNull Call<ModelVerifySkyKey> call, @NonNull Response<ModelVerifySkyKey> response) {
                if (response.isSuccessful()) {

                    // Do  awesome stuff
                    assert response.body() != null;

                    Boolean success = response.body().getSuccess();
                    Boolean error = response.body().getError();
                    String message = response.body().getMessage();

                    if (success) {
                        getReactions();
                    }

                    if (error) {
                        toast(message);
                        getReactions();
                    }
                } else if (response.code() == 429) {
                    // Handle unauthorized
                    toast("error contacting github error 429");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelVerifySkyKey> call, @NonNull Throwable t) {
                Log.d("error_contact", t.toString());
                toast("no network");
            }
        });
    }

    public void viewReactions(View view) {
        Intent intent = new Intent(RantActivity.this, ReactionActivity.class);
        startActivity(intent);
    }

    public void doNothing(View view) {
    }

    public void cancelModify(View view) {
        constraintLayoutModify.setVisibility(View.GONE);
        app.hideKeyboard(RantActivity.this);
    }

    public void editComment(View view) {
        if (Account.isLoggedIn()) {
            String c = editTextModifyComment.getText().toString();
            if (c.length()<1) {
                toast("enter comment");
            } else {
                editTextModifyComment.setText("");
                modifyC(c);
                hideKeyboard(RantActivity.this);
            }
        } else {
            Intent intent = new Intent(RantActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    private void modifyC(String c) {

        progressBar.setVisibility(View.VISIBLE);

        try {
            RequestBody app = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), "3");
            RequestBody comment = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), c);
            RequestBody token_id = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), String.valueOf(Account.id()));
            RequestBody token_key = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), Account.key());
            RequestBody user_id = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), String.valueOf(Account.user_id()));

            Retrofit.Builder builder = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create());
            Retrofit retrofit = builder.build();

            ModifyCommentClient client = retrofit.create(ModifyCommentClient.class);
            // finally, execute the request

            Call<ModelSuccess> call = client.upload(String.valueOf(modifyComment.getId()),app, comment, token_id, token_key, user_id);
            call.enqueue(new Callback<ModelSuccess>() {
                @Override
                public void onResponse(@NonNull Call<ModelSuccess> call, @NonNull Response<ModelSuccess> response) {
                    Log.v("Upload", response + " ");

                    if (response.isSuccessful()) {
                        // Do awesome stuff
                        assert response.body() != null;
                        Boolean success = response.body().getSuccess();

                        if (success) {
                            constraintLayoutModify.setVisibility(View.GONE);
                            requestComments(true);
                        } else {
                            toast("failed");
                            textViewMessage.setVisibility(View.VISIBLE);
                            editTextModifyComment.setText(c);
                            progressBar.setVisibility(View.GONE);
                        }

                    } else if (response.code() == 400) {
                        progressBar.setVisibility(View.GONE);
                        toast("Invalid login credentials entered. Please try again. :(");
                        editTextModifyComment.setText(c);
                    } else if (response.code() == 429) {
                        // Handle unauthorized
                        progressBar.setVisibility(View.GONE);
                        toast("You are not authorized :P");
                        editTextModifyComment.setText(c);
                    } else {
                        toast(response.message());
                        progressBar.setVisibility(View.GONE);
                        editTextModifyComment.setText(c);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ModelSuccess> call, @NonNull Throwable t) {
                    toast("Request failed! " + t.getMessage());
                    editTextModifyComment.setText(c);
                    progressBar.setVisibility(View.GONE);
                }

            });
        } catch (Exception e) {
            editTextModifyComment.setText(c);
            progressBar.setVisibility(View.GONE);
        }
    }

    public void deleteComment(View view) {
        progressBar.setVisibility(View.VISIBLE);

        try {

            Retrofit.Builder builder = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create());
            Retrofit retrofit = builder.build();

            DeleteCommentClient client = retrofit.create(DeleteCommentClient.class);
            // finally, execute the request

            Call<ModelSuccess> call = client.delete(String.valueOf(modifyComment.getId()),"3", String.valueOf(Account.id()), Account.key(), String.valueOf(Account.user_id()));
            call.enqueue(new Callback<ModelSuccess>() {
                @Override
                public void onResponse(@NonNull Call<ModelSuccess> call, @NonNull Response<ModelSuccess> response) {
                    Log.v("Upload", response + " ");

                    if (response.isSuccessful()) {
                        // Do awesome stuff
                        assert response.body() != null;
                        Boolean success = response.body().getSuccess();

                        if (success) {
                            constraintLayoutModify.setVisibility(View.GONE);
                            requestComments(true);

                            toast("comment deleted");
                        } else {
                            toast("failed");
                            progressBar.setVisibility(View.GONE);
                        }

                    } else if (response.code() == 400) {
                        progressBar.setVisibility(View.GONE);
                        toast("Invalid login credentials entered. Please try again. :(");
                    } else if (response.code() == 429) {
                        // Handle unauthorized
                        progressBar.setVisibility(View.GONE);
                        toast("You are not authorized :P");
                    } else {
                        toast(response.message());
                        progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ModelSuccess> call, @NonNull Throwable t) {
                    toast("Request failed! " + t.getMessage());
                    progressBar.setVisibility(View.GONE);
                }

            });
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            editTextModifyComment.setText(e.toString());
        }
    }
}