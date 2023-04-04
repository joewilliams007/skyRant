package com.dev.engineerrant.methods;

import com.dev.engineerrant.models.ModelProfile;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MethodsProfile {
    @GET
    Call<ModelProfile> getAllData(
            @Url String url
    );
}
