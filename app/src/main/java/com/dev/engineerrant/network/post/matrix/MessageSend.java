package com.dev.engineerrant.network.post.matrix;

import com.dev.engineerrant.classes.matrix.MessageRequest;
import com.dev.engineerrant.network.models.matrix.ModelSendMessage;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MessageSend {
    @POST("rooms/{roomId}/send")
    Call<ModelSendMessage> sendMessage(
            @Path("roomId") String roomId,
            @Header("Authorization") String authorization, // Add this header
            @Body MessageRequest messageRequest
    );
}
