package com.dev.engineerrant.network.put;

import com.dev.engineerrant.classes.JSONsendMsg;
import com.dev.engineerrant.classes.matrix.MessageRequest;
import com.dev.engineerrant.network.models.matrix.ModelSendMessage;
import com.dev.engineerrant.network.post.matrix.JSONRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MessageSendClient {
    @Headers({"Accept: application/json","Content-Type: application/json","User-Agent: insomnia/8.1.0","Accept-Language: en-US,en;q=0.5"})
    @PUT("_matrix/client/v3/rooms/{roomId}/send/m.room.message/{messageId}")
    Call<ModelSendMessage> sendMessage(
            @Path("roomId") String roomId,
            @Path("messageId") String messageId,
            @Query(value = "access_token",encoded = true) String access_token,
            @Body JSONsendMsg jsoNsendMsg
    );
}
