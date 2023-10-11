package com.dev.engineerrant.adapters;

import static android.text.format.DateUtils.getRelativeTimeSpanString;

import static com.dev.engineerrant.ReactionActivity.reactions;
import static com.dev.engineerrant.app.toast;
import static com.dev.engineerrant.network.RetrofitClient.SKY_SERVER_URL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dev.engineerrant.BuildConfig;
import com.dev.engineerrant.R;
import com.dev.engineerrant.SettingsActivity;
import com.dev.engineerrant.animations.Tools;
import com.dev.engineerrant.auth.Account;
import com.dev.engineerrant.auth.MyApplication;
import com.dev.engineerrant.classes.dev.Links;
import com.dev.engineerrant.classes.sky.Reactions;
import com.dev.engineerrant.network.DownloadImageTask;
import com.dev.engineerrant.network.RetrofitClient;
import com.dev.engineerrant.network.methods.matrix.MethodsProfile;
import com.dev.engineerrant.network.methods.sky.MethodsSkyPost;
import com.dev.engineerrant.network.models.matrix.ModelMatrixProfile;
import com.dev.engineerrant.network.models.sky.ModelSkyPost;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class MatrixAdapter extends RecyclerView.Adapter<MatrixAdapter.RecyclerViewHolder> {

    private ArrayList<MatrixChatItem> dataSource = new ArrayList<MatrixChatItem>();

    public abstract void onItemClicked(Integer feedPosition);

    public interface AdapterCallback{
        void onItemClicked(Integer menuPosition);
    }
    private final AdapterCallback callback;

    private String drawableIcon;
    private final Context context;


    public MatrixAdapter(Context context, ArrayList<MatrixChatItem> dataArgs, AdapterCallback callback){
        this.context = context;
        this.dataSource = dataArgs;
        this.callback = callback;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.matrix_chat_item,parent,false);
        return new RecyclerViewHolder(view);
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder
    {

        TextView textViewText, textViewDate, textViewUsername;
        ImageView imageView, imageViewProfile;
        RecyclerView link_view;
        public RecyclerViewHolder(View view) {
            super(view);
            textViewText = view.findViewById(R.id.textViewText);
            textViewDate = view.findViewById(R.id.textViewDate);
            textViewUsername = view.findViewById(R.id.textViewUsername);
            imageView = view.findViewById(R.id.imageView);
            imageViewProfile = view.findViewById(R.id.imageViewProfile);
            link_view = view.findViewById(R.id.link_view);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        MatrixChatItem data_provider = dataSource.get(position);

       // holder.textViewDate.setText(getRelativeTimeSpanString(data_provider.getTimestamp()));
        holder.textViewUsername.setText(data_provider.getMessage().getSender().split(":")[0].split("@")[1]);
      //  holder.textViewUsername.setText(data_provider.getMessage().ge);
        holder.textViewText.setText(data_provider.getMessage().getContent().getBody());
        if (data_provider.getMessage().getContent().getBody()==null||data_provider.getMessage().getContent().getBody().equals("")) {
            holder.textViewText.setText("[unsupported message]");
        }

        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm, dd.MM");
        holder.textViewDate.setText(formatter.format(data_provider.getMessage().getOrigin_server_ts()));

        holder.imageViewProfile.setImageDrawable(null);


                try {
                    MethodsProfile methods = RetrofitClient.getRetrofitInstance().create(MethodsProfile.class);
                    String total_url = "https://matrix-client.matrix.org/_matrix/client/v3/profile/"+data_provider.getMessage().getSender();

                    Call<ModelMatrixProfile> call = methods.getAllData(total_url);

                    call.enqueue(new Callback<ModelMatrixProfile>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onResponse(@NonNull Call<ModelMatrixProfile> call, @NonNull Response<ModelMatrixProfile> response) {
                            if (response.isSuccessful()) {
                                holder.imageViewProfile.setVisibility(View.VISIBLE);
                                assert response.body() != null;
                                try {
                                    holder.textViewUsername.setText(response.body().getDisplayname());
                                    if (response.body().getAvatar_url()!=null) {
                                        Glide.with(MyApplication.getAppContext()).load("https://matrix-client.matrix.org/_matrix/media/v3/thumbnail/"+response.body().getAvatar_url().split("mxc://")[1]+"?width=258&height=258&method=scale").into(holder.imageViewProfile);
                                    } else {
                                        holder.imageViewProfile.setBackgroundColor(Color.parseColor("#7bc8a4"));
                                    }
                                } catch (Exception e){

                                }
                            } else if (response.code() == 429) {
                                // Handle unauthorized
                                toast("you are not authorized");
                            } else {
                                holder.imageViewProfile.setBackgroundColor(Color.parseColor("#7bc8a4"));
                                toast("no success "+response.message());
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<ModelMatrixProfile> call, @NonNull Throwable t) {
                            Log.d("error_contact", t.toString());
                            holder.imageViewProfile.setBackgroundColor(Color.parseColor("#7bc8a4"));
                        }
                    });
                } catch (Exception e) {

                }



        try {
            if (!data_provider.getMessage().getContent().getMsgtype().equals("m.image")) {
                holder.imageView.setVisibility(View.GONE);
            } else {
                holder.textViewText.setVisibility(View.GONE);
                String image = "https://matrix-client.matrix.org/_matrix/media/v3/download/matrix.org/"+data_provider.getMessage().getContent().getUrl().split("matrix.org/")[1];

                holder.imageView.setVisibility(View.VISIBLE);

                if (Account.autoLoad()) {
                    new DownloadImageTask(holder.imageView)
                            .execute(image);
                }
                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(image));
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        MyApplication.getAppContext().startActivity(i);
                    }
                });
            }
        } catch (Exception e) {
            holder.imageView.setVisibility(View.GONE);
        }
        // LINK RECYCLERVIEW
        String text = data_provider.getMessage().getContent().getBody();

        if (text==null) {
            return;
        }
        if (text.contains("http")) {
            ArrayList<LinkItem> linkItems = new ArrayList<>();

            for (String _text : text.split("\n")) {
                if (_text.contains("http")) {
                    for (String __text : _text.split(" ")) {
                        if (__text.contains("http")) {
                            linkItems.add(new LinkItem(__text,true));
                        }
                    }
                }
            }

            holder.link_view.setHasFixedSize(false);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MyApplication.getAppContext());
            LinkAdapter mAdapter = new LinkAdapter(MyApplication.getAppContext(), linkItems, new LinkAdapter.AdapterCallback() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onItemClicked(Integer menuPosition) {
                    LinkItem menuItem = linkItems.get(menuPosition);

                    if (menuItem.getLink().contains("github")) {
                        if (menuItem.getLink().split("github/")[1].contains("/")) {
                            Intent intent = new Intent(MyApplication.getAppContext(), SettingsActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("repo_url",menuItem.getLink());
                            MyApplication.getAppContext().startActivity(intent);
                        } else {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(menuItem.getLink()));
                            browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            MyApplication.getAppContext().startActivity(browserIntent);
                        }
                    } else {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(menuItem.getLink()));
                        browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        MyApplication.getAppContext().startActivity(browserIntent);
                    }
                }
            }) {
                @Override
                public void onItemClicked(Integer feedPosition) {

                }
            };

            holder.link_view.setLayoutManager(mLayoutManager);
            holder.link_view.setAdapter(mAdapter);
        }
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }
}

