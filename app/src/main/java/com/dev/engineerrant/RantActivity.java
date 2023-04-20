package com.dev.engineerrant;

import static android.text.format.DateUtils.getRelativeTimeSpanString;
import static com.dev.engineerrant.app.hideKeyboard;
import static com.dev.engineerrant.app.toast;
import static com.dev.engineerrant.auth.Account.vibrate;
import static com.dev.engineerrant.network.RetrofitClient.BASE_URL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dev.engineerrant.adapters.CommentAdapter;
import com.dev.engineerrant.adapters.CommentItem;
import com.dev.engineerrant.adapters.FeedAdapter;
import com.dev.engineerrant.adapters.FeedItem;
import com.dev.engineerrant.adapters.LinkAdapter;
import com.dev.engineerrant.adapters.LinkItem;
import com.dev.engineerrant.animations.Tools;
import com.dev.engineerrant.auth.Account;
import com.dev.engineerrant.auth.MyApplication;
import com.dev.engineerrant.classes.Comment;
import com.dev.engineerrant.classes.Links;
import com.dev.engineerrant.classes.Rants;
import com.dev.engineerrant.methods.MethodsFeed;
import com.dev.engineerrant.methods.MethodsRant;
import com.dev.engineerrant.models.ModelFeed;
import com.dev.engineerrant.models.ModelRant;
import com.dev.engineerrant.models.ModelSuccess;
import com.dev.engineerrant.network.DownloadImageTask;
import com.dev.engineerrant.network.RetrofitClient;
import com.dev.engineerrant.post.CommentClient;
import com.dev.engineerrant.post.VoteClient;
import com.dev.engineerrant.post.VoteCommentClient;
import com.nguyencse.URLEmbeddedData;
import com.nguyencse.URLEmbeddedView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RantActivity extends AppCompatActivity {
    ImageView imageViewProfile, imageViewRant, imageViewSurprise, imageViewRefresh;
    TextView textViewUsername, textViewScore, textViewText, textViewTags, textViewComments, textViewScoreRant, textViewDate, textViewPlus, textViewMinus;
    EditText editTextComment;

    ProgressBar progressBar;
    RecyclerView link_view;
    View view_container;
    int rantVote = 0;
    String id;
    String user_id;
    String image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rant);
        initialize();
        setRant();
        requestComments();
    }

    @SuppressLint("SetTextI18n")
    private void setRant() { // If rant was already loaded in previous Activity
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        if (intent.getStringExtra("surprise")==null) {
            imageViewSurprise.setVisibility(View.GONE);
        }
        if (intent.getStringExtra("info").equals("true")) {
            textViewUsername.setText(intent.getStringExtra("username"));
            textViewScore.setText(intent.getStringExtra("user_score"));

            if(Account.highlighter()) {
                Tools.highlighter(intent.getStringExtra("text"),textViewText);
            } else {
                textViewText.setText(intent.getStringExtra("text"));
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

            imageViewProfile.setImageDrawable(null);
            if (intent.getStringExtra("i") != null) {
                Glide.with(MyApplication.getAppContext()).load("https://avatars.devrant.com/" + intent.getStringExtra("i")).into(imageViewProfile);
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

    @SuppressLint("SetTextI18n")
    private void setRantPulled(Rants rants) { // If Rant could not be pulled from previous activity
        textViewUsername.setText(rants.getUser_username());
        textViewText.setText(rants.getText());
        textViewDate.setText(getRelativeTimeSpanString(rants.getCreated_time()* 1000L));



        if (rants.getUser_score()<0) {
            textViewScore.setText(String.valueOf(rants.getUser_score()));
        } else {
            textViewScore.setText("+"+ rants.getUser_score());
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
            Glide.with(MyApplication.getAppContext()).load("https://avatars.devrant.com/"+rants.getUser_avatar().getI()).into(imageViewProfile);
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


    private void requestComments() {
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
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(menuItem.getLink()));
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

                    Rants rants = response.body().getRant();

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

                    user_id = String.valueOf(rants.getUser_id());

                    createFeedList(comments);
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


        for (Comment comment : comments){
            String s = comment.getBody();

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


           /* if (rant.getAttached_image().toString().contains("http")) {
                menuItems.add(new FeedItem(null,s,rant.getId(),"feed",rant.getScore(), rant.getNum_comments(),0,null,rant.getVote_state(),rant.getUser_avatar().getB(),rant.getUser_avatar().getI()));
                menuItems.add(new FeedItem(null,s,rant.getId(),"image",rant.getScore(), rant.getNum_comments(),0,null,rant.getVote_state(),rant.getUser_avatar().getB(),rant.getUser_avatar().getI()));
            } else {
            }*/

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
                                requestComments();
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
                            requestComments();
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
                            requestComments();
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
        imageViewRefresh.setVisibility(View.GONE);
        int red = new Random().nextInt(255);
        int green = new Random().nextInt(255);
        int blue = new Random().nextInt(255);
        ThemeColors.setNewThemeColor(RantActivity.this, red, green, blue);
    }

    public void surpriseRant(View view) {
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
}