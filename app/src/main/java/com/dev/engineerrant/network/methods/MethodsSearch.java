package com.dev.engineerrant.network.methods;

import com.dev.engineerrant.network.models.ModelSearch;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MethodsSearch {
    @GET
    Call<ModelSearch> getAllData(
            @Url String url
    );
}
