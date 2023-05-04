package com.dev.engineerrant.network.methods;

import com.dev.engineerrant.network.models.ModelFeed;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MethodsFeed {
    @GET
    Call<ModelFeed> getAllData(@Url String url);

}
