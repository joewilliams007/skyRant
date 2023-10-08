package com.dev.engineerrant;

import static com.dev.engineerrant.app.toast;
import static com.dev.engineerrant.auth.Account.vibrate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dev.engineerrant.adapters.MatrixAdapter;
import com.dev.engineerrant.adapters.MatrixChatItem;
import com.dev.engineerrant.animations.Tools;
import com.dev.engineerrant.auth.MatrixAccount;
import com.dev.engineerrant.classes.matrix.Chunk;
import com.dev.engineerrant.classes.matrix.MessageRequest;
import com.dev.engineerrant.network.RetrofitClient;
import com.dev.engineerrant.network.methods.matrix.MethodsChat;
import com.dev.engineerrant.network.models.matrix.ModelMatrixChat;
import com.dev.engineerrant.network.models.matrix.ModelSendMessage;
import com.dev.engineerrant.network.post.matrix.MessageSend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MatrixChatActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matrix_chat);
        initialize();
        getMessages();
    }

    private void sendMessage(String hi) {
        // Create a Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://matrix-client.matrix.org/_matrix/client/v3/") // Replace with your Matrix API base URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create an instance of your MatrixApiService
                MessageSend apiService = retrofit.create(MessageSend.class);

        // Define the room ID and message
        String roomId = "%21DoEmcaQYzaoOxMsqCL:matrix.org";
        String messageText = "Hi";
        // Add the access token to the Authorization header
        String authorizationHeader = "Bearer " + MatrixAccount.accessToken();

        MessageRequest messageRequest = new MessageRequest(messageText);

        // Make the API call to send the message
        Call<ModelSendMessage> call = apiService.sendMessage(roomId, authorizationHeader, messageRequest);

        call.enqueue(new Callback<ModelSendMessage>() {
            @Override
            public void onResponse(Call<ModelSendMessage> call, Response<ModelSendMessage> response) {
                if (response.isSuccessful()) {
                    // Message sent successfully
                    // Handle the response here
                    getMessages();
                } else {
                    toast(response+" ");
                }
                Log.d("error_contact", response.message()+" "+response.code()+" "+response.toString()+" "+response.body()+" "+response.errorBody());
            }

            @Override
            public void onFailure(Call<ModelSendMessage> call, Throwable t) {
                // Handle network errors here
            }
        });

    }

    private void initialize() {
        recyclerView = findViewById(R.id.matrix_view);
    }

    private void getMessages() {
        MethodsChat methods = RetrofitClient.getRetrofitInstance().create(MethodsChat.class);
        Call<ModelMatrixChat> call = methods.getAllData(MatrixAccount.accessToken(),"50");
        call.enqueue(new Callback<ModelMatrixChat>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ModelMatrixChat> call, @NonNull Response<ModelMatrixChat> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    Log.d("MESSAGES", response.body().toString());
                    List<Chunk> messages = response.body().getMessages().getChunk();
                    createList(messages);
                } else if (response.code() == 429) {
                    // Handle unauthorized
                    toast(response.message());
                } else {
                    toast(response+" ");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ModelMatrixChat> call, @NonNull Throwable t) {
                Log.d("error_contact", t.toString());
                toast("no network");
            }
        });
    }

    private void createList(List<Chunk> messages) {
        ArrayList<MatrixChatItem> menuItems = new ArrayList<>();
        for (Chunk message : messages){
            try {
                //all += "\n"+message.getContent().getBody();
                //Log.d("Message", message.getContent().getBody());

                menuItems.add(new MatrixChatItem(message));
            } catch (Exception e) {
                Log.d("Message", "failed to log message");
            }

        }
        Collections.reverse(menuItems);
        build(menuItems);
    }

    private void build(ArrayList<MatrixChatItem> menuItems) {
        recyclerView.setHasFixedSize(false);

        recyclerView.getRecycledViewPool().setMaxRecycledViews(0,0);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MatrixChatActivity.this);


        MatrixAdapter mAdapter = new MatrixAdapter(this, menuItems, new MatrixAdapter.AdapterCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClicked(Integer menuPosition) {
                MatrixChatItem menuItem = menuItems.get(menuPosition);
            }
        }) {
            @Override
            public void onItemClicked(Integer feedPosition) {

            }
        };

        recyclerView.setAlpha(0);
        recyclerView.setTranslationY(Tools.dpToPx(40));
        recyclerView.animate().alpha(1).translationY(0).setDuration(300).withLayer();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {
                    Log.d("-----","end");
                    vibrate();
                }
            }
        });



        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);
    }

    public void refresh(View view) {
        getMessages();
    }

    public void openChat(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://matrix.to/#/#drcc:matrix.org"));
        startActivity(browserIntent);
    }
}