package com.dev.engineerrant.network.post.matrix;

import com.dev.engineerrant.network.models.dev.ModelLogin;
import com.dev.engineerrant.network.models.matrix.ModelToken;

import org.chromium.support_lib_boundary.WebMessagePayloadBoundaryInterface;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RegisterClient {

    @POST("_matrix/client/v3/register?kind=guest")
    Call<ModelToken> getToken(
            @Body TokenRequest body
    );
}