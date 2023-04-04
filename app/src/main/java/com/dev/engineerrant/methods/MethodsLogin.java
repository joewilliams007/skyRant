package com.dev.engineerrant.methods;

import com.dev.engineerrant.models.ModelLogin;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MethodsLogin {
    @GET
    Call<ModelLogin> getAllData(
            @Url String url
    );
}
