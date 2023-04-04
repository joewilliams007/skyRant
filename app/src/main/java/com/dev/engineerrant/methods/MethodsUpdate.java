package com.dev.engineerrant.methods;

import com.dev.engineerrant.models.ModelUpdate;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MethodsUpdate {
    @GET
    Call<ModelUpdate> getAllData(
            @Url String url
    );
}
