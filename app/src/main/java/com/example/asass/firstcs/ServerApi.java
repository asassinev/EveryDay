package com.example.asass.firstcs;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface ServerApi {

    @GET("/get_user_details")
    Call<User> getUserDetails(@Query("login") String UserName, @Query("password") String Password);

}
