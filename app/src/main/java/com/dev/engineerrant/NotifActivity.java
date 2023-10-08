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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dev.engineerrant.adapters.NotifAdapter;
import com.dev.engineerrant.adapters.NotifItem;
import com.dev.engineerrant.animations.Tools;
import com.dev.engineerrant.auth.Account;
import com.dev.engineerrant.classes.dev.NotifItems;
import com.dev.engineerrant.classes.dev.NotifUnread;
import com.dev.engineerrant.network.methods.dev.MethodsNotif;
import com.dev.engineerrant.network.models.dev.ModelNotif;
import com.dev.engineerrant.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotifActivity extends AppCompatActivity {

    TextView stats;
    ImageView imageViewRefresh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif);
        initialize();
        startReq();
    }

    private void initialize() {
        stats = findViewById(R.id.stats);
        imageViewRefresh = findViewById(R.id.imageViewRefresh);
    }

    private void startReq() {
        MethodsNotif methods = RetrofitClient.getRetrofitInstance().create(MethodsNotif.class);
        String total_url = BASE_URL
                + "users/me/notif-feed?"+"token_id="+ Account.id()+"&token_key="+Account.key()+"&user_id="+Account.user_id()+"&app=3"+"/";

        System.out.println(total_url);
        Call<ModelNotif> call = methods.getAllData(total_url);
        call.enqueue(new Callback<ModelNotif>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ModelNotif> call, @NonNull Response<ModelNotif> response) {
                if (response.isSuccessful()) {

                    // Do awesome stuff
                    assert response.body() != null;
                    Boolean success = response.body().getSuccess();
                    List<NotifItems> items = response.body().getData().getItems();
                    NotifUnread unread = response.body().getData().getUnread();
                    Map<Integer, String> username_map = response.body().getData().getUsername_map();
                    createFeedList(items, unread, username_map);
                    imageViewRefresh.setVisibility(View.VISIBLE);

                    String text = ">"+unread.getTotal()+" notifications";
                    String[] t = text.split("");
                    text = "";
                    for (String l:t) {
                        text+=l+" ";
                    }
                    stats.setText(text);
                } else if (response.code() == 429) {
                    // Handle unauthorized
                    toast(response.message());
                } else {
                    toast(response+" ");

                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelNotif> call, @NonNull Throwable t) {
                Log.d("error_contact", t.toString());
                toast("no network");
            }
        });
    }


    public void createFeedList(List<NotifItems> items, NotifUnread unread, Map<Integer, String> username_map){
        ArrayList<NotifItem> menuItems = new ArrayList<>();

        int all = unread.getAll();
        int upvotes = unread.getUpvotes();
        int mentions = unread.getMentions();
        int comments = unread.getComments();
        int subs = unread.getSubs();
        int total = unread.getTotal();


        /*menuItems.add(new NotifItem(0,"all",all,0,0,"all "+all
                +"\nupvotes "+upvotes
                +"\nmentions "+mentions
                +"\ncomments "+comments
                +"\nsubs "+subs));*/

        for (NotifItems item : items){
            menuItems.add(new NotifItem(item.getCreated_time()* 1000L,
                    item.getType(),
                    item.getRead(),
                    item.getRant_id(),
                    item.getUid(),
                    username_map.get(item.getUid())));
        }
        build(menuItems);
    }

    private void build(ArrayList<NotifItem> menuItems) {
        RecyclerView recyclerView = findViewById(R.id.notif_view);
        recyclerView.setHasFixedSize(false);

        recyclerView.getRecycledViewPool().setMaxRecycledViews(0,0);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(NotifActivity.this);


        NotifAdapter mAdapter = new NotifAdapter(this, menuItems, new NotifAdapter.AdapterCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClicked(Integer menuPosition) {
                NotifItem menuItem = menuItems.get(menuPosition);
                if (!menuItem.getType().equals("all")) {
                    Intent intent = new Intent(NotifActivity.this, RantActivity.class);
                    intent.putExtra("id",String.valueOf(menuItem.getRant_id()));
                    intent.putExtra("info","false");
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
    }

    public void refresh(View view) {
        imageViewRefresh.setVisibility(View.GONE);
        int red = new Random().nextInt(255);
        int green = new Random().nextInt(255);
        int blue = new Random().nextInt(255);
        ThemeColors.setNewThemeColor(NotifActivity.this, red, green, blue);
    }
}