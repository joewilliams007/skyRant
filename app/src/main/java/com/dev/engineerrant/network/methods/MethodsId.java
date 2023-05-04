package com.dev.engineerrant.network.methods;

import com.dev.engineerrant.network.models.ModelId;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MethodsId {
    @GET
    Call<ModelId> getAllData(
            @Url String url
    );
}
