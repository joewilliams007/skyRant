package com.dev.engineerrant.network.methods.dev;

import com.dev.engineerrant.network.models.dev.ModelRant;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MethodsRant {
    @GET
    Call<ModelRant> getAllData(
            @Url String url
    );
}
