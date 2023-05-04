package com.dev.engineerrant.network.methods;

import com.dev.engineerrant.network.models.ModelCommunity;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MethodsCommunity {
    @GET
    Call<ModelCommunity> getAllData(
            @Url String url
    );
}
