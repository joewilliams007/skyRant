package com.dev.engineerrant;

import static android.text.format.DateUtils.getRelativeTimeSpanString;
import static com.dev.engineerrant.EntryActivity.selected_entry;
import static com.dev.engineerrant.app.toast;
import static com.dev.engineerrant.auth.Account.vibrate;
import static com.dev.engineerrant.network.RetrofitClient.BASE_URL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.dev.engineerrant.adapters.FeedAdapter;
import com.dev.engineerrant.adapters.FeedItem;
import com.dev.engineerrant.adapters.FollowingItem;
import com.dev.engineerrant.adapters.MagazineAdapter;
import com.dev.engineerrant.adapters.MagazineItem;
import com.dev.engineerrant.adapters.UsersAdapter;
import com.dev.engineerrant.animations.Tools;
import com.dev.engineerrant.auth.Account;
import com.dev.engineerrant.classes.dev.Rants;
import com.dev.engineerrant.classes.kbin.Items;
import com.dev.engineerrant.network.RetrofitClient;
import com.dev.engineerrant.network.methods.dev.MethodsFeed;
import com.dev.engineerrant.network.methods.kbin.MethodsEntries;
import com.dev.engineerrant.network.models.dev.ModelFeed;
import com.dev.engineerrant.network.models.kbin.ModelEntries;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MagazineActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magazine);
        initialize();
        getMagazine();
    }

    private void initialize() {
        recyclerView = findViewById(R.id.recyclerView);
    }

    private void getMagazine() {
        MethodsEntries methods = RetrofitClient.getRetrofitInstance().create(MethodsEntries.class);
        String sort = "newest"; // oldest, hot, active, top, commented;
        String time = "%E2%88%9E";
        String p = "1";
        String perPage = "25";
        String usePreferredLangs = "false";
        String magazineId = "304";
        String total_url = "https://kbin.melroy.org/api/magazine/"+magazineId+"/entries?sort="+sort+"&p="+p+"&perPage="+perPage+"&usePreferredLangs="+usePreferredLangs+"&time="+time;


        Call<ModelEntries> call = methods.getAllData(total_url);
        call.enqueue(new Callback<ModelEntries>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ModelEntries> call, @NonNull Response<ModelEntries> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    List<Items> items = response.body().getItems();
                    //   toast("success: "+success+" size: "+rants.size());

                    createMagazineList(items);
                } else if (response.code() == 429) {
                    // Handle unauthorized
                    toast("you are not authorized");
                } else {
                   toast(response.code()+" ");
                }
                System.out.println(total_url);
            }

            @Override
            public void onFailure(@NonNull Call<ModelEntries> call, @NonNull Throwable t) {
                Log.d("error_contact", t.toString());
                toast("no network");
                System.out.println(total_url);
            }
        });
    }


    ArrayList<MagazineItem> menuItems;
    public void createMagazineList(List<Items> items){
        menuItems = new ArrayList<>();
        for (Items item : items){
                menuItems.add(new MagazineItem(item
                ));
            }
        build(menuItems);
    }



    private void build(ArrayList<MagazineItem> feedItems) {
        recyclerView.setHasFixedSize(false);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0,0);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MagazineActivity.this);

        MagazineAdapter mAdapter = new MagazineAdapter(this, feedItems, new MagazineAdapter.AdapterCallback() {
            @Override
            public void onItemClicked(Integer menuPosition) {
                MagazineItem menuItem = feedItems.get(menuPosition);
                selected_entry = menuItem.getItem();
                Intent intent = new Intent(MagazineActivity.this, EntryActivity.class);
                startActivity(intent);
            }
        }) {
            @Override
            public void onItemClicked(Integer feedPosition) {

            }
        };
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setAlpha(0);
        recyclerView.setTranslationY(Tools.dpToPx(40));
        recyclerView.animate().alpha(1).translationY(0).setDuration(300).withLayer();
    }
}