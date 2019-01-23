package com.example.asass.firstcs.API;

import com.example.asass.firstcs.model.Token;
import com.example.asass.firstcs.model.Tweet;
import com.example.asass.firstcs.model.User;
import com.vk.sdk.VKAccessToken;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ServerApi {

    @GET("/user/get")
    Call<Token> getUser(@Query("login") String UserName, @Query("password") String Password);

    @GET("/user/create")
    Call<User> createUser(@Query("login") String UserName, @Query("password") String Password, @Query("email") String email);

    @FormUrlEncoded
    @POST("/text/create")
    Call<Tweet> createText(@FieldMap Map<String, String> map, @Header("Authorization") String accessToken);

    @GET("/text/get")
    Call<List<Tweet>> getText(@Query("login") String login,  @Header("Authorization") String accessToken);

    @GET("/text/delete")
    Call<Tweet> deleteText(@Query("login") String UserName, @Query("createdAt") String time, @Header("Authorization") String accessToken);

    @POST("/user/refresh-tokens")
    Call<Token> getNewToken(@Body Map<String, String> map);



}
