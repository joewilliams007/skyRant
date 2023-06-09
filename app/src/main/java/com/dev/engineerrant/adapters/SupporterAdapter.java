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

import java.util.ArrayList;

public abstract class SupporterAdapter extends RecyclerView.Adapter<SupporterAdapter.RecyclerViewHolder> {

    private ArrayList<SupporterItem> dataSource = new ArrayList<SupporterItem>();

    public abstract void onItemClicked(Integer feedPosition);

    public interface AdapterCallback{
        void onItemClicked(Integer menuPosition);
    }
    private final AdapterCallback callback;

    private String drawableIcon;
    private final Context context;


    public SupporterAdapter(Context context, ArrayList<SupporterItem> dataArgs, AdapterCallback callback){
        this.context = context;
        this.dataSource = dataArgs;
        this.callback = callback;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.supporter_item,parent,false);
        return new RecyclerViewHolder(view);
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder
    {

        ImageView imageViewProfile;
        TextView textViewUsername, textViewDate;
        ConstraintLayout constraintLayout;
        public RecyclerViewHolder(View view) {
            super(view);
            imageViewProfile = view.findViewById(R.id.imageViewProfile);
            textViewUsername = view.findViewById(R.id.textViewUsername);
            constraintLayout = view.findViewById(R.id.item);
            textViewDate = view.findViewById(R.id.textViewDate);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        SupporterItem data_provider = dataSource.get(position);

        holder.textViewDate.setText("supporter since "+getRelativeTimeSpanString(data_provider.getSupporterItems().getStart()*1000L));

        holder.imageViewProfile.setImageDrawable(null);
        if (data_provider.getSupporterItems().getUser().getAvatar().getI()!=null) {
            Glide.with(MyApplication.getAppContext()).load("https://avatars.devrant.com/"+data_provider.getSupporterItems().getUser().getAvatar().getI()).into(holder.imageViewProfile);
        } else {
            holder.imageViewProfile.setBackgroundColor(Color.parseColor("#"+data_provider.getSupporterItems().getUser().getAvatar().getB()));
        }

        holder.textViewUsername.setText(data_provider.getSupporterItems().getUser().getName());

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

