package com.dev.engineerrant.network.methods.dev;

import com.dev.engineerrant.network.models.dev.ModelId;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MethodsId {
    @GET
    Call<ModelId> getAllData(
            @Url String url
    );
}
