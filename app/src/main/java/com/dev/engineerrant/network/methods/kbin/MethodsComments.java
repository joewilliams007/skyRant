package com.dev.engineerrant.network.methods.kbin;

import com.dev.engineerrant.network.models.kbin.ModelComments;
import com.dev.engineerrant.network.models.kbin.ModelEntries;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MethodsComments {
    @GET
    Call<ModelComments> getAllData(@Url String url);

}
