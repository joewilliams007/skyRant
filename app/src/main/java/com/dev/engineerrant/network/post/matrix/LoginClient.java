package com.dev.engineerrant.network.post.matrix;

import com.dev.engineerrant.classes.matrix.LoginData;
import com.dev.engineerrant.network.models.matrix.ModelMatrixLogin;
import com.dev.engineerrant.network.models.matrix.ModelToken;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface LoginClient {
    @Headers("Content-Type: application/json")
    @POST("_matrix/client/v3/login")
    Call<ModelMatrixLogin> getStringScalar(@Body LoginData body);
}