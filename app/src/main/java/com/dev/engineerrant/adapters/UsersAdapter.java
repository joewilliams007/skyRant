package com.dev.engineerrant.adapters;

import static com.dev.engineerrant.BuilderActivity.prev_selected;
import static com.dev.engineerrant.BuilderActivity.score;
import static com.dev.engineerrant.BuilderActivity.selected;
import static com.dev.engineerrant.app.toast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dev.engineerrant.R;
import com.dev.engineerrant.auth.MyApplication;

import java.util.ArrayList;

public abstract class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.RecyclerViewHolder> {

    private ArrayList<UsersItem> dataSource = new ArrayList<UsersItem>();

    public abstract void onItemClicked(Integer feedPosition);

    public interface AdapterCallback{
        void onItemClicked(Integer menuPosition);
    }
    private final AdapterCallback callback;

    private String drawableIcon;
    private final Context context;


    public UsersAdapter(Context context, ArrayList<UsersItem> dataArgs, AdapterCallback callback){
        this.context = context;
        this.dataSource = dataArgs;
        this.callback = callback;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item,parent,false);
        return new RecyclerViewHolder(view);
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder
    {

        TextView textViewScore, textViewUsername;
        ImageView imageViewProfile;
        ConstraintLayout avatarsLayout;
        public RecyclerViewHolder(View view) {
            super(view);
            textViewScore = view.findViewById(R.id.textViewScore);
            imageViewProfile = view.findViewById(R.id.imageViewProfile);
            avatarsLayout = view.findViewById(R.id.avatarsLayout);
            textViewUsername = view.findViewById(R.id.textViewUsername);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        UsersItem data_provider = dataSource.get(position);

        holder.textViewUsername.setText(data_provider.getProfile().getUsername());

        // This code works, but i need to make it less resource demanding in order to be smooth. at least async
       /* holder.imageViewProfile.setImageDrawable(null);
        if (data_provider.getProfile().getAvatar().getI()!=null) {
            Glide.with(MyApplication.getAppContext()).load("https://avatars.devrant.com/"+data_provider.getProfile().getAvatar().getI()).into(holder.imageViewProfile);
        } else {
            holder.imageViewProfile.setBackgroundColor(Color.parseColor("#"+data_provider.getProfile().getAvatar().getB()));
        }

        if (data_provider.getProfile().getScore()<0) {
            holder.textViewScore.setText(String.valueOf(data_provider.getProfile().getScore()));
        } else {
            holder.textViewScore.setText("+"+data_provider.getProfile().getScore());
        } */

        holder.avatarsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(callback != null) {
                    if (selected != position) {
                        callback.onItemClicked(position);
                    }
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }
}

