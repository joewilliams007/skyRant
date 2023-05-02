package com.dev.engineerrant.adapters;

import static com.dev.engineerrant.BuilderActivity.menu_prev_selected;
import static com.dev.engineerrant.BuilderActivity.menu_selected;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.dev.engineerrant.R;

import java.util.ArrayList;

public abstract class AvatarMenuAdapter extends RecyclerView.Adapter<AvatarMenuAdapter.RecyclerViewHolder> {

    private ArrayList<AvatarMenuItem> dataSource = new ArrayList<AvatarMenuItem>();

    public abstract void onItemClicked(Integer feedPosition);

    public interface AdapterCallback{
        void onItemClicked(Integer menuPosition);
    }
    private final AdapterCallback callback;

    private final Context context;


    public AvatarMenuAdapter(Context context, ArrayList<AvatarMenuItem> dataArgs, AdapterCallback callback){
        this.context = context;
        this.dataSource = dataArgs;
        this.callback = callback;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item,parent,false);
        return new RecyclerViewHolder(view);
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder
    {

        TextView textViewText;
        ConstraintLayout menuLayout;
        View viewSelected;
        public RecyclerViewHolder(View view) {
            super(view);
            textViewText = view.findViewById(R.id.textViewText);
            menuLayout = view.findViewById(R.id.menuLayout);
            viewSelected = view.findViewById(R.id.viewSelected);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        AvatarMenuItem data_provider = dataSource.get(position);
        holder.viewSelected.setVisibility(View.GONE);


        holder.textViewText.setText(data_provider.getMenus().getText());

        holder.menuLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(callback != null) {
                    if (menu_prev_selected != position)  {
                        notifyItemChanged(menu_prev_selected);
                    }
                    holder.viewSelected.setVisibility(View.VISIBLE);
                    menu_selected = position;
                    menu_prev_selected = position;
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

