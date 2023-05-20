package com.dev.engineerrant.network.post.sky;

import com.dev.engineerrant.network.models.sky.ModelSuccess;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface DeleteAccountClient {
    @Multipart

    @POST("delete_account")
    Call<ModelSuccess> upload(
            @Part("session_id") RequestBody session_id,
            @Part("user_id") RequestBody user_id
    );
}