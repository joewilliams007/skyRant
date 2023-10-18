package com.dev.engineerrant.network.post;

import com.dev.engineerrant.network.models.matrix.ModelSendMessage;
import com.dev.engineerrant.network.models.sky.ModelSuccess;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ModifyCommentClient {
    @Multipart

    @POST("comments/{commentId}")
    Call<ModelSuccess> upload(
            @Path("commentId") String roomId,
            @Part("app") RequestBody app,
            @Part("comment") RequestBody comment,
            @Part("token_id") RequestBody token_id,
            @Part("token_key") RequestBody token_key,
            @Part("user_id") RequestBody user_id
    );
}