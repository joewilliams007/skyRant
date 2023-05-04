package com.dev.engineerrant.network.post;

import com.dev.engineerrant.network.models.ModelLogin;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface LoginClient {
    @Multipart

    @POST("users/auth-token")
    Call<ModelLogin> login(
            @Part("app") RequestBody app,
            @Part("username") RequestBody username,
            @Part("password") RequestBody password
    );
}