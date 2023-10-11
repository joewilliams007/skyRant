package com.dev.engineerrant.network.post.matrix;

import com.dev.engineerrant.network.models.matrix.ModelToken;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RegisterClient {

    @POST("_matrix/client/v3/register?kind=guest")
    Call<ModelToken> getToken(
            @Body JSONRequest body
    );
}