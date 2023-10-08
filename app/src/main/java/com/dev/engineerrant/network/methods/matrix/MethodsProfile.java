package com.dev.engineerrant.network.methods.matrix;

import com.dev.engineerrant.network.models.git.ModelCommunity;
import com.dev.engineerrant.network.models.matrix.ModelMatrixChat;
import com.dev.engineerrant.network.models.matrix.ModelMatrixProfile;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface MethodsProfile {
    @GET
    Call<ModelMatrixProfile> getAllData(
            @Url String url
    );
}
