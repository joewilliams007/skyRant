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
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dev.engineerrant.adapters.MatrixAdapter;
import com.dev.engineerrant.adapters.MatrixChatItem;
import com.dev.engineerrant.animations.Tools;
import com.dev.engineerrant.auth.MatrixAccount;
import com.dev.engineerrant.classes.JSONsendMsg;
import com.dev.engineerrant.classes.matrix.Chunk;
import com.dev.engineerrant.classes.matrix.MessageRequest;
import com.dev.engineerrant.network.DownloadImageTask;
import com.dev.engineerrant.network.RetrofitClient;
import com.dev.engineerrant.network.methods.matrix.MethodsChat;
import com.dev.engineerrant.network.models.matrix.ModelMatrixChat;
import com.dev.engineerrant.network.models.matrix.ModelSendMessage;
import com.dev.engineerrant.network.models.matrix.ModelToken;
import com.dev.engineerrant.network.post.matrix.JSONRequest;
import com.dev.engineerrant.network.post.matrix.MessageSend;
import com.dev.engineerrant.network.post.matrix.RegisterClient;
import com.dev.engineerrant.network.put.JSONAsyncTask;
import com.dev.engineerrant.network.put.MessageSendClient;

import java.io.IOException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MatrixChatActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ProgressBar progressBar;
    EditText editTextComment;
    String roomId = "%21DoEmcaQYzaoOxMsqCL:matrix.org";

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
        progressBar = findViewById(R.id.progressBar);
        editTextComment = findViewById(R.id.editTextComment);
    }

    private void getMessages() {
        MethodsChat methods = RetrofitClient.getRetrofitInstance().create(MethodsChat.class);
        Call<ModelMatrixChat> call = methods.getAllData(MatrixAccount.accessToken(),"50");
        call.enqueue(new Callback<ModelMatrixChat>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ModelMatrixChat> call, @NonNull Response<ModelMatrixChat> response) {

                Log.v("Upload", response+" ");

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
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<ModelMatrixChat> call, @NonNull Throwable t) {
                Log.d("error_contact", t.toString());
                toast("no network");
                progressBar.setVisibility(View.GONE);
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
    //Callback for AsyncTask to call when its completed
    public void JSONAsyncTask() {
        //Do stuff once data has been loaded
        getMessages();
        progressBar.setVisibility(View.GONE);
    }
    public void sendMessage(View view) {
        String message = editTextComment.getText().toString();
        if (message.length()<1){
            return;
        }
        editTextComment.setText(null);
        progressBar.setVisibility(View.VISIBLE);

        //Pass your params array and the current activity to the AsyncTask
        new JSONAsyncTask(MatrixChatActivity.this,message).execute();

        /*OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"msgtype\":\"m.text\",\"body\":\"hisdii\",\"m.mentions\":{}}");
        Request request = new Request.Builder()
                .url("https://matrix-client.matrix.org/_matrix/client/v3/rooms/!ucXjVyXiDeXwFuYFAN%3Amatrix.org/send/m.room.message/m1271s8d921?=&=&=&access_token=syt_am9ld2lsbGlhbXMwMDc_VDNfQUmawNGvrMujjnAC_3k1Cm1")
                .put(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("User-Agent", "insomnia/8.1.0")
                .build();

        try {
            okhttp3.Response response = client.newCall(request).execute();
            toast(response.code()+" ");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JSONRequest JSONRequest = new JSONRequest("{'msgtype':'m.text','body':'test message','m.mentions':{}}'");
        JSONsendMsg jsoNsendMsg = new JSONsendMsg();
        jsoNsendMsg.setBody("test from skyRant pls work");
        jsoNsendMsg.setMsgtype("m.text");
        String[] mentions = new String[0];
        jsoNsendMsg.setMentions(mentions);
        // Service
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://matrix-client.matrix.org") // SERVER IP
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        MessageSendClient client = retrofit.create(MessageSendClient.class);
        // finally, execute the request

        Call<ModelSendMessage> call = client.sendMessage(roomId, "m1697x049f",MatrixAccount.accessToken(),jsoNsendMsg);
        call.enqueue(new Callback<ModelSendMessage>() {
            @Override
            public void onResponse(@NonNull Call<ModelSendMessage> call, @NonNull Response<ModelSendMessage> response) {
                Log.v("Upload", response+" ");

                if (response.isSuccessful()) {
                    // Do awesome stuff
                    assert response.body() != null;

                } else {
                    // This is what happens with code 403
                    toast(response.code()+" code");
                    System.out.println(response.errorBody());
                }
                System.out.println(response.body());
                System.out.println(response);
                System.out.println(call.request().headers());
                System.out.println(call.request().body());
                System.out.println(response.raw().networkResponse());
                // System.out.println(response.headers());
            }

            @Override
            public void onFailure(@NonNull Call<ModelSendMessage> call, @NonNull Throwable t) {
                toast("Request failed! "+t.getMessage());
            }

        });*/
    }
}