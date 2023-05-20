package com.dev.engineerrant.network.methods.dev;

import com.dev.engineerrant.network.models.dev.ModelSearch;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MethodsSearch {
    @GET
    Call<ModelSearch> getAllData(
            @Url String url
    );
}
