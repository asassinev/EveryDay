package com.example.asass.firstcs.API;

import com.example.asass.firstcs.model.Tweet;
import com.example.asass.firstcs.model.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ServerApi {

    @GET("/user/get")
    Call<User> getUser(@Query("login") String UserName, @Query("password") String Password);

    @GET("/user/create")
    Call<User> createUser(@Query("login") String UserName, @Query("password") String Password, @Query("email") String email);

    @POST("/text/create")
    Call<Tweet> createText(@FieldMap Map<String, String> text,  @Header("access_token") String access_token);

    @GET("/text/get")
    Call<List<Tweet>> getTweets(@Query("login") String Login);//,  @Header("access_token") String access_token);
}
