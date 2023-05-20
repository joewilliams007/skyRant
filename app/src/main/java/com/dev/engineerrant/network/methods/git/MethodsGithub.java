package com.dev.engineerrant.network.methods.git;

import com.dev.engineerrant.network.models.git.ModelGithub;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Url;

public interface MethodsGithub {
    @GET
    Call<ModelGithub> getAllData(@Header("Authorization") String auth,
            @Url String url
    );
}
