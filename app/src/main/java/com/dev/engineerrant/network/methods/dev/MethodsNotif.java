package com.dev.engineerrant.network.methods.dev;

import com.dev.engineerrant.network.models.dev.ModelNotif;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MethodsNotif {
    @GET
    Call<ModelNotif> getAllData(
            @Url String url
    );
}
