package com.dev.engineerrant.adapters;

import static android.text.format.DateUtils.getRelativeTimeSpanString;

import static com.dev.engineerrant.CommunityPostActivity.communityItem;

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
import androidx.recyclerview.widget.RecyclerView;

import com.dev.engineerrant.CommunityActivity;
import com.dev.engineerrant.CommunityPostActivity;
import com.dev.engineerrant.R;
import com.dev.engineerrant.auth.MyApplication;

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

        TextView textViewTitle, textViewActive, textViewDate, textViewDesc, textViewType, textViewGithub, textViewRelated, textViewWebsite,textViewInfo;
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
            textViewInfo = view.findViewById(R.id.textViewInfo);
            textViewWebsite = view.findViewById(R.id.textViewWebsite);
            textViewGithub = view.findViewById(R.id.textViewGithub);
            textViewRelated = view.findViewById(R.id.textViewRelated);
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

            if (!Objects.equals(data_provider.getGithub(), "")) {
                holder.textViewGithub.setVisibility(View.VISIBLE);
                holder.textViewGithub.setText(data_provider.getGithub());
                holder.textViewGithub.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (data_provider.getGithub().contains("gist")) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data_provider.getGithub().replace("https","http")));
                            browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            MyApplication.getAppContext().startActivity(browserIntent);
                        } else {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data_provider.getGithub()));
                            browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            MyApplication.getAppContext().startActivity(browserIntent);
                        }

                    }
                });
            } else {
                holder.textViewGithub.setVisibility(View.GONE);
            }

        if (!Objects.equals(data_provider.getWebsite(), "")) {
            holder.textViewWebsite.setVisibility(View.VISIBLE);
            holder.textViewWebsite.setText(data_provider.getWebsite());
            holder.textViewWebsite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data_provider.getWebsite()));
                    browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    MyApplication.getAppContext().startActivity(browserIntent);
                }
            });
        } else {
            holder.textViewWebsite.setVisibility(View.GONE);
        }

        if (data_provider.getGithub().length() <5){
            holder.textViewInfo.setVisibility(View.GONE);
        }

        holder.textViewInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(callback != null) {
                    callback.onItemClicked(position);
                }
            }
        });


        if (!Objects.equals(data_provider.getRelevant_dr_url(), "")) {
            holder.textViewRelated.setText(data_provider.getRelevant_dr_url());
            holder.textViewRelated.setVisibility(View.VISIBLE);
            holder.textViewRelated.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data_provider.getRelevant_dr_url()));
                    browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    MyApplication.getAppContext().startActivity(browserIntent);
                }
            });
        } else {
            holder.textViewRelated.setVisibility(View.GONE);
        }

    }


    @Override
    public int getItemCount() {
        return dataSource.size();
    }
}

