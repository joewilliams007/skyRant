package com.dev.engineerrant;

import static com.dev.engineerrant.app.toast;
import static com.dev.engineerrant.auth.Account.vibrate;
import static com.dev.engineerrant.network.RetrofitClient.BASE_URL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.dev.engineerrant.adapters.FollowingAdapter;
import com.dev.engineerrant.adapters.FollowingItem;
import com.dev.engineerrant.adapters.SupporterAdapter;
import com.dev.engineerrant.adapters.SupporterItem;
import com.dev.engineerrant.animations.Tools;
import com.dev.engineerrant.auth.Account;
import com.dev.engineerrant.classes.SupporterItems;
import com.dev.engineerrant.network.RetrofitClient;
import com.dev.engineerrant.network.methods.MethodsSupporters;
import com.dev.engineerrant.network.models.ModelSupporters;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowingActivity extends AppCompatActivity {

    EditText editTextFollowing;
    ConstraintLayout edit;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);
        edit = findViewById(R.id.edit);
        editTextFollowing = findViewById(R.id.editTextFollowing);

        edit.setVisibility(View.GONE);
        if (Account.following()!=null) {
            createList();
        }
    }

    public void createList(){
        ArrayList<FollowingItem> menuItems = new ArrayList<>();

        String[] following = Account.following().split("\n");
        try {
        for (String item : following){
            String[] args = item.split(" ");
            menuItems.add(new FollowingItem(
                    args[0],
                    args[1],
                    args[2],
                    args[3]
            ));
        }
        } catch (Exception e) {

        }
        build(menuItems);
    }

    private void build(ArrayList<FollowingItem> menuItems) {
        RecyclerView recyclerView = findViewById(R.id.following_view);
        recyclerView.setHasFixedSize(false);

        recyclerView.getRecycledViewPool().setMaxRecycledViews(0,0);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(FollowingActivity.this);


        FollowingAdapter mAdapter = new FollowingAdapter(this, menuItems, new FollowingAdapter.AdapterCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClicked(Integer menuPosition) {
                FollowingItem menuItem = menuItems.get(menuPosition);
                Intent intent = new Intent(FollowingActivity.this, ProfileActivity.class);
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

    public void saveFollowing(View view) {
        if (editTextFollowing.getText().length()==0) {
            Account.setFollowing(null);
        } else {
            Account.setFollowing(editTextFollowing.getText().toString());
        }
        toast("saved");
        edit.setVisibility(View.GONE);
        if (Account.following()!=null) {
            createList();
        } else {
            ArrayList<FollowingItem> menuItems = new ArrayList<>();
            build(menuItems);
        }
        app.hideKeyboard(FollowingActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        editTextFollowing.setText(Account.following());
        if (Account.following()!=null) {
            createList();
        } else {
            ArrayList<FollowingItem> menuItems = new ArrayList<>();
            build(menuItems);
        }
    }

    public void showEdit(View view) {
        editTextFollowing.setText(Account.following());
        if (edit.getVisibility() == View.VISIBLE) {
            edit.setVisibility(View.GONE);
            app.hideKeyboard(FollowingActivity.this);
        } else {
            edit.setVisibility(View.VISIBLE);
        }
    }
}