package com.dev.engineerrant.adapters;

import static com.dev.engineerrant.BuilderActivity.prev_selected;
import static com.dev.engineerrant.BuilderActivity.score;
import static com.dev.engineerrant.BuilderActivity.selected;
import static com.dev.engineerrant.app.toast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dev.engineerrant.R;
import com.dev.engineerrant.auth.MyApplication;

import java.util.ArrayList;

public abstract class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.RecyclerViewHolder> {

    private ArrayList<FollowingItem> dataSource = new ArrayList<FollowingItem>();

    public abstract void onItemClicked(Integer feedPosition);

    public interface AdapterCallback{
        void onItemClicked(Integer menuPosition,Boolean profile);
    }
    private final AdapterCallback callback;

    private String drawableIcon;
    private final Context context;


    public UsersAdapter(Context context, ArrayList<FollowingItem> dataArgs, AdapterCallback callback){
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
        FollowingItem data_provider = dataSource.get(position);

        holder.textViewUsername.setText(data_provider.getUsername());

        holder.imageViewProfile.setImageDrawable(null);

        if (!data_provider.getAvatar().equals("null")) {
            Glide.with(MyApplication.getAppContext()).load("https://avatars.devrant.com/"+data_provider.getAvatar()).into(holder.imageViewProfile);
        }

        if (data_provider.getId().equals("0")) {
            holder.imageViewProfile.setImageDrawable(ContextCompat.getDrawable(MyApplication.getAppContext(), R.drawable.stress_ball));
        } else {
            holder.imageViewProfile.setBackgroundColor(Color.parseColor(data_provider.getColor()));
        }


        holder.avatarsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(callback != null) {
                        callback.onItemClicked(position, false);
                }

            }
        });

        holder.avatarsLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(callback != null) {
                        callback.onItemClicked(position, true);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }
}

