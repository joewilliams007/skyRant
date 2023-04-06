package com.dev.engineerrant.adapters;

import static android.text.format.DateUtils.getRelativeTimeSpanString;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.dev.engineerrant.BuildConfig;
import com.dev.engineerrant.R;
import com.dev.engineerrant.animations.Tools;

import java.util.ArrayList;

public abstract class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.RecyclerViewHolder> {

    private ArrayList<CommunityItem> dataSource = new ArrayList<CommunityItem>();

    public abstract void onItemClicked(Integer feedPosition);

    public interface AdapterCallback{
        void onItemClicked(Integer menuPosition);
    }
    private AdapterCallback callback;

    private String drawableIcon;
    private Context context;


    public CommunityAdapter(Context context, ArrayList<CommunityItem> dataArgs, AdapterCallback callback){
        this.context = context;
        this.dataSource = dataArgs;
        this.callback = callback;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_item,parent,false);
        return new RecyclerViewHolder(view);
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder
    {

        TextView textViewTitle, textViewActive, textViewDate, textViewDesc, textViewType;
        ConstraintLayout constraintLayout;
        public RecyclerViewHolder(View view) {
            super(view);
            textViewType = view.findViewById(R.id.textViewType);
            textViewTitle = view.findViewById(R.id.textViewTitle);
            textViewActive = view.findViewById(R.id.textViewActive);
            textViewDate = view.findViewById(R.id.textViewDate);
            textViewDesc = view.findViewById(R.id.textViewDesc);
            constraintLayout = view.findViewById(R.id.inside);
        };
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        CommunityItem data_provider = dataSource.get(position);

        holder.textViewDate.setText("added "+getRelativeTimeSpanString(data_provider.getTimestamp_added()* 1000L));
        if (data_provider.getOs().equals("")) {
            holder.textViewType.setText(data_provider.getType());
        } else {
            holder.textViewType.setText(data_provider.getType()+" - "+data_provider.getOs());
        }
        holder.textViewDesc.setText(data_provider.getDesc());
        holder.textViewTitle.setText(data_provider.getTitle());

        if (data_provider.getActive()) {
            holder.textViewActive.setText("active");
        } else {
            holder.textViewActive.setText("inactive");
        }


        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

