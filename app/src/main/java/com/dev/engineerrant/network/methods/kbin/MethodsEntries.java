package com.dev.engineerrant.network.methods.kbin;

import com.dev.engineerrant.network.models.dev.ModelFeed;
import com.dev.engineerrant.network.models.kbin.ModelEntries;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MethodsEntries {
    @GET
    Call<ModelEntries> getAllData(@Url String url);

}
