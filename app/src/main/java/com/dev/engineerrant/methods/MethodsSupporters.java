package com.dev.engineerrant.methods;

import com.dev.engineerrant.models.ModelNotif;
import com.dev.engineerrant.models.ModelSupporters;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MethodsSupporters {
    @GET
    Call<ModelSupporters> getAllData(
            @Url String url
    );
}
