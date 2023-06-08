package com.dev.engineerrant.network.methods.sky;

import com.dev.engineerrant.network.models.sky.ModelSkyProfileAuth;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MethodsSkyProfileAuth {
    @GET
    Call<ModelSkyProfileAuth> getAllData(@Url String url
    );
}
