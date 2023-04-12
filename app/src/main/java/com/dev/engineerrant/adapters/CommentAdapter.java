package com.dev.engineerrant.adapters;

import static android.text.format.DateUtils.getRelativeTimeSpanString;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dev.engineerrant.DoubleClickListener;
import com.dev.engineerrant.R;
import com.dev.engineerrant.animations.Tools;
import com.dev.engineerrant.auth.Account;
import com.dev.engineerrant.auth.MyApplication;
import com.dev.engineerrant.network.DownloadImageTask;

import java.util.ArrayList;

public abstract class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.RecyclerViewHolder> {

    private ArrayList<CommentItem> dataSource = new ArrayList<CommentItem>();

    public abstract void onItemClicked(Integer feedPosition);

    public interface AdapterCallback{
        void onItemClicked(Integer menuPosition, String type);

    }
    private final AdapterCallback callback;

    private String drawableIcon;
    private final Context context;


    public CommentAdapter(Context context, ArrayList<CommentItem> dataArgs, AdapterCallback callback){
        this.context = context;
        this.dataSource = dataArgs;
        this.callback = callback;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item,parent,false);
        return new RecyclerViewHolder(view);
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder
    {

        ImageView imageViewProfile, imageViewRant;
        TextView textViewUsername, textViewScore, textViewText, textViewReply, textViewDate, textViewScoreRant, textViewPlus, textViewMinus;
        ConstraintLayout constraintLayout;
        View _view;
        View viewState;

        public RecyclerViewHolder(View view) {
            super(view);
            imageViewProfile = view.findViewById(R.id.imageViewProfile);
            textViewUsername = view.findViewById(R.id.textViewUsername);
            textViewScore = view.findViewById(R.id.textViewScore);
            textViewText = view.findViewById(R.id.textViewText);
            constraintLayout = view.findViewById(R.id.item);
            textViewScoreRant = view.findViewById(R.id.textViewScoreRant);
            viewState = view.findViewById(R.id.viewState);
            textViewReply = view.findViewById(R.id.textViewReply);
            textViewDate = view.findViewById(R.id.textViewDate);
            textViewPlus = view.findViewById(R.id.textViewPlus);
            textViewMinus = view.findViewById(R.id.textViewMinus);
            imageViewRant = view.findViewById(R.id.imageViewRant);
            _view = view.findViewById(R.id.view_container);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        CommentItem data_provider = dataSource.get(position);

        // holder.setIsRecyclable(false);

        holder.imageViewProfile.setImageDrawable(null);
        if (data_provider.getI()!=null) {
            Glide.with(MyApplication.getAppContext()).load("https://avatars.devrant.com/"+data_provider.getI()).into(holder.imageViewProfile);
        } else {
            holder.imageViewProfile.setBackgroundColor(Color.parseColor("#"+data_provider.getB()));
        }

        holder.textViewUsername.setText(data_provider.getUsername());

        String s = data_provider.getText();

        if(Account.highlighter()) {
            Tools.highlighter(s,holder.textViewText);
        } else {
            holder.textViewText.setText(s);
        }


        holder.textViewDate.setText(getRelativeTimeSpanString(data_provider.getCreated_time()* 1000L));

        if (data_provider.getImage() == null) {
            holder.imageViewRant.setVisibility(View.GONE);
        } else {
            holder.imageViewRant.setVisibility(View.VISIBLE);

            if (Account.autoLoad()) {
                new DownloadImageTask(holder.imageViewRant)
                        .execute(data_provider.getImage());
            }
            holder.imageViewRant.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(data_provider.getImage()));
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    MyApplication.getAppContext().startActivity(i);
                }
            });
        }

        if (data_provider.getScore() < 0) {
            holder.textViewScoreRant.setText(String.valueOf(data_provider.getScore()));
        } else {
            holder.textViewScoreRant.setText("+"+data_provider.getScore());
        }

        int rantVote = data_provider.getVote_state();

        if (rantVote == 1) {
            holder.textViewPlus.setTextColor(Color.parseColor("#FFFF0000"));
            holder.textViewMinus.setTextColor(Color.parseColor("#FFFFFF"));
        } else if (rantVote == -1) {
            holder.textViewPlus.setTextColor(Color.parseColor("#FFFFFF"));
            holder.textViewMinus.setTextColor(Color.parseColor("#FFFF0000"));
        }

        holder.textViewPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(callback != null) {
                    callback.onItemClicked(position, "upvote");
                }
            }
        });

        holder._view.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onSingleClick(View v) {

            }

            @Override
            public void onDoubleClick(View v) {
                if(callback != null) {
                    callback.onItemClicked(position, "upvote");
                }
            }
        });


        holder.textViewMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(callback != null) {
                    callback.onItemClicked(position, "downVote");
                }
            }
        });

        holder.textViewUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(callback != null) {
                    callback.onItemClicked(position, "profile");
                }
            }
        });

        holder.textViewScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(callback != null) {
                    callback.onItemClicked(position, "profile");
                }
            }
        });

        holder.imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(callback != null) {
                    callback.onItemClicked(position, "profile");
                }
            }
        });

        if (data_provider.getUser_score()<0) {
            holder.textViewScore.setText(String.valueOf(data_provider.getUser_score()));
        } else {
            holder.textViewScore.setText("+"+data_provider.getUser_score());
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

        holder.textViewReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                if(callback != null) {
                    callback.onItemClicked(position, "reply");
                }
            }
        });




    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }
}

