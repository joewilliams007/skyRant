package com.dev.engineerrant;

import static com.dev.engineerrant.app.toast;
import static com.dev.engineerrant.auth.Account.vibrate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.dev.engineerrant.adapters.ChangelogAdapter;
import com.dev.engineerrant.adapters.ChangelogItem;
import com.dev.engineerrant.animations.Tools;
import com.dev.engineerrant.classes.Changelog;
import com.dev.engineerrant.network.methods.MethodsUpdate;
import com.dev.engineerrant.network.models.ModelUpdate;
import com.dev.engineerrant.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangelogActivity extends AppCompatActivity {
    ProgressBar progressBar;
    RecyclerView recyclerView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changelog);
        recyclerView = findViewById(R.id.log_view);
        progressBar = findViewById(R.id.progressBar);
        getLogs();
    }

    private void getLogs() {
        MethodsUpdate methods = RetrofitClient.getRetrofitInstance().create(MethodsUpdate.class);
        String total_url = "https://raw.githubusercontent.com/joewilliams007/jsonapi/gh-pages/phoneclient.json";

        progressBar.setVisibility(View.VISIBLE);

        Call<ModelUpdate> call = methods.getAllData(total_url);
        call.enqueue(new Callback<ModelUpdate>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ModelUpdate> call, @NonNull Response<ModelUpdate> response) {
                if (response.isSuccessful()) {

                    // Do  awesome stuff
                    assert response.body() != null;

                    String version = response.body().getVersion();
                    int build = response.body().getBuild();
                    int versionCode = BuildConfig.VERSION_CODE;
                    List<Changelog> logs = response.body().getChangelog();

                    createList(logs);
                } else if (response.code() == 429) {
                    // Handle unauthorized
                    toast("error contacting github error 429");
                } else {
                    toast("error contacting github");
                }
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NonNull Call<ModelUpdate> call, @NonNull Throwable t) {
                Log.d("error_contact", t.toString());
                toast("no network");
            }
        });
    }

    public void createList(List<Changelog> items){
        ArrayList<ChangelogItem> menuItems = new ArrayList<>();

        for (Changelog item : items){
            menuItems.add(new ChangelogItem(item.getTimestamp()* 1000L,
                    item.getVersion(),
                    item.getBuild(),
                    item.getText()
            ));
        }
        build(menuItems);
    }

    private void build(ArrayList<ChangelogItem> menuItems) {
        recyclerView.setHasFixedSize(false);

        recyclerView.getRecycledViewPool().setMaxRecycledViews(0,0);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ChangelogActivity.this);


        ChangelogAdapter mAdapter = new ChangelogAdapter(this, menuItems, new ChangelogAdapter.AdapterCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClicked(Integer menuPosition) {
                ChangelogItem menuItem = menuItems.get(menuPosition);
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
}