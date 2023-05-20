package com.dev.engineerrant.adapters;

import static com.dev.engineerrant.FollowingActivity.showFollowingRemoveBtn;

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

public abstract class ReactionAdapter extends RecyclerView.Adapter<ReactionAdapter.RecyclerViewHolder> {

    private ArrayList< ReactionItem> dataSource = new ArrayList< ReactionItem>();

    public abstract void onItemClicked(Integer feedPosition);

    public interface AdapterCallback{
        void onItemClicked(Integer menuPosition, String open);
    }
    private final AdapterCallback callback;

    private String drawableIcon;
    private final Context context;


    public ReactionAdapter(Context context, ArrayList< ReactionItem> dataArgs, AdapterCallback callback){
        this.context = context;
        this.dataSource = dataArgs;
        this.callback = callback;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reaction_item,parent,false);
        return new RecyclerViewHolder(view);
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder
    {

        ImageView imageViewProfile;
        TextView textViewUsername,textViewReaction;
        ConstraintLayout constraintLayout;
        public RecyclerViewHolder(View view) {
            super(view);
            imageViewProfile = view.findViewById(R.id.imageViewProfile);
            textViewUsername = view.findViewById(R.id.textViewUsername);
            textViewReaction = view.findViewById(R.id.textViewReaction);
            constraintLayout = view.findViewById(R.id.item);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        ReactionItem data_provider = dataSource.get(position);
        holder.imageViewProfile.setImageDrawable(null);
        if (data_provider.getAvatar()!=null && !data_provider.getAvatar().equals("")) {
            Glide.with(MyApplication.getAppContext()).load("https://avatars.devrant.com/"+data_provider.getAvatar()).into(holder.imageViewProfile);
        }
        holder.textViewUsername.setText(data_provider.getUsername());
        try {
            holder.imageViewProfile.setBackgroundColor(Color.parseColor(data_provider.getColor()));
        } catch (Exception e) {
            holder.textViewUsername.setText(data_provider.getUsername()+"\nINVALID HEX COLOR");
        }

        holder.textViewReaction.setText(data_provider.getReaction());

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(callback != null) {
                    callback.onItemClicked(position,"open");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }
}

