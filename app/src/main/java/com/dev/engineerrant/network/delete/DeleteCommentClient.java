package com.dev.engineerrant.network.delete;

import com.dev.engineerrant.network.models.sky.ModelSuccess;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DeleteCommentClient {

    @DELETE("comments/{commentId}")
    Call<ModelSuccess> delete(
            @Path("commentId") String roomId,
            @Query(value = "app",encoded = true) String app,
            @Query(value = "token_id",encoded = true) String token_id,
            @Query(value = "token_key",encoded = true) String token_key,
            @Query(value = "user_id",encoded = true) String user_id
    );
}