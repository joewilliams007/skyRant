package com.dev.engineerrant.adapters;

import static android.text.format.DateUtils.getRelativeTimeSpanString;

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
import com.dev.engineerrant.network.DownloadImageTask;

import java.util.ArrayList;

public abstract class NotifAdapter extends RecyclerView.Adapter<NotifAdapter.RecyclerViewHolder> {

    private ArrayList<NotifItem> dataSource = new ArrayList<NotifItem>();

    public abstract void onItemClicked(Integer feedPosition);

    public interface AdapterCallback{
        void onItemClicked(Integer menuPosition);
    }
    private final AdapterCallback callback;

    private String drawableIcon;
    private final Context context;


    public NotifAdapter(Context context, ArrayList<NotifItem> dataArgs, AdapterCallback callback){
        this.context = context;
        this.dataSource = dataArgs;
        this.callback = callback;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notif_item,parent,false);
        return new RecyclerViewHolder(view);
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder
    {

        ImageView imageViewProfile;
        TextView textViewText, textViewDate;
        ConstraintLayout constraintLayout;
        public RecyclerViewHolder(View view) {
            super(view);
            imageViewProfile = view.findViewById(R.id.imageViewProfile);
            textViewText = view.findViewById(R.id.textViewText);
            constraintLayout = view.findViewById(R.id.item);
            textViewDate = view.findViewById(R.id.textViewDate);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        NotifItem data_provider = dataSource.get(position);

        // holder.setIsRecyclable(false);
        holder.textViewDate.setText(getRelativeTimeSpanString(data_provider.getCreated_time()));

        switch (data_provider.getType()) {
            case "all":
                holder.textViewText.setText(data_provider.getUsername());
                holder.textViewDate.setVisibility(View.GONE);
                holder.textViewText.setBackground(null);
                break;
            case "comment_vote":
                holder.textViewText.setText(data_provider.getUsername()+" ++'d your comment!");
                break;
            case "comment_content":
                holder.textViewText.setText(data_provider.getUsername()+" commented on your rant!");
                break;
            case "comment_mention":
                holder.textViewText.setText(data_provider.getUsername()+" mentioned you in a comment!");
                break;
            case "comment_discuss":
                holder.textViewText.setText(data_provider.getUsername()+" (or more) new comments on a rant you commented on!");
                break;
            case "content_vote":
                holder.textViewText.setText(data_provider.getUsername()+" ++'d your rant");
                break;
            case "rant_sub":
                holder.textViewText.setText(data_provider.getUsername()+" posted a new rant!");
                break;
        }


        if (data_provider.getRead() == 0 && !data_provider.getType().equals("all")) {
            holder.textViewText.setTextColor(Color.parseColor("#f4945c"));
        }

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                holder.textViewText.setTextColor(Color.parseColor("#FFFFFF"));
                if(callback != null) {
                    callback.onItemClicked(position);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }
}

