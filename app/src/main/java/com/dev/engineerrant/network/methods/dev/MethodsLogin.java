package com.dev.engineerrant.network.methods.dev;

import com.dev.engineerrant.network.models.dev.ModelLogin;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MethodsLogin {
    @GET
    Call<ModelLogin> getAllData(
            @Url String url
    );
}
