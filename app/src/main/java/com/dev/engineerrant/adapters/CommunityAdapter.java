package com.dev.engineerrant.adapters;

import static android.text.format.DateUtils.getRelativeTimeSpanString;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dev.engineerrant.BuildConfig;
import com.dev.engineerrant.R;
import com.dev.engineerrant.animations.Tools;
import com.dev.engineerrant.auth.MyApplication;
import com.dev.engineerrant.classes.Links;

import java.util.ArrayList;
import java.util.Objects;

public abstract class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.RecyclerViewHolder> {

    private ArrayList<CommunityItem> dataSource = new ArrayList<CommunityItem>();

    public abstract void onItemClicked(Integer feedPosition);

    public interface AdapterCallback{
        void onItemClicked(Integer menuPosition);
    }
    private final AdapterCallback callback;

    private String drawableIcon;
    private final Context context;


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
        RecyclerView link_view;
        public RecyclerViewHolder(View view) {
            super(view);
            textViewType = view.findViewById(R.id.textViewType);
            textViewTitle = view.findViewById(R.id.textViewTitle);
            textViewActive = view.findViewById(R.id.textViewActive);
            textViewDate = view.findViewById(R.id.textViewDate);
            textViewDesc = view.findViewById(R.id.textViewDesc);
            constraintLayout = view.findViewById(R.id.inside);
            link_view = view.findViewById(R.id.link_view);
        }
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


        /*holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(callback != null) {
                    callback.onItemClicked(position);
                }
            }
        });*/

        // LINK RECYCLERVIEW

            ArrayList<LinkItem> linkItems = new ArrayList<>();

            if (!Objects.equals(data_provider.getGithub(), "")) {
                linkItems.add(new LinkItem(data_provider.getGithub()));
            }
            if (!Objects.equals(data_provider.getWebsite(), "")) {
                linkItems.add(new LinkItem(data_provider.getWebsite()));
            }
            if (!Objects.equals(data_provider.getRelevant_dr_url(), "")) {
                linkItems.add(new LinkItem(data_provider.getRelevant_dr_url()));
            }
            holder.link_view.setHasFixedSize(false);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MyApplication.getAppContext());
            LinkAdapter mAdapter = new LinkAdapter(MyApplication.getAppContext(), linkItems, new LinkAdapter.AdapterCallback() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onItemClicked(Integer menuPosition) {
                    LinkItem menuItem = linkItems.get(menuPosition);
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(menuItem.getLink()));
                    browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    MyApplication.getAppContext().startActivity(browserIntent);
                }
            }) {
                @Override
                public void onItemClicked(Integer feedPosition) {

                }
            };

            holder.link_view.setLayoutManager(mLayoutManager);
            holder.link_view.setAdapter(mAdapter);
        }


    @Override
    public int getItemCount() {
        return dataSource.size();
    }
}

