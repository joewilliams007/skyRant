package com.dev.engineerrant;

import static com.dev.engineerrant.app.toast;
import static com.dev.engineerrant.auth.Account.vibrate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.dev.engineerrant.adapters.FollowingAdapter;
import com.dev.engineerrant.adapters.FollowingItem;
import com.dev.engineerrant.animations.Tools;
import com.dev.engineerrant.auth.Account;

import java.util.ArrayList;

public class FollowingActivity extends AppCompatActivity {

    EditText editTextFollowing;
    ConstraintLayout edit;
    public static Boolean showFollowingRemoveBtn = false;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);
        edit = findViewById(R.id.edit);
        editTextFollowing = findViewById(R.id.editTextFollowing);
        showFollowingRemoveBtn = false;
        edit.setVisibility(View.GONE);
        if (Account.following()!=null) {
            createList();
        }
    }
    public void showEditBtn(View view) {
        showFollowingRemoveBtn = !showFollowingRemoveBtn;
        if (Account.following()!=null) {
            createList();
        } else {
            ArrayList<FollowingItem> menuItems = new ArrayList<>();
            build(menuItems);
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
    FollowingAdapter mAdapter;
    private void build(ArrayList<FollowingItem> menuItems) {
        RecyclerView recyclerView = findViewById(R.id.following_view);
        recyclerView.setHasFixedSize(false);

        recyclerView.getRecycledViewPool().setMaxRecycledViews(0,0);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(FollowingActivity.this);


        mAdapter = new FollowingAdapter(this, menuItems, new FollowingAdapter.AdapterCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClicked(Integer menuPosition, String open) {
                FollowingItem menuItem = menuItems.get(menuPosition);
                switch (open) {
                    case "unfollow":
                        Account.unfollow(menuItem.getId());
                        if (Account.following()!=null) {
                            createList();
                        } else {
                            ArrayList<FollowingItem> menuItems = new ArrayList<>();
                            build(menuItems);
                        }
                        break;
                    default:
                        Intent intent = new Intent(FollowingActivity.this, ProfileActivity.class);
                        intent.putExtra("user_id",String.valueOf(menuItem.getId()));
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
            app.hideKeyboard(FollowingActivity.this);
            edit.setVisibility(View.GONE);
        } else {
            edit.setVisibility(View.VISIBLE);
        }
    }

    public void copyFollowing(View view) {
        if (Account.following()!=null && !Account.following().equals("")) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip;
            clip = ClipData.newPlainText("following", Account.following());
            clipboard.setPrimaryClip(clip);
        } else {
            toast("nothing to copy");
        }

    }


}