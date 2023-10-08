package com.dev.engineerrant.adapters;

import static android.text.format.DateUtils.getRelativeTimeSpanString;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.dev.engineerrant.BuildConfig;
import com.dev.engineerrant.MainActivity;
import com.dev.engineerrant.MatrixLoginActivity;
import com.dev.engineerrant.R;
import com.dev.engineerrant.animations.Tools;
import com.dev.engineerrant.auth.Account;
import com.dev.engineerrant.auth.MyApplication;
import com.dev.engineerrant.network.DownloadImageTask;

import java.util.ArrayList;

public abstract class MagazineAdapter extends RecyclerView.Adapter<MagazineAdapter.RecyclerViewHolder> {

    private ArrayList<MagazineItem> dataSource = new ArrayList<MagazineItem>();

    public abstract void onItemClicked(Integer feedPosition);

    public interface AdapterCallback{
        void onItemClicked(Integer menuPosition);
    }
    private final AdapterCallback callback;

    private String drawableIcon;
    private final Context context;


    public MagazineAdapter(Context context, ArrayList<MagazineItem> dataArgs, AdapterCallback callback){
        this.context = context;
        this.dataSource = dataArgs;
        this.callback = callback;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.magazine_item,parent,false);
        return new RecyclerViewHolder(view);
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder
    {

        TextView textViewTitle, textViewBody, textViewUsername, textViewTags, textViewViews, textViewComments;
        ImageView imageViewProfile, imageViewRant;
        ConstraintLayout item;
        public RecyclerViewHolder(View view) {
            super(view);
            item = view.findViewById(R.id.item);
            textViewBody = view.findViewById(R.id.textViewBody);
            textViewTitle = view.findViewById(R.id.textViewTitle);
            textViewUsername = view.findViewById(R.id.textViewUsername);
            imageViewProfile = view.findViewById(R.id.imageViewProfile);
            textViewTags = view.findViewById(R.id.textViewTags);
            textViewViews = view.findViewById(R.id.textViewViews);
            textViewComments = view.findViewById(R.id.textViewComments);
            imageViewRant = view.findViewById(R.id.imageViewRant);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        MagazineItem data_provider = dataSource.get(position);
        if (data_provider.getItem().getTitle()==null) {
            holder.textViewTitle.setVisibility(View.GONE);
        } else {
            holder.textViewTitle.setText(data_provider.getItem().getTitle());
        }
        try {
            String s = data_provider.getItem().getBody();
            if (s==null) {
                holder.textViewBody.setVisibility(View.GONE);
            }
            if (s.length()>500) {
                s = s.substring(0, 500)+"... [read more]";
            }
            holder.textViewBody.setText(s);
        } catch (Exception ignored) {

        }
        holder.textViewUsername.setText(data_provider.getItem().getUser().getUsername());
        holder.textViewViews.setText(data_provider.getItem().getViews()+" views");
        holder.imageViewProfile.setImageDrawable(null);
        if (data_provider.getItem().getUser().getAvatar()!=null) {
            Glide.with(MyApplication.getAppContext()).load(data_provider.getItem().getUser().getAvatar().getStorageUrl()).into(holder.imageViewProfile);
        } else {
            holder.imageViewProfile.setBackgroundColor(Color.parseColor("#7bc892"));
        }
        if (data_provider.getItem().getNumComments() < 1) {
            holder.textViewComments.setVisibility(View.GONE);
        } else {
            holder.textViewComments.setText(data_provider.getItem().getNumComments()+" comments");
        }
        if (data_provider.getItem().getTags() == null) {
            holder.textViewTags.setVisibility(View.INVISIBLE);
        } else {
            String[] tags = data_provider.getItem().getTags();
            String t = "";
            for (String tag: tags) {
                t+=tag+", ";
            }
            holder.textViewTags.setText(t.substring(0, t.length()-2));
        }
        if (data_provider.getItem().getImage() == null) {
            holder.imageViewRant.setVisibility(View.GONE);
        } else {
            holder.imageViewRant.setVisibility(View.VISIBLE);

            if (Account.autoLoad()) {
                new DownloadImageTask(holder.imageViewRant)
                        .execute(data_provider.getItem().getImage().getStorageUrl());
            }
        }
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onItemClicked(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }
}

