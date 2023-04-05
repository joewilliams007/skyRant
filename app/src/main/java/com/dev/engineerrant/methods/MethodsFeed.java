package com.dev.engineerrant.methods;

import com.dev.engineerrant.models.ModelFeed;

import kotlin.ParameterName;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface MethodsFeed {
    @GET
    Call<ModelFeed> getAllData(@Url String url);

}
