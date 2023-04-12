package com.dev.engineerrant.adapters;

import static android.text.format.DateUtils.getRelativeTimeSpanString;

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
import com.nguyencse.URLEmbeddedData;
import com.nguyencse.URLEmbeddedTask;
import com.nguyencse.URLEmbeddedView;

import java.util.ArrayList;

public abstract class LinkAdapter extends RecyclerView.Adapter<LinkAdapter.RecyclerViewHolder> {

    private ArrayList<LinkItem> dataSource = new ArrayList<LinkItem>();

    public abstract void onItemClicked(Integer feedPosition);

    public interface AdapterCallback{
        void onItemClicked(Integer menuPosition);
    }
    private final AdapterCallback callback;

    private String drawableIcon;
    private final Context context;


    public LinkAdapter(Context context, ArrayList<LinkItem> dataArgs, AdapterCallback callback){
        this.context = context;
        this.dataSource = dataArgs;
        this.callback = callback;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.link_item,parent,false);
        return new RecyclerViewHolder(view);
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder
    {

        TextView textViewHost, textViewTitle, textViewDesc;
        ImageView imageViewPreview, imageViewIcon;
        ConstraintLayout constraintLayout;
        public RecyclerViewHolder(View view) {
            super(view);
            constraintLayout = view.findViewById(R.id.item);
            textViewDesc = view.findViewById(R.id.textViewDesc);
            textViewHost = view.findViewById(R.id.textViewHost);
            textViewTitle = view.findViewById(R.id.textViewTitle);
            imageViewIcon = view.findViewById(R.id.imageViewIcon);
            imageViewPreview = view.findViewById(R.id.imageViewPreview);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        LinkItem data_provider = dataSource.get(position);

        URLEmbeddedTask urlTask = new URLEmbeddedTask(new URLEmbeddedTask.OnLoadURLListener() {
            @Override
            public void onLoadURLCompleted(URLEmbeddedData data) {
                if (data!=null) {
                    if (data.getTitle()!=null) {
                        holder.textViewTitle.setText(data.getTitle());
                        holder.textViewTitle.setVisibility(View.VISIBLE);
                    } else {
                        holder.textViewTitle.setVisibility(View.GONE);
                    }
                    if (data.getHost()!=null) {
                        holder.textViewHost.setText(data.getHost());
                        holder.textViewHost.setVisibility(View.VISIBLE);
                    } else {
                        holder.textViewHost.setVisibility(View.GONE);
                    }
                    if (data.getDescription()!=null) {
                        holder.textViewDesc.setText(data.getDescription());
                        holder.textViewDesc.setVisibility(View.VISIBLE);
                    } else {
                        holder.textViewDesc.setVisibility(View.GONE);
                    }

                    holder.imageViewPreview.setImageDrawable(null);
                    if (data.getThumbnailURL()!=null) {
                        Glide.with(MyApplication.getAppContext()).load(data.getThumbnailURL()).into(holder.imageViewPreview);
                        holder.imageViewPreview.setVisibility(View.VISIBLE);
                    } else {
                        holder.imageViewPreview.setVisibility(View.GONE);
                    }
                    if (data.getFavorURL()!=null) {
                        if (data.getHost().equals("github.com")) {
                            holder.imageViewIcon.setImageResource(R.drawable.github);
                        } else if (data.getHost().equals("devrant.com")) {
                            holder.imageViewIcon.setImageResource(R.drawable.icon);
                        } else {
                            Glide.with(MyApplication.getAppContext()).load(data.getFavorURL()).into(holder.imageViewIcon);
                        }
                    } else {
                        holder.imageViewIcon.setVisibility(View.GONE);
                        holder.imageViewIcon.setVisibility(View.VISIBLE);
                    }
                }

            }
        });
        urlTask.execute(data_provider.getLink());


        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
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

