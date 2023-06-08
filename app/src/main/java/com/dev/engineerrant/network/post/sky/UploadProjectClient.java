package com.dev.engineerrant.network.post.sky;

import com.dev.engineerrant.network.models.sky.ModelSuccess;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UploadProjectClient {
    @Multipart

    @POST("upload_project")
    Call<ModelSuccess> upload(
            @Part("session_id") RequestBody session_id,
            @Part("user_id") RequestBody user_id,

            @Part("title") RequestBody title,
            @Part("os") RequestBody os,
            @Part("type") RequestBody type,
            @Part("timestamp_added") RequestBody timestamp_added,
            @Part("description") RequestBody description,
            @Part("relevant_dr_url") RequestBody relevant_dr_url,
            @Part("website") RequestBody website,
            @Part("github") RequestBody github,
            @Part("language") RequestBody language,
            @Part("active") RequestBody active,
            @Part("archived") RequestBody archived,
            @Part("owner_user_id") RequestBody owner_user_id,
            @Part("owner") RequestBody owner

    );
}