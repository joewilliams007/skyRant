package com.dev.engineerrant;

import static com.dev.engineerrant.auth.Account.vibrate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.dev.engineerrant.adapters.FollowingAdapter;
import com.dev.engineerrant.adapters.FollowingItem;
import com.dev.engineerrant.adapters.ReactionAdapter;
import com.dev.engineerrant.adapters.ReactionItem;
import com.dev.engineerrant.animations.Tools;
import com.dev.engineerrant.auth.Account;
import com.dev.engineerrant.classes.sky.Reactions;

import java.util.ArrayList;
import java.util.List;

public class ReactionActivity extends AppCompatActivity {

    public static List<Reactions> reactions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reaction);
        createList();
    }

    public void createList(){
        ArrayList<ReactionItem> menuItems = new ArrayList<>();

        try {
            for (Reactions item : reactions){
                menuItems.add(new ReactionItem(
                        String.valueOf(item.getUser_id()),
                        item.getUsername(),
                        "#"+item.getColor(),
                        item.getAvatar(),
                        item.getReaction(),
                        String.valueOf(item.getPost_id())
                ));
            }
        } catch (Exception ignored) {

        }
        build(menuItems);
    }
    ReactionAdapter mAdapter;
    private void build(ArrayList<ReactionItem> menuItems) {
        RecyclerView recyclerView = findViewById(R.id.reactions_view);
        recyclerView.setHasFixedSize(false);

        recyclerView.getRecycledViewPool().setMaxRecycledViews(0,0);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ReactionActivity.this);


        mAdapter = new ReactionAdapter(this, menuItems, new ReactionAdapter.AdapterCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClicked(Integer menuPosition, String open) {
                ReactionItem menuItem = menuItems.get(menuPosition);
                        Intent intent = new Intent(ReactionActivity.this, ProfileActivity.class);
                        intent.putExtra("user_id",String.valueOf(menuItem.getId()));
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