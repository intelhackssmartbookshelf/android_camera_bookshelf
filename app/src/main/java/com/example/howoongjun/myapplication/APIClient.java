package com.example.howoongjun.myapplication;

/**
 * Created by howoongjun on 2017. 7. 26..
 */

import com.example.howoongjun.myapplication.AccessToken;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface APIClient {

    @FormUrlEncoded
    @POST("/o/token/")
    Call<AccessToken> getNewAccessToken(
            @Field("username") String userName,
            @Field("password") String password,
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret,
            @Field("grant_type") String grantType);

    @FormUrlEncoded
    @POST("/o/token/")
    Call<AccessToken> getRefreshAccessToken(
            @Field("refresh_token") String refreshToken,
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret,
            @Field("grant_type") String grantType);

}