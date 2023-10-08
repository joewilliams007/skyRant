package com.dev.engineerrant.adapters;

import static android.text.format.DateUtils.getRelativeTimeSpanString;
import static java.lang.Integer.toBinaryString;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dev.engineerrant.DoubleClickListener;
import com.dev.engineerrant.EntryActivity;
import com.dev.engineerrant.R;
import com.dev.engineerrant.SettingsActivity;
import com.dev.engineerrant.animations.Tools;
import com.dev.engineerrant.auth.Account;
import com.dev.engineerrant.auth.MyApplication;
import com.dev.engineerrant.classes.dev.Links;
import com.dev.engineerrant.classes.kbin.CommentItems;
import com.dev.engineerrant.network.DownloadImageTask;

import java.util.ArrayList;

public abstract class EntryCommentAdapter extends RecyclerView.Adapter<EntryCommentAdapter.RecyclerViewHolder> {

    private ArrayList<EntryCommentItem> dataSource = new ArrayList<EntryCommentItem>();

    public abstract void onItemClicked(Integer feedPosition);

    public interface AdapterCallback{
        void onItemClicked(Integer menuPosition, String type);

    }
    private final AdapterCallback callback;

    private String drawableIcon;
    private final Context context;


    public EntryCommentAdapter(Context context, ArrayList<EntryCommentItem> dataArgs, AdapterCallback callback){
        this.context = context;
        this.dataSource = dataArgs;
        this.callback = callback;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.entry_comment_item,parent,false);
        return new RecyclerViewHolder(view);
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder
    {

        TextView textViewBody, textViewUsername, textViewTags, textViewViews, textViewComments;
        ImageView imageViewProfile, imageViewRant;
        ConstraintLayout item;
        RecyclerView link_view, comments_view;
        public RecyclerViewHolder(View view) {
            super(view);
            item = view.findViewById(R.id.item);
            textViewBody = view.findViewById(R.id.textViewBody);
            textViewUsername = view.findViewById(R.id.textViewUsername);
            imageViewProfile = view.findViewById(R.id.imageViewProfile);
            textViewTags = view.findViewById(R.id.textViewTags);
            textViewViews = view.findViewById(R.id.textViewViews);
            textViewComments = view.findViewById(R.id.textViewComments);
            imageViewRant = view.findViewById(R.id.imageViewRant);
            link_view = view.findViewById(R.id.link_view);
            comments_view = view.findViewById(R.id.comments_view);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        EntryCommentItem data_provider = dataSource.get(position);


        holder.textViewBody.setText(data_provider.getCommentItems().getBody());

        holder.textViewUsername.setText(data_provider.getCommentItems().getUser().getUsername());
        holder.imageViewProfile.setImageDrawable(null);
        if (data_provider.getCommentItems().getUser().getAvatar()!=null) {
            Glide.with(MyApplication.getAppContext()).load(data_provider.getCommentItems().getUser().getAvatar().getStorageUrl()).into(holder.imageViewProfile);
        } else {
            holder.imageViewProfile.setBackgroundColor(Color.parseColor("#7bc892"));
        }
        if (data_provider.getCommentItems().getImage() == null) {
            holder.imageViewRant.setVisibility(View.GONE);
        } else {
            holder.imageViewRant.setVisibility(View.VISIBLE);

            if (Account.autoLoad()) {
                new DownloadImageTask(holder.imageViewRant)
                        .execute(data_provider.getCommentItems().getImage().getStorageUrl());
            }
        }

        if (data_provider.getCommentItems().getChildCount()>0) {
                ArrayList<EntryCommentItem> menuItems = new ArrayList<>();

                for (CommentItems comment : data_provider.getCommentItems().getChildren()){
                    menuItems.add(new EntryCommentItem(comment));
                }

            holder.comments_view.setHasFixedSize(false);

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MyApplication.getAppContext());


                EntryCommentAdapter mAdapter = new EntryCommentAdapter(MyApplication.getAppContext(), menuItems, new EntryCommentAdapter.AdapterCallback() {
                    @Override
                    public void onItemClicked(Integer menuPosition, String type) {

                    }
                }) {
                    @Override
                    public void onItemClicked(Integer feedPosition) {

                    }
                };

            holder.comments_view.setLayoutManager(mLayoutManager);
            holder.comments_view.setAdapter(mAdapter);

        }

        // LINK RECYCLERVIEW
        String text = data_provider.getCommentItems().getBody();

        if (text==null) {
            return;
        }
        if (text.contains("http")) {
            ArrayList<LinkItem> linkItems = new ArrayList<>();

            for (String _text : text.split(" ")) {
                if (_text.contains("http")) {
                    linkItems.add(new LinkItem(_text.replaceAll("http"," http").split(" ")[1],true));
                }
            }

            holder.link_view.setHasFixedSize(false);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MyApplication.getAppContext());
            LinkAdapter mAdapter = new LinkAdapter(MyApplication.getAppContext(), linkItems, new LinkAdapter.AdapterCallback() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onItemClicked(Integer menuPosition) {
                    LinkItem menuItem = linkItems.get(menuPosition);

                    if (menuItem.getLink().contains("github")) {
                        if (menuItem.getLink().split("github/")[1].contains("/")) {
                            Intent intent = new Intent(MyApplication.getAppContext(), SettingsActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("repo_url",menuItem.getLink());
                            MyApplication.getAppContext().startActivity(intent);
                        } else {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(menuItem.getLink()));
                            browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            MyApplication.getAppContext().startActivity(browserIntent);
                        }
                    } else {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(menuItem.getLink()));
                        browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        MyApplication.getAppContext().startActivity(browserIntent);
                    }
                }
            }) {
                @Override
                public void onItemClicked(Integer feedPosition) {

                }
            };

            holder.link_view.setLayoutManager(mLayoutManager);
            holder.link_view.setAdapter(mAdapter);
        }
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }
}

