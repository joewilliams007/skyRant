package com.dev.engineerrant.network.post.sky;

import com.dev.engineerrant.auth.Account;
import com.dev.engineerrant.network.models.sky.ModelSuccess;

import java.util.Arrays;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface StashClient {
    @Multipart

    @POST("stash_rant")
    Call<ModelSuccess> upload(
            @Part("session_id") RequestBody session_id,
            @Part("user_id") RequestBody user_id,
            @Part("url") RequestBody url,
            @Part("rant_id") RequestBody rant_id,
            @Part("text") RequestBody text,
            @Part("score") RequestBody score,
            @Part("created_time") RequestBody created_time,
            @Part("num_comments") RequestBody num_comments,
            @Part("tags") RequestBody tags,
            @Part("edited") RequestBody edited,
            @Part("rant_user_id") RequestBody rant_user_id,
            @Part("user_username") RequestBody user_username,
            @Part("user_score") RequestBody user_score,
            @Part("b") RequestBody b,
            @Part("i") RequestBody i
    );
}