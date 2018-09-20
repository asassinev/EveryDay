package com.example.asass.firstcs;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Registration extends Activity {

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private ProgressDialog pDialog;

    Button Regbut;
    EditText Login, Password;


    private static String BASE_URL = "http://79.139.231.194:10202";
    private String login,password;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);

        Regbut = (Button) findViewById(R.id.regbut);
        Login = (EditText) findViewById(R.id.login);
        Password = (EditText) findViewById(R.id.password);

        Regbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    login = Login.getText().toString();
                    password = Password.getText().toString();
                    if (login.equals("") || password.equals("")) {
                        Toast.makeText(getApplicationContext(), "Введите логин и пароль.", Toast.LENGTH_LONG).show();
                    } else {
                        Gson gson = new GsonBuilder()
                                .setLenient()
                                .create();

                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(BASE_URL)
                                .addConverterFactory(GsonConverterFactory.create(gson))
                                .build();

                        ServerApi reg = retrofit.create(ServerApi.class);
                        reg.createUser(login, password).enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Добро пожаловать!", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(Registration.this, Profile.class);
                                    startActivity(intent);
                                } else {
                                    switch(response.code()) {
                                        case 400:
                                            Toast.makeText(getApplicationContext(), "Логин занят!", Toast.LENGTH_LONG).show();
                                            break;
                                        case 500:
                                            // ошибка на сервере. можно использовать ResponseBody, см. ниже
                                            break;
                                    }
                                    System.out.println(response.errorBody());
                                }
                            }
                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                Toast.makeText(Registration.this, "An error occurred during networking", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else
                {
                    Toast.makeText(getApplicationContext(), "Подключите интернет!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}



