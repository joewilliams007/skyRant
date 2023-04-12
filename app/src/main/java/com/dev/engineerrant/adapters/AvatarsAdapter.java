package com.dev.engineerrant.adapters;

import static android.text.format.DateUtils.getRelativeTimeSpanString;

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
import com.dev.engineerrant.BuildConfig;
import com.dev.engineerrant.R;
import com.dev.engineerrant.animations.Tools;
import com.dev.engineerrant.auth.MyApplication;
import com.dev.engineerrant.network.DownloadImageTask;

import java.util.ArrayList;

public abstract class AvatarsAdapter extends RecyclerView.Adapter<AvatarsAdapter.RecyclerViewHolder> {

    private ArrayList<AvatarsItem> dataSource = new ArrayList<AvatarsItem>();

    public abstract void onItemClicked(Integer feedPosition);

    public interface AdapterCallback{
        void onItemClicked(Integer menuPosition);
    }
    private final AdapterCallback callback;

    private String drawableIcon;
    private final Context context;


    public AvatarsAdapter(Context context, ArrayList<AvatarsItem> dataArgs, AdapterCallback callback){
        this.context = context;
        this.dataSource = dataArgs;
        this.callback = callback;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.builder_item,parent,false);
        return new RecyclerViewHolder(view);
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder
    {

        TextView textViewScore;
        ImageView imageViewAvatars;
        ConstraintLayout avatarsLayout;
        View viewSelected;
        public RecyclerViewHolder(View view) {
            super(view);
            textViewScore = view.findViewById(R.id.textViewScore);
            imageViewAvatars = view.findViewById(R.id.imageViewAvatars);
            avatarsLayout = view.findViewById(R.id.avatarsLayout);
            viewSelected = view.findViewById(R.id.viewSelected);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        AvatarsItem data_provider = dataSource.get(position);

        if (data_provider.getAvatars().getSelected()!=null) {
            if (prev_selected==0 && selected==0) {
                if (data_provider.getAvatars().getSelected()) {
                    holder.viewSelected.setVisibility(View.VISIBLE);
                    prev_selected = position;
                    selected = position;
                } else {
                    holder.viewSelected.setVisibility(View.GONE);
                }
            } else if (selected!=position) {
                holder.viewSelected.setVisibility(View.GONE);
            } else {
                prev_selected = position;
            }
        } else {
            holder.viewSelected.setVisibility(View.GONE);
        }

        if (data_provider.getAvatars().getPoints()!=0) {
            holder.textViewScore.setText(data_provider.getAvatars().getPoints()+"++");
        } else {
            holder.textViewScore.setText(" ");
        }


        Glide.with(MyApplication.getAppContext()).load("https://avatars.devrant.com/"+data_provider.getAvatars().getImg().getMid()).into(holder.imageViewAvatars);
        holder.avatarsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (data_provider.getAvatars().getPoints()>score) {
                    toast("You don't have enough points for this item.");
                } else {
                    if(callback != null) {
                        if (selected != position) {
                            holder.viewSelected.setVisibility(View.VISIBLE);
                            selected = position;
                            notifyItemChanged(prev_selected);
                            prev_selected = position;
                            callback.onItemClicked(position);
                        }
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

