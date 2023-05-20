package com.dev.engineerrant.network.methods.sky;

import com.dev.engineerrant.network.models.sky.ModelSkyPost;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MethodsSkyPost {
    @GET
    Call<ModelSkyPost> getAllData(@Url String url
    );
}
