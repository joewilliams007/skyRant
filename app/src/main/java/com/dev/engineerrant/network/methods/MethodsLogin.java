package com.dev.engineerrant.network.methods;

import com.dev.engineerrant.network.models.ModelLogin;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MethodsLogin {
    @GET
    Call<ModelLogin> getAllData(
            @Url String url
    );
}
