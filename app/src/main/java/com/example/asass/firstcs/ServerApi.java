package com.example.asass.firstcs;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ServerApi {

    @GET("/getUser")
    Call<User> getUser(@Query("login") String UserName, @Query("password") String Password);

    @GET("/createUser")
    Call<User> createUser(@Query("login") String UserName, @Query("password") String Password);

    @POST("/createText")
    Call<message> createText(@FieldMap Map<String, String> text);

    @GET("/getText")
    Call<message> getText(@Query("Author") String Author);
}
