package com.dev.engineerrant.network.put;

import android.os.AsyncTask;

import com.dev.engineerrant.MatrixChatActivity;
import com.dev.engineerrant.auth.Account;
import com.dev.engineerrant.auth.MatrixAccount;

import java.io.IOException;
import java.security.Timestamp;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


public class JSONAsyncTask extends AsyncTask<String, Void, Boolean> {

        private MatrixChatActivity activity;
        private String message;
        public JSONAsyncTask(MatrixChatActivity activity, String message) {
            this.activity = activity;
            this.message = message;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(String... urls) {
            try {


                OkHttpClient client = new OkHttpClient();

                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\"msgtype\":\"m.text\",\"body\":\""+message+"\",\"m.mentions\":{}}");
                Request request = new Request.Builder()
                        .url("https://matrix-client.matrix.org/_matrix/client/v3/rooms/%21DoEmcaQYzaoOxMsqCL:matrix.org/send/m.room.message/m"+System.currentTimeMillis()+"?=&=&=&access_token="+ MatrixAccount.accessToken())
                        .put(body)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("User-Agent", "insomnia/8.1.0")
                        .build();


                    okhttp3.Response response = client.newCall(request).execute();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        protected void onPostExecute(Boolean result) {
            activity.JSONAsyncTask();
        }

    }
