package com.dev.engineerrant.network.methods.kbin;

import com.dev.engineerrant.network.models.kbin.ModelEntries;
import com.dev.engineerrant.network.models.kbin.ModelUser;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface MethodsUser {
    @GET
    Call<ModelUser> getAllData(@Url String url);

}
