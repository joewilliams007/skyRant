package com.dev.engineerrant.adapters;

import static android.text.format.DateUtils.getRelativeTimeSpanString;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dev.engineerrant.BuildConfig;
import com.dev.engineerrant.R;
import com.dev.engineerrant.animations.Tools;

import java.util.ArrayList;

public abstract class ChangelogAdapter extends RecyclerView.Adapter<ChangelogAdapter.RecyclerViewHolder> {

    private ArrayList<ChangelogItem> dataSource = new ArrayList<ChangelogItem>();

    public abstract void onItemClicked(Integer feedPosition);

    public interface AdapterCallback{
        void onItemClicked(Integer menuPosition);
    }
    private final AdapterCallback callback;

    private String drawableIcon;
    private final Context context;


    public ChangelogAdapter(Context context, ArrayList<ChangelogItem> dataArgs, AdapterCallback callback){
        this.context = context;
        this.dataSource = dataArgs;
        this.callback = callback;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_item,parent,false);
        return new RecyclerViewHolder(view);
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder
    {

        TextView textViewText, textViewDate, textViewVersion, textViewBuild, textViewInstalled;
        public RecyclerViewHolder(View view) {
            super(view);
            textViewText = view.findViewById(R.id.textViewText);
            textViewBuild = view.findViewById(R.id.textViewBuild);
            textViewVersion = view.findViewById(R.id.textViewVersion);
            textViewDate = view.findViewById(R.id.textViewDate);
            textViewInstalled = view.findViewById(R.id.textViewInstalled);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        ChangelogItem data_provider = dataSource.get(position);

        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;


        //  holder.setIsRecyclable(false);

        holder.textViewDate.setText(getRelativeTimeSpanString(data_provider.getTimestamp()));
        // holder.textViewText.setText(data_provider.getText());
        holder.textViewVersion.setText("Version "+data_provider.getVersion());
        holder.textViewBuild.setText("Build "+data_provider.getBuild());

        Tools.logHighlighter(data_provider.getText(),holder.textViewText);

        if (Integer.parseInt(data_provider.getBuild())==versionCode) {
            holder.textViewInstalled.setText("current version");
        } else if (Integer.parseInt(data_provider.getBuild())>versionCode) {
            holder.textViewInstalled.setText("not installed");
        } else {
            holder.textViewInstalled.setText("") ;
        }


    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }
}

