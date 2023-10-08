package com.dev.engineerrant.network.methods.matrix;

import com.dev.engineerrant.network.models.git.ModelCommunity;
import com.dev.engineerrant.network.models.matrix.ModelMatrixChat;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface MethodsChat {
    @GET("https://matrix-client.matrix.org/_matrix/client/v3/rooms/%21DoEmcaQYzaoOxMsqCL:matrix.org/initialSync")
    Call<ModelMatrixChat> getAllData(
            @Query(value = "access_token",encoded = true) String access_token,
            @Query(value = "limit",encoded = true) String limit
    );
}
