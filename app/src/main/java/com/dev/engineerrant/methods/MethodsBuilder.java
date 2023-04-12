package com.dev.engineerrant.methods;

import com.dev.engineerrant.models.ModelBuilder;
import com.dev.engineerrant.models.ModelFeed;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MethodsBuilder {
    @GET
    Call<ModelBuilder> getAllData(@Url String url);

}
