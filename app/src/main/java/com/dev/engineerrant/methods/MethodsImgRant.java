package com.dev.engineerrant.methods;

import com.dev.engineerrant.models.ModelImgRant;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MethodsImgRant {
    @GET
    Call<ModelImgRant> getAllData(
            @Url String url
    );
}
