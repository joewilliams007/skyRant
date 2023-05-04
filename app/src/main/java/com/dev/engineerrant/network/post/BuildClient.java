package com.dev.engineerrant.network.post;

import com.dev.engineerrant.network.models.ModelSuccess;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface BuildClient {
    @Multipart

    @POST("avatar")
    Call<ModelSuccess> upload(
            @Part("app") RequestBody app,
            @Part("image_id") RequestBody image_id,
            @Part("token_id") RequestBody token_id,
            @Part("token_key") RequestBody token_key,
            @Part("user_id") RequestBody user_id
    );
}