package com.dev.engineerrant.methods;

import com.dev.engineerrant.models.ModelId;
import com.dev.engineerrant.models.ModelLogin;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MethodsId {
    @GET
    Call<ModelId> getAllData(
            @Url String url
    );
}
