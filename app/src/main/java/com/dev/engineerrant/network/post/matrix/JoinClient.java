package com.dev.engineerrant.network.post.matrix;

import com.dev.engineerrant.network.models.matrix.ModelJoin;
import com.dev.engineerrant.network.models.sky.ModelSuccess;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface JoinClient {
    @Multipart

    @POST("_matrix/client/v3/rooms/!DoEmcaQYzaoOxMsqCL%253Amatrix.org/invite")
    Call<ModelJoin> join(
            @Part("reason") RequestBody reason,
            @Part("user_id") RequestBody user_id,
            @Part("access_token") RequestBody access_token
    );
}