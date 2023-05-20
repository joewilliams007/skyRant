package com.dev.engineerrant.network.methods.dev;

import com.dev.engineerrant.network.models.dev.ModelImgRant;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MethodsImgRant {
    @GET
    Call<ModelImgRant> getAllData(
            @Url String url
    );
}
