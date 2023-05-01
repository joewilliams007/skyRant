package com.dev.engineerrant.methods;

import com.dev.engineerrant.models.ModelGithub;
import com.dev.engineerrant.models.ModelRepo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Url;

public interface MethodsGithub {
    @GET
    Call<ModelGithub> getAllData(@Header("Authorization") String auth,
            @Url String url
    );
}
