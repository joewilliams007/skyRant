package com.dev.engineerrant.network.methods.dev;

import com.dev.engineerrant.network.models.dev.ModelProfile;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MethodsProfile {
    @GET
    Call<ModelProfile> getAllData(
            @Url String url
    );
}
