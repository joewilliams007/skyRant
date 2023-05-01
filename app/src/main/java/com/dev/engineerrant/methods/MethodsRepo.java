package com.dev.engineerrant.methods;

import com.dev.engineerrant.models.ModelRepo;
import com.dev.engineerrant.models.ModelUpdate;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Url;

public interface MethodsRepo {
    @GET
    Call<ModelRepo> getAllData(@Header("Authorization") String auth,
                               @Url String url
    );
}
