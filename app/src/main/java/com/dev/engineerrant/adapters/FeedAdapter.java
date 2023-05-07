package com.dev.engineerrant.adapters;

import static com.dev.engineerrant.app.toast;

import static java.lang.Integer.toBinaryString;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.dev.engineerrant.DoubleClickListener;
import com.dev.engineerrant.R;
import com.dev.engineerrant.animations.Tools;
import com.dev.engineerrant.auth.Account;
import com.dev.engineerrant.auth.MyApplication;
import com.dev.engineerrant.network.DownloadImageTask;

import java.util.ArrayList;

public abstract class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.RecyclerViewHolder> {

    private ArrayList<FeedItem> dataSource = new ArrayList<FeedItem>();

    public abstract void onItemClicked(Integer feedPosition);

    public interface AdapterCallback{
        void onItemClicked(Integer menuPosition, String type);
    }
    private final AdapterCallback callback;

    private String drawableIcon;
    private final Context context;


    public FeedAdapter(Context context, ArrayList<FeedItem> dataArgs, AdapterCallback callback){
        this.context = context;
        this.dataSource = dataArgs;
        this.callback = callback;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item,parent,false);
        return new RecyclerViewHolder(view);
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder
    {

        ImageView imageViewProfile, imageViewRant;
        TextView textViewUsername, textViewScore, textViewText, textViewTags, textViewComments, textViewScoreRant, textViewPlus, textViewMinus;
        ConstraintLayout constraintLayout, userInfo;
        View viewState, _view;

        public RecyclerViewHolder(View view) {
            super(view);
            imageViewProfile = view.findViewById(R.id.imageViewProfile);
            textViewUsername = view.findViewById(R.id.textViewUsername);
            textViewScore = view.findViewById(R.id.textViewScore);
            textViewText = view.findViewById(R.id.textViewText);
            constraintLayout = view.findViewById(R.id.item);
            textViewScoreRant = view.findViewById(R.id.textViewScoreRant);
            viewState = view.findViewById(R.id.viewState);
            textViewTags = view.findViewById(R.id.textViewTags);
            textViewComments = view.findViewById(R.id.textViewComments);
            textViewMinus = view.findViewById(R.id.textViewMinus);
            textViewPlus = view.findViewById(R.id.textViewPlus);
            imageViewRant = view.findViewById(R.id.imageViewRant);
            userInfo = view.findViewById(R.id.userInfo);
            _view = view.findViewById(R.id.view_container);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        FeedItem data_provider = dataSource.get(position);

        // holder.setIsRecyclable(false);

        if (Account.userInfo()) {
            holder.textViewUsername.setText(data_provider.getUsername());
            holder.imageViewProfile.setImageDrawable(null);
            if (data_provider.getI()!=null) {
                Glide.with(MyApplication.getAppContext()).load("https://avatars.devrant.com/"+data_provider.getI()).into(holder.imageViewProfile);
            } else {
                holder.imageViewProfile.setBackgroundColor(Color.parseColor("#"+data_provider.getB()));
            }

            if (data_provider.getUser_score()<0) {
                holder.textViewScore.setText(String.valueOf(data_provider.getUser_score()));
            } else {
                holder.textViewScore.setText("+"+data_provider.getUser_score());


                if (Account.binary()) {
                    holder.textViewScore.setText("+"+toBinaryString(data_provider.getUser_score()));
                }
            }

        } else {
            holder.userInfo.setVisibility(View.INVISIBLE);
        }

        if (data_provider.getImage() == null) {
            holder.imageViewRant.setVisibility(View.GONE);
        } else {
            holder.imageViewRant.setVisibility(View.VISIBLE);

            if (Account.autoLoad()) {
                new DownloadImageTask(holder.imageViewRant)
                        .execute(data_provider.getImage());
            }
        }

        try {
            String s = data_provider.getText();
            if (s.length()>500) {
                s = s.substring(0, 500)+"... [read more]";
            }
            holder.textViewText.setText(s);
        } catch (Exception e) {

        }



        if (data_provider.getVote_state() == 1) {
            holder.textViewPlus.setTextColor(Color.parseColor("#FFFF0000"));
        } else if (data_provider.getVote_state() == -1) {
            holder.textViewMinus.setTextColor(Color.parseColor("#FFFF0000"));
        }


        if (data_provider.getNumComments() < 1) {
            holder.textViewComments.setVisibility(View.GONE);
        } else {
            holder.textViewComments.setText(data_provider.getNumComments()+" comments");
        }

        if (data_provider.getScore() < 0) {
            holder.textViewScoreRant.setText(String.valueOf(data_provider.getScore()));
        } else {
            holder.textViewScoreRant.setText("+"+data_provider.getScore());
        }

        holder.textViewPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(callback != null) {
                    if (holder.textViewPlus.getCurrentTextColor() == Color.parseColor("#FFFF0000")) {
                        holder.textViewPlus.setTextColor(Color.parseColor("#FFFFFF"));
                        holder.textViewMinus.setTextColor(Color.parseColor("#FFFFFF"));
                        callback.onItemClicked(position, "removeVote");
                    } else {
                        holder.textViewPlus.setTextColor(Color.parseColor("#FFFF0000"));
                        holder.textViewMinus.setTextColor(Color.parseColor("#FFFFFF"));
                        callback.onItemClicked(position, "upvote");
                    }
                }
            }
        });

        holder.textViewMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(callback != null) {
                    if (holder.textViewMinus.getCurrentTextColor() == Color.parseColor("#FFFF0000")) {
                        holder.textViewPlus.setTextColor(Color.parseColor("#FFFFFF"));
                        holder.textViewMinus.setTextColor(Color.parseColor("#FFFFFF"));
                        callback.onItemClicked(position, "removeVote");
                    } else {
                        holder.textViewPlus.setTextColor(Color.parseColor("#FFFFFF"));
                        holder.textViewMinus.setTextColor(Color.parseColor("#FFFF0000"));
                        callback.onItemClicked(position, "downVote");
                    }
                }
            }
        });

        holder._view.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if(callback != null) {
                    callback.onItemClicked(position, "rant");
                }
            }

            @Override
            public void onDoubleClick(View v) {
                if(callback != null) {
                    if (holder.textViewPlus.getCurrentTextColor() == Color.parseColor("#FFFF0000")) {
                        holder.textViewPlus.setTextColor(Color.parseColor("#FFFFFF"));
                        holder.textViewMinus.setTextColor(Color.parseColor("#FFFFFF"));
                        callback.onItemClicked(position, "removeVote");
                    } else {
                        holder.textViewPlus.setTextColor(Color.parseColor("#FFFF0000"));
                        holder.textViewMinus.setTextColor(Color.parseColor("#FFFFFF"));
                        callback.onItemClicked(position, "upvote");
                    }
                }
            }
        });
        


        if (data_provider.getTags() == null) {
            holder.textViewTags.setVisibility(View.INVISIBLE);
        } else {
            String[] tags = data_provider.getTags();
            String t = "";
            for (String tag: tags) {
                t+=tag+", ";
            }

            holder.textViewTags.setText(t.substring(0, t.length()-2));
        }



        switch (data_provider.getType()) {
            case "feed":

                break;
            case "feed_image":

                break;
            case "feed_gif":

                break;
        }

        //if(callback != null) {
        //    callback.onItemClicked(position);
        //}



    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }
}

