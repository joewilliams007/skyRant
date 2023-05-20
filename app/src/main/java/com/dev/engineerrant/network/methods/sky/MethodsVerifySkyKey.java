package com.dev.engineerrant.network.methods.sky;

import com.dev.engineerrant.network.models.sky.ModelVerifySkyKey;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MethodsVerifySkyKey {
    @GET
    Call<ModelVerifySkyKey> getAllData(@Url String url
    );
}
