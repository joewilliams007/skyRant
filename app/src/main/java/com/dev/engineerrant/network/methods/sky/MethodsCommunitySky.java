package com.dev.engineerrant.network.methods.sky;

import com.dev.engineerrant.network.models.git.ModelCommunity;
import com.dev.engineerrant.network.models.sky.ModelCommunitySky;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MethodsCommunitySky {
    @GET
    Call<ModelCommunitySky> getAllData(
            @Url String url
    );
}
