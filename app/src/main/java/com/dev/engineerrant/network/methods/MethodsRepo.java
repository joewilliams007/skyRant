package com.dev.engineerrant.network.methods;

import com.dev.engineerrant.network.models.ModelRepo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Url;

public interface MethodsRepo {
    @GET
    Call<ModelRepo> getAllData(@Header("Authorization") String auth,
                               @Url String url
    );
}
