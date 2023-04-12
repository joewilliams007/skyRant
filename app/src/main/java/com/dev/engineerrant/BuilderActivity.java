package com.dev.engineerrant;

import static android.text.format.DateUtils.getRelativeTimeSpanString;
import static com.dev.engineerrant.animations.Tools.isNumeric;
import static com.dev.engineerrant.app.hideKeyboard;
import static com.dev.engineerrant.app.toast;
import static com.dev.engineerrant.auth.Account.vibrate;
import static com.dev.engineerrant.network.RetrofitClient.BASE_URL;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dev.engineerrant.adapters.AvatarsAdapter;
import com.dev.engineerrant.adapters.AvatarsItem;
import com.dev.engineerrant.adapters.FeedItem;
import com.dev.engineerrant.adapters.NotifAdapter;
import com.dev.engineerrant.adapters.NotifItem;
import com.dev.engineerrant.animations.RantLoadingAnimation;
import com.dev.engineerrant.animations.Tools;
import com.dev.engineerrant.auth.Account;
import com.dev.engineerrant.auth.MyApplication;
import com.dev.engineerrant.classes.Avatars;
import com.dev.engineerrant.classes.Counts;
import com.dev.engineerrant.classes.NotifItems;
import com.dev.engineerrant.classes.Options;
import com.dev.engineerrant.classes.Rants;
import com.dev.engineerrant.classes.User_avatar;
import com.dev.engineerrant.methods.MethodsBuilder;
import com.dev.engineerrant.methods.MethodsFeed;
import com.dev.engineerrant.methods.MethodsProfile;
import com.dev.engineerrant.models.ModelBuilder;
import com.dev.engineerrant.models.ModelFeed;
import com.dev.engineerrant.models.ModelProfile;
import com.dev.engineerrant.models.ModelSuccess;
import com.dev.engineerrant.network.DownloadImageTask;
import com.dev.engineerrant.network.DownloadImageTaskProgress;
import com.dev.engineerrant.network.RetrofitClient;
import com.dev.engineerrant.post.BuildClient;
import com.dev.engineerrant.post.CommentClient;
import com.paulrybitskyi.valuepicker.ValuePickerView;
import com.paulrybitskyi.valuepicker.model.Item;
import com.paulrybitskyi.valuepicker.model.PickerItem;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BuilderActivity extends AppCompatActivity {
    ValuePickerView valuePickerView;
    TextView textViewUsername, textViewScore;
    ImageView imageViewAvatar;
    String profile_image_url = null;
    ConstraintLayout back;
    ProgressBar progressBar;
    public static int score;
    public static int selected;
    public static int prev_selected;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_builder);
        valuePickerView = findViewById(R.id.valuePickerView);
        imageViewAvatar = findViewById(R.id.imageViewAvatar);
        textViewUsername = findViewById(R.id.textViewUsername);
        textViewScore = findViewById(R.id.textViewScore);
        progressBar = findViewById(R.id.progressBar);
        back = findViewById(R.id.back);
        requestProfile();

        /*if(Tools.writeStringAsFile("Hello world","avatars")) {
            toast(Tools.readFileAsString(
                    "avatars"
            ));
        }*/


        valuePickerView.setOnItemSelectedListener((item) -> {
            // Do something with item
            requestItems(item.getPayload());
        });


    }

    private void requestItems(Object payload) {
        progressBar.setVisibility(View.VISIBLE);
        Options option = (Options) payload;

        try {
            request((String) option.getId(),option.getLabel());
        } catch (Exception e) {
            request(String.valueOf((double) option.getId()).split("\\.")[0],option.getLabel());
        }

    }

    private void request(String option, String sub_option) {
        progressBar.setVisibility(View.VISIBLE);
        MethodsBuilder methods = RetrofitClient.getRetrofitInstance().create(MethodsBuilder.class);
        String total_url;
        total_url = BASE_URL
                    + "devrant/avatars/build?token_id="+Account.id()+"&user_id="+Account.user_id()+"&token_key="+Account.key()+
                "&option="+option+"&sub_option="+sub_option+"&app=3&image_id=https%3A%2F%2Favatars.devrant.com%2F"+profile_image_url;



        Call<ModelBuilder> call = methods.getAllData(total_url);
        call.enqueue(new Callback<ModelBuilder>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ModelBuilder> call, @NonNull Response<ModelBuilder> response) {
                if (response.isSuccessful()) {

                    // Do awesome stuff
                    assert response.body() != null;
                    Boolean success = response.body().getSuccess();
                    List<Options> options = response.body().getOptions();
                    //   toast("success: "+success+" size: "+rants.size());
                    selected = 0;
                    createList(response.body().getAvatars());
                    valuePickerView.setItems(getPickerItems(options));


                } else if (response.code() == 429) {
                    // Handle unauthorized
                    toast("you are not authorized");
                } else {
                    toast("please logout and in again - "+response.message());
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<ModelBuilder> call, @NonNull Throwable t) {
                Log.d("error_contact", t.toString());
                toast("no network");
                progressBar.setVisibility(View.GONE);
            }
        });


    }

    private void createList(List<Avatars> avatars) {
        ArrayList<AvatarsItem> menuItems = new ArrayList<>();

        for (Avatars item : avatars){
            menuItems.add(new AvatarsItem(item));
        }
        build(menuItems);
    }

    private void build(ArrayList<AvatarsItem> menuItems) {
        RecyclerView recyclerView = findViewById(R.id.builder_view);
        recyclerView.setHasFixedSize(false);

        recyclerView.getRecycledViewPool().setMaxRecycledViews(0,0);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(BuilderActivity.this,LinearLayoutManager.HORIZONTAL,false);


        AvatarsAdapter mAdapter = new AvatarsAdapter(this, menuItems, new AvatarsAdapter.AdapterCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClicked(Integer menuPosition) {
                AvatarsItem menuItem = menuItems.get(menuPosition);
                profile_image_url = menuItem.getAvatars().getImg().getFull();
                progressBar.setVisibility(View.VISIBLE);
                imageViewAvatar.setImageDrawable(null);
                back.setBackgroundColor(Color.parseColor("#"+menuItem.getAvatars().getImg().getB()));
                new DownloadImageTaskProgress(imageViewAvatar, progressBar)
                        .execute("https://avatars.devrant.com/"+menuItem.getAvatars().getImg().getFull());
                imageViewAvatar.setBackgroundColor(Color.parseColor("#"+menuItem.getAvatars().getImg().getB()));
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

    private ArrayList<Item> getPickerItems(List<Options> options) {
        final ArrayList<Item> pickerItems = new ArrayList<>(100);

        int i = 0;
        for (Options option : options) {
            pickerItems.add(new PickerItem(i, option.getLabel(),option));
            i++;
        }
        // valuePickerView.setSelectedItem(pickerItems.get(0));

        return pickerItems;
    }

    private void requestProfile() {
        progressBar.setVisibility(View.VISIBLE);
        MethodsProfile methods = RetrofitClient.getRetrofitInstance().create(MethodsProfile.class);
        String total_url;
        total_url = BASE_URL + "users/"+Account.user_id()+"?app=3&token_id="+Account.id()+"&token_key="+Account.key()+"&user_id="+Account.user_id();

        Call<ModelProfile> call = methods.getAllData(total_url);
        call.enqueue(new Callback<ModelProfile>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ModelProfile> call, @NonNull Response<ModelProfile> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;

                    String user_avatar = response.body().getProfile().getAvatar().getI();
                    String username = response.body().getProfile().getUsername();
                    score = response.body().getProfile().getScore();
                    profile_image_url = user_avatar;
                    textViewUsername.setText(username);
                    new DownloadImageTaskProgress(imageViewAvatar, progressBar)
                            .execute("https://avatars.devrant.com/"+profile_image_url);
                    back.setBackgroundColor(Color.parseColor("#"+response.body().getProfile().getAvatar().getB()));

                    if (score < 0) {
                        textViewScore.setText(String.valueOf(score));
                    } else {
                        textViewScore.setText("+"+score);
                    }
                    request("g","");
                } else if (response.code() == 429) {
                    // Handle unauthorized
                } else {
                    toast(response.message());
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<ModelProfile> call, @NonNull Throwable t) {
                Log.d("error_contact", t.toString());
                toast(t.toString());
                request("g","");
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void saveAvatarBtn(View view) {
            try {
                RequestBody app = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), "3");
                RequestBody image_id = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), profile_image_url);
                RequestBody token_id = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), String.valueOf(Account.id()));
                RequestBody token_key = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), Account.key());
                RequestBody user_id = RequestBody.create(MediaType.parse("application/x-form-urlencoded"), String.valueOf(Account.user_id()));

                Retrofit.Builder builder = new Retrofit.Builder().baseUrl(BASE_URL + "users/me/").addConverterFactory(GsonConverterFactory.create());
                Retrofit retrofit = builder.build();

                BuildClient client = retrofit.create(BuildClient.class);
                // finally, execute the request

                Call<ModelSuccess> call = client.upload(app, image_id, token_id, token_key, user_id);
                call.enqueue(new Callback<ModelSuccess>() {
                    @Override
                    public void onResponse(@NonNull Call<ModelSuccess> call, @NonNull Response<ModelSuccess> response) {
                        Log.v("Upload", response + " ");

                        if (response.isSuccessful()) {
                            // Do awesome stuff
                            assert response.body() != null;
                            Boolean success = response.body().getSuccess();

                            if (success) {
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
                    }

                });
            } catch (Exception e) {

            }
        }

    public void discardAvatarBtn(View view) {
        finish();
    }
}