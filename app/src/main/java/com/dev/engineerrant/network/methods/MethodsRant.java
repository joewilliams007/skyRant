package com.dev.engineerrant.network.methods;

import com.dev.engineerrant.network.models.ModelRant;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MethodsRant {
    @GET
    Call<ModelRant> getAllData(
            @Url String url
    );
}
