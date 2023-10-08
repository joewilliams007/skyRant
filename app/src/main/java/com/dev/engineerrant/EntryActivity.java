package com.dev.engineerrant;

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
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dev.engineerrant.adapters.CommentAdapter;
import com.dev.engineerrant.adapters.CommentItem;
import com.dev.engineerrant.adapters.EntryCommentAdapter;
import com.dev.engineerrant.adapters.EntryCommentItem;
import com.dev.engineerrant.adapters.LinkAdapter;
import com.dev.engineerrant.adapters.LinkItem;
import com.dev.engineerrant.animations.Tools;
import com.dev.engineerrant.auth.Account;
import com.dev.engineerrant.auth.MyApplication;
import com.dev.engineerrant.classes.dev.Comment;
import com.dev.engineerrant.classes.dev.Links;
import com.dev.engineerrant.classes.dev.Rants;
import com.dev.engineerrant.classes.dev.Weekly;
import com.dev.engineerrant.classes.kbin.CommentItems;
import com.dev.engineerrant.classes.kbin.Items;
import com.dev.engineerrant.network.DownloadImageTask;
import com.dev.engineerrant.network.RetrofitClient;
import com.dev.engineerrant.network.methods.dev.MethodsRant;
import com.dev.engineerrant.network.methods.kbin.MethodsComments;
import com.dev.engineerrant.network.models.dev.ModelRant;
import com.dev.engineerrant.network.models.kbin.ModelComments;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EntryActivity extends AppCompatActivity {

    public static Items selected_entry = null;
    TextView textViewTitle, textViewBody, textViewUsername, textViewTags, textViewViews, textViewComments;
    ImageView imageViewProfile, imageViewRant;
    RecyclerView link_view, comments_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        initialize();
        setEntry();
        requestComments();
    }

    private void initialize() {
        textViewBody = findViewById(R.id.textViewBody);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewUsername = findViewById(R.id.textViewUsername);
        imageViewProfile = findViewById(R.id.imageViewProfile);
        textViewTags = findViewById(R.id.textViewTags);
        textViewViews = findViewById(R.id.textViewViews);
        textViewComments = findViewById(R.id.textViewComments);
        imageViewRant = findViewById(R.id.imageViewRant);
        link_view = findViewById(R.id.link_view);
    }

    @SuppressLint("SetTextI18n")
    private void setEntry() {
        if (selected_entry.getBody()==null) {
            textViewBody.setVisibility(View.GONE);
        }
        textViewBody.setText(selected_entry.getBody());
        textViewTitle.setText(selected_entry.getTitle());
        textViewUsername.setText(selected_entry.getUser().getUsername());
        textViewViews.setText(selected_entry.getViews()+" views");
        imageViewProfile.setImageDrawable(null);
        if (selected_entry.getUser().getAvatar()!=null) {
            Glide.with(MyApplication.getAppContext()).load(selected_entry.getUser().getAvatar().getStorageUrl()).into(imageViewProfile);
        } else {
            imageViewProfile.setBackgroundColor(Color.parseColor("#7bc892"));
        }
        if (selected_entry.getNumComments() < 1) {
            textViewComments.setVisibility(View.GONE);
        } else {
            textViewComments.setText(selected_entry.getNumComments()+" comments");
        }
        if (selected_entry.getTags() == null) {
            textViewTags.setVisibility(View.INVISIBLE);
        } else {
            String[] tags = selected_entry.getTags();
            String t = "";
            for (String tag: tags) {
                t+=tag+", ";
            }
            textViewTags.setText(t.substring(0, t.length()-2));
        }
        if (selected_entry.getImage() == null) {
            imageViewRant.setVisibility(View.GONE);
        } else {
            imageViewRant.setVisibility(View.VISIBLE);

            if (Account.autoLoad()) {
                new DownloadImageTask(imageViewRant)
                        .execute(selected_entry.getImage().getStorageUrl());
            }
        }
        String text = selected_entry.getBody();

        if (text==null) {
            return;
        }
        if (text.contains("http")) {
            ArrayList<LinkItem> linkItems = new ArrayList<>();

            for (String _text : text.split(" ")) {
                if (_text.contains("http")) {
                    linkItems.add(new LinkItem(_text.replaceAll("http"," http").split(" ")[1],true));
                }
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
    }

    public void openImage(View view) {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(selected_entry.getImage().getStorageUrl()));
            startActivity(browserIntent);
        } catch (Exception e) {
            toast("could not open image");
        }
    }

    private void requestComments() {
        MethodsComments methods = RetrofitClient.getRetrofitInstance().create(MethodsComments.class);
        String total_url = "https://kbin.melroy.org/api/entry/"+selected_entry.getEntryId()+"/comments?sortBy=newest&time=%E2%88%9E&p=1&perPage=15&d=10&usePreferredLangs=false";

        Call<ModelComments> call = methods.getAllData(total_url);

        call.enqueue(new Callback<ModelComments>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ModelComments> call, @NonNull Response<ModelComments> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;

                    List<CommentItems> comments = response.body().getItems();


                    createFeedList(comments);
                } else if (response.code() == 429) {
                    // Handle unauthorized
                    toast("you are not authorized");
                } else {
                    toast("no success "+response.message());
                    Log.d("error_contact", response.code()+" ");
                    Log.d("error_contact", total_url);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelComments> call, @NonNull Throwable t) {
                Log.d("error_contact", t.toString());
                toast("no network");
            }
        });
    }

    public void createFeedList(List<CommentItems> comments){
        ArrayList<EntryCommentItem> menuItems = new ArrayList<>();

        String[] blockedWords = Account.blockedWords().split(",");
        String[] blockedUsers = Account.blockedUsers().split(",");
        for (CommentItems comment : comments){
            menuItems.add(new EntryCommentItem(comment));
        }
        build(menuItems);
    }

    private void build(ArrayList<EntryCommentItem> commentItems) {
        RecyclerView recyclerView = findViewById(R.id.comments_view);
        recyclerView.setHasFixedSize(false);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(EntryActivity.this);


        EntryCommentAdapter mAdapter = new EntryCommentAdapter(this, commentItems, new EntryCommentAdapter.AdapterCallback() {
            @Override
            public void onItemClicked(Integer menuPosition, String type) {
                EntryCommentItem menuItem = commentItems.get(menuPosition);
            }
        }) {
            @Override
            public void onItemClicked(Integer feedPosition) {

            }
        };

        recyclerView.setAlpha(0);
        recyclerView.setTranslationY(Tools.dpToPx(40));
        recyclerView.animate().alpha(1).translationY(0).setDuration(300).withLayer();
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);
    }

    public void refresh(View view) {
        requestComments();
    }

    public void sharePost(View view) {
        /*Create an ACTION_SEND Intent*/
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        /*The type of the content is text, obviously.*/
        intent.setType("text/plain");
        /*Applying information Subject and Body.*/
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "dR Bulletin Board");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "https://kbin.melroy.org/m/drbboard/t/"+selected_entry.getEntryId());
        /*Fire!*/
        startActivity(Intent.createChooser(intent, "dR Bulletin Board"));
    }
}