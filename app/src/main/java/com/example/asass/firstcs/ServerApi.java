package com.example.asass.firstcs;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ServerApi {

    @GET("/getUser")
    Call<User> getUser(@Query("login") String UserName, @Query("password") String Password);

    @GET("/createUser")
    Call<User> createUser(@Query("login") String UserName, @Query("password") String Password);

}
