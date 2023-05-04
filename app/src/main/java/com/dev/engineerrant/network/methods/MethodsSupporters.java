package com.dev.engineerrant.network.methods;

import com.dev.engineerrant.network.models.ModelSupporters;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MethodsSupporters {
    @GET
    Call<ModelSupporters> getAllData(
            @Url String url
    );
}
