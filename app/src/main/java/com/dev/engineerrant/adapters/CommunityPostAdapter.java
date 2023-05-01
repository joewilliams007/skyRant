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

import com.dev.engineerrant.R;

import java.util.ArrayList;

public abstract class CommunityPostAdapter extends RecyclerView.Adapter<CommunityPostAdapter.RecyclerViewHolder> {

    private ArrayList<CommunityPostItem> dataSource = new ArrayList<CommunityPostItem>();

    public abstract void onItemClicked(Integer feedPosition);

    public interface AdapterCallback{
        void onItemClicked(Integer menuPosition);
    }
    private final AdapterCallback callback;

    private String drawableIcon;
    private final Context context;


    public CommunityPostAdapter(Context context, ArrayList<CommunityPostItem> dataArgs, AdapterCallback callback){
        this.context = context;
        this.dataSource = dataArgs;
        this.callback = callback;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_post_item,parent,false);
        return new RecyclerViewHolder(view);
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder
    {

        TextView textViewTxt, textViewItem;
        ConstraintLayout constraintLayout;
        public RecyclerViewHolder(View view) {
            super(view);
            textViewTxt = view.findViewById(R.id.textViewTxt);
            constraintLayout = view.findViewById(R.id.item);
            textViewItem = view.findViewById(R.id.textViewItem);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        CommunityPostItem data_provider = dataSource.get(position);

        holder.textViewTxt.setText(data_provider.getTxt());
        holder.textViewItem.setText(data_provider.getItem());
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }
}

