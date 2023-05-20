package com.dev.engineerrant.network.methods.git;

import com.dev.engineerrant.network.models.sky.ModelUpdate;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MethodsUpdate {
    @GET
    Call<ModelUpdate> getAllData(
            @Url String url
    );
}
