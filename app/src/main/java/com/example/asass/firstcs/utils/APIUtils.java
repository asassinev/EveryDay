package com.example.asass.firstcs.utils;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.example.asass.firstcs.API.ServerApi;
import com.example.asass.firstcs.Activity.Profile;
import com.example.asass.firstcs.model.Token;
import com.example.asass.firstcs.model.Tweet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.asass.firstcs.utils.config.BASE_URL;

public class APIUtils extends Profile{
    public static Retrofit getRetrofit() {
        Gson gson = new GsonBuilder()
        .setLenient()
        .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit;
    }

    public static Token updateToken() {
        Retrofit retrofit = getRetrofit();
        ServerApi request = retrofit.create(ServerApi.class);

        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("refreshToken", token.getRefreshToken());

        Call<Token> call = request.getNewToken(hashMap);
        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if (response.isSuccessful()){
                    response.body();
                    token.setAccessToken(response.body().getAccessToken());
                    token.setRefreshToken(response.body().getRefreshToken());
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {

                //Toast("Ошибка сети", context);
            }
        });
        return token;
    }

    public static void Toast(String text, Context context){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void loadText(){
        Retrofit retrofit = getRetrofit();
        ServerApi request = retrofit.create(ServerApi.class);

        Call<List<Tweet>> call = request.getText(user.getLogin(), token.getAccessToken());
        call.enqueue(new Callback<List<Tweet>>() {
            @Override
            public void onResponse(Call<List<Tweet>> call, Response<List<Tweet>> response) {
                if (response.isSuccessful()){
                    data.clear();
                    data.addAll(response.body());
                    recyclerView.getAdapter().notifyDataSetChanged();
                } else {
                    switch(response.code()) {
                        case 400:
                            //old token
                            token = updateToken();
                            loadText();
                            break;
                        case 401:
                            //invalid token
                            //Toast("Пожалуйста, авторизируйтесь заново!", context);
                        case 500:
                            //Toast("Пожалуйста, повторите позже!", context);
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Tweet>> call, Throwable t) {
                //Toast("Ошибка сети", context);
            }
        });
    }

    public static void createText() {
        Retrofit retrofit = APIUtils.getRetrofit();
        ServerApi request = retrofit.create(ServerApi.class);

        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("head", customDialog_Header.getText().toString());
        hashMap.put("body", customDialog_Body.getText().toString());
        hashMap.put("login", user.getLogin());

        Call<Tweet> call = request.createText(hashMap, token.getAccessToken());
        call.enqueue(new Callback<Tweet>() {
            @Override
            public void onResponse(Call<Tweet> call, Response<Tweet> response) {
                if (response.isSuccessful()){
                    //Toast("Добавлено!", context);
                    loadText();
                    recyclerView.getAdapter().notifyDataSetChanged();
                    customDialog_Header.setText("");
                    customDialog_Body.setText("");
                } else {
                    switch(response.code()) {
                        case 400:
                            //old token
                            token = updateToken();
                            createText();
                            break;
                        case 401:
                            //invalid token
                            //Toast("Пожалуйста, авторизируйтесь заново!", context);
                        case 500:
                            //Toast("Пожалуйста, повторите позже!", context);
                            break;
                    }
                    System.out.println(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Tweet> call, Throwable t) {
                Log.d("Error",t.getMessage());
            }
        });
    }
    public static void deleteText(String time){
        Retrofit retrofit = APIUtils.getRetrofit();
        ServerApi request = retrofit.create(ServerApi.class);
        Call<Tweet> call = request.deleteText(user.getLogin() ,time, token.getAccessToken());
        call.enqueue(new Callback<Tweet>() {
            @Override
            public void onResponse(Call<Tweet> call, Response<Tweet> response) {
                if (response.isSuccessful()){
                    recyclerView.getAdapter().notifyDataSetChanged();

                } else {
//                    Toast("Error", context);
                }
            }

            @Override
            public void onFailure(Call<Tweet> call, Throwable t) {

            }
        });
    }


}
