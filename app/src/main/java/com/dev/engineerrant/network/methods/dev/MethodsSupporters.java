package com.dev.engineerrant.network.methods.dev;

import com.dev.engineerrant.network.models.dev.ModelSupporters;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MethodsSupporters {
    @GET
    Call<ModelSupporters> getAllData(
            @Url String url
    );
}
