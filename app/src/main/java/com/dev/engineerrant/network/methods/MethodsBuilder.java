package com.dev.engineerrant.network.methods;

import com.dev.engineerrant.network.models.ModelBuilder;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MethodsBuilder {
    @GET
    Call<ModelBuilder> getAllData(@Url String url);

}
