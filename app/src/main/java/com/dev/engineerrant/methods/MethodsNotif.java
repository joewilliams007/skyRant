package com.dev.engineerrant.methods;

import com.dev.engineerrant.models.ModelNotif;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MethodsNotif {
    @GET
    Call<ModelNotif> getAllData(
            @Url String url
    );
}
