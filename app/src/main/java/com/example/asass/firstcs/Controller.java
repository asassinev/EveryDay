package com.example.asass.firstcs;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Controller implements Callback<User> {

    static private String BASE_URL = "https://localhost";

    public void start(String UserName, String Password) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        System.out.print(UserName + " " + Password);
        ServerApi auth = retrofit.create(ServerApi.class);
        auth.getUserDetails(UserName,Password).enqueue(this);


    }
    @Override
    public void onResponse(Call<User> call, Response<User> response) {
        if(response.isSuccessful()) {
            User authorization = response.body();
            System.out.print("authorization:-----" + authorization);

        } else {
            System.out.println(response.errorBody());
        }
    }

    @Override
    public void onFailure(Call<User> call, Throwable t) {
        t.printStackTrace();
    }
}
