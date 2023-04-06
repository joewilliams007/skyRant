package com.dev.engineerrant.methods;

import com.dev.engineerrant.models.ModelCommunity;
import com.dev.engineerrant.models.ModelUpdate;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MethodsCommunity {
    @GET
    Call<ModelCommunity> getAllData(
            @Url String url
    );
}
