package com.dev.engineerrant.post;

import com.dev.engineerrant.models.ModelSuccess;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RantClient {
    @Multipart

    @POST("rants")
    Call<ModelSuccess> upload(
            @Part("app") RequestBody app,
            @Part("rant") RequestBody rant,
            @Part("type") RequestBody type,
            @Part("tags") RequestBody tags,
            @Part("token_id") RequestBody token_id,
            @Part("token_key") RequestBody token_key,
            @Part("user_id") RequestBody user_id
    );
}