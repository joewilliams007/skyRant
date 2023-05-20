package com.dev.engineerrant;

import static com.dev.engineerrant.CommunityPostActivity.communityItem;
import static com.dev.engineerrant.app.toast;
import static com.dev.engineerrant.auth.Account.vibrate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dev.engineerrant.adapters.CommunityAdapter;
import com.dev.engineerrant.adapters.CommunityItem;
import com.dev.engineerrant.adapters.CommunityMenuAdapter;
import com.dev.engineerrant.adapters.CommunityMenuItem;
import com.dev.engineerrant.animations.Tools;
import com.dev.engineerrant.classes.dev.Projects;
import com.dev.engineerrant.network.methods.git.MethodsCommunity;
import com.dev.engineerrant.network.models.git.ModelCommunity;
import com.dev.engineerrant.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommunityActivity extends AppCompatActivity {
    ProgressBar progressBar;
    RecyclerView recyclerView;
    List<Projects> logs;
    String type = "all";
    TextView filter;
    String os = "all";
    SwitchCompat switchCompat;

    public static int os_menu_selected;
    public static int os_menu_prev_selected;
    public static int type_menu_selected;
    public static int type_menu_prev_selected;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);
        recyclerView = findViewById(R.id.community_view);
        progressBar = findViewById(R.id.progressBar);
        filter = findViewById(R.id.filter);
        switchCompat = findViewById(R.id.switchActive);
        filter.setText("");
        getLogs();
    }



    private void getLogs() {
        MethodsCommunity methods = RetrofitClient.getRetrofitInstance().create(MethodsCommunity.class);
        String total_url = "https://raw.githubusercontent.com/joewilliams007/jsonapi/gh-pages/community.json";

        progressBar.setVisibility(View.VISIBLE);
        switchCompat.setVisibility(View.GONE);

        Call<ModelCommunity> call = methods.getAllData(total_url);
        call.enqueue(new Callback<ModelCommunity>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ModelCommunity> call, @NonNull Response<ModelCommunity> response) {
                if (response.isSuccessful()) {

                    // Do  awesome stuff
                    assert response.body() != null;

                    long last_updated = response.body().getLast_updated();
                    logs = response.body().getProjects();

                    if (communityMenuItems==null) {
                        createList();
                    }
                } else if (response.code() == 429) {
                    // Handle unauthorized
                    toast("error contacting github error 429");
                } else {
                    toast("error contacting github");
                }
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NonNull Call<ModelCommunity> call, @NonNull Throwable t) {
                Log.d("error_contact", t.toString());
                toast("no network");
            }
        });
    }
    ArrayList<CommunityItem> menuItems;
    public void createList(){
        progressBar.setVisibility(View.VISIBLE);
        switchCompat.setVisibility(View.GONE);
        filter.setText("");
        menuItems = new ArrayList<>();

        SwitchCompat switchCompat = findViewById(R.id.switchActive);
        boolean active = switchCompat.isChecked();

        ArrayList<String> __os = new ArrayList<>();
        ArrayList<String> __type = new ArrayList<>();
        __os.add("all");
        __type.add("all");
        // Some os types may include multiple Os. They are separated with a comma.
        for (Projects item : logs){

            for (String _type: item.getType().split(",")) {
                if (!Objects.equals(_type, "")) {
                    if (!__type.contains(_type)) {
                        __type.add(_type);
                    }
                }
            }

            for (String _os: item.getOs().split(",")) {
                if (!Objects.equals(_os, "")) {
                    if (!__os.contains(_os)) {
                        __os.add(_os);
                    }
                }
            }

            if (active) {
                if (item.getActive()) {
                    sortType(item);
                }
            } else {
                sortType(item);
            }
        }


        if (communityMenuItems==null) {
            createMenuList(__os);
            createMenuListType(__type);
        }

        build(menuItems);
    }

    ArrayList<CommunityMenuItem> communityMenuItems = null;
    private void createMenuList(ArrayList<String> _strings) {
        communityMenuItems = new ArrayList<>();
        for (String str: _strings) {
            communityMenuItems.add(new CommunityMenuItem(str,"os"));
        }

        buildCommunityMenu(communityMenuItems);
    }

    ArrayList<CommunityMenuItem> communityMenuItemsType = null;
    private void createMenuListType(ArrayList<String> _strings) {
        communityMenuItemsType = new ArrayList<>();
        for (String str: _strings) {
            communityMenuItemsType.add(new CommunityMenuItem(str,"type"));
        }

        buildCommunityMenuType(communityMenuItemsType);
    }

    private void buildCommunityMenuType(ArrayList<CommunityMenuItem> menuItems) {
        RecyclerView recyclerView = findViewById(R.id.type_menu_view);
        recyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(CommunityActivity.this,LinearLayoutManager.HORIZONTAL,false);
        CommunityMenuAdapter mAdapter = new CommunityMenuAdapter(this, menuItems, new CommunityMenuAdapter.AdapterCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClicked(Integer menuPosition) {
                CommunityMenuItem menuItem = communityMenuItemsType.get(menuPosition);
                progressBar.setVisibility(View.VISIBLE);
                type = menuItem.getItem();
                createList();
            }
        }) {
            @Override
            public void onItemClicked(Integer feedPosition) {

            }
        };

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);
    }

    private void buildCommunityMenu(ArrayList<CommunityMenuItem> menuItems) {
        RecyclerView recyclerView = findViewById(R.id.os_menu_view);
        recyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(CommunityActivity.this,LinearLayoutManager.HORIZONTAL,false);
        CommunityMenuAdapter mAdapter = new CommunityMenuAdapter(this, menuItems, new CommunityMenuAdapter.AdapterCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClicked(Integer menuPosition) {
                CommunityMenuItem menuItem = communityMenuItems.get(menuPosition);
                progressBar.setVisibility(View.VISIBLE);
                os = menuItem.getItem();
                createList();
            }
        }) {
            @Override
            public void onItemClicked(Integer feedPosition) {

            }
        };

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);
    }

    private void addItem(Projects item) {
        menuItems.add(new CommunityItem(item.getTitle(),
                item.getOs(),
                item.getType(),
                item.getTimestamp_added(),
                item.getDesc(),
                item.getRelevant_dr_url(),
                item.getWebsite(),
                item.getGithub(),
                item.getLanguage(),
                item.getActive()
        ));

    }

    private void sortType(Projects item) {
        if (type.equals("all")) {
            sortOs(item);
        } else if (item.getType().contains(type)) {
            sortOs(item);
        }
    }

    private void sortOs(Projects item) {
        if (os.equals("all")) {
            addItem(item);
        } else if (item.getOs().toLowerCase().contains(os.toLowerCase())) {
            addItem(item);
        }
    }

    private void build(ArrayList<CommunityItem> menuItems) {
        recyclerView.setHasFixedSize(false);

        recyclerView.getRecycledViewPool().setMaxRecycledViews(0,0);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(CommunityActivity.this);

        filter.setText(String.valueOf(menuItems.size()+" results"));


        CommunityAdapter mAdapter = new CommunityAdapter(this, menuItems, new CommunityAdapter.AdapterCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClicked(Integer menuPosition) {
                CommunityItem menuItem = menuItems.get(menuPosition);
                if (!menuItem.getGithub().equals("") && !menuItem.getGithub().contains("gist")){
                    Intent intent = new Intent(CommunityActivity.this, CommunityPostActivity.class);
                    communityItem = menuItem;
                    startActivity(intent);
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


        switchCompat.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    public void switchActive(View view) {
        createList();
    }
}