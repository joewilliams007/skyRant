package com.dev.engineerrant.network.post.sky;

import com.dev.engineerrant.network.models.sky.ModelSuccess;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface CustomClient {
    @Multipart

    @POST("set_custom")
    Call<ModelSuccess> upload(
            @Part("session_id") RequestBody session_id,
            @Part("user_id") RequestBody user_id,
            @Part("avatar_frame_url") RequestBody avatar_frame_url,
            @Part("avatar_bg_hex") RequestBody avatar_bg_hex,
            @Part("avatar_bg_url") RequestBody avatar_bg_url,
            @Part("profile_bg_url") RequestBody profile_bg_url
    );
}