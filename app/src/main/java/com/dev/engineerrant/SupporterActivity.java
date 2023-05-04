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
import android.os.Bundle;
import android.util.Log;

import com.dev.engineerrant.adapters.SupporterAdapter;
import com.dev.engineerrant.adapters.SupporterItem;
import com.dev.engineerrant.animations.Tools;
import com.dev.engineerrant.classes.SupporterItems;
import com.dev.engineerrant.network.methods.MethodsSupporters;
import com.dev.engineerrant.network.models.ModelSupporters;
import com.dev.engineerrant.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SupporterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supporter);
        startReq();
    }

    private void startReq() {
        MethodsSupporters methods = RetrofitClient.getRetrofitInstance().create(MethodsSupporters.class);
        String total_url = BASE_URL
                + "devrant/supporters?app=3/";

        System.out.println(total_url);
        Call<ModelSupporters> call = methods.getAllData(total_url);
        call.enqueue(new Callback<ModelSupporters>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ModelSupporters> call, @NonNull Response<ModelSupporters> response) {
                if (response.isSuccessful()) {
                    // Do awesome stuff
                    assert response.body() != null;
                    List<SupporterItems> items = response.body().getItems();
                    createFeedList(items);
                } else if (response.code() == 429) {
                    // Handle unauthorized
                    toast(response.message());
                } else {
                    toast(response+" ");

                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelSupporters> call, @NonNull Throwable t) {
                Log.d("error_contact", t.toString());
                toast("no network");
            }
        });
    }


    public void createFeedList(List<SupporterItems> items){
        ArrayList<SupporterItem> menuItems = new ArrayList<>();

        for (SupporterItems item : items){
            menuItems.add(new SupporterItem(item));
        }
        build(menuItems);
    }

    private void build(ArrayList<SupporterItem> menuItems) {
        RecyclerView recyclerView = findViewById(R.id.supporter_view);
        recyclerView.setHasFixedSize(false);

        recyclerView.getRecycledViewPool().setMaxRecycledViews(0,0);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(SupporterActivity.this);


        SupporterAdapter mAdapter = new SupporterAdapter(this, menuItems, new SupporterAdapter.AdapterCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClicked(Integer menuPosition) {
                SupporterItem menuItem = menuItems.get(menuPosition);
                Intent intent = new Intent(SupporterActivity.this, ProfileActivity.class);
                intent.putExtra("user_id",String.valueOf(menuItem.getSupporterItems().getUser().getId()));
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
}