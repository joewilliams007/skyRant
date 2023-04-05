package com.dev.engineerrant.post;

import com.dev.engineerrant.models.ModelSuccess;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface AccountClient {
    @Multipart

    @POST("edit-profile")
    Call<ModelSuccess> edit(
            @Part("app") RequestBody app,
            @Part("token_id") RequestBody token_id,
            @Part("token_key") RequestBody token_key,
            @Part("user_id") RequestBody user_id,

            @Part("profile_about") RequestBody profile_about,
            @Part("profile_skills") RequestBody profile_skills,
            @Part("profile_location") RequestBody profile_location,
            @Part("profile_website") RequestBody profile_website,
            @Part("profile_github") RequestBody profile_github

    );
}