package com.example.asass.firstcs.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.asass.firstcs.API.ServerApi;
import com.example.asass.firstcs.R;
import com.example.asass.firstcs.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.asass.firstcs.utils.config.BASE_URL;

public class Registration extends Activity {

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private ProgressDialog pDialog;

    Button Regbut;
    EditText Login, Password, Email;

    private String login,password, email;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);

        Regbut = (Button) findViewById(R.id.regbut);
        Login = (EditText) findViewById(R.id.login);
        Password = (EditText) findViewById(R.id.password);
        Email = (EditText) findViewById(R.id.email);

        Regbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    login = Login.getText().toString();
                    password = Password.getText().toString();
                    email = Email.getText().toString();
                    if (login.isEmpty() || password.isEmpty() || email.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Введите логин и пароль.", Toast.LENGTH_SHORT).show();
                    } else {
                        Gson gson = new GsonBuilder()
                                .setLenient()
                                .create();

                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(BASE_URL)
                                .addConverterFactory(GsonConverterFactory.create(gson))
                                .build();

                        ServerApi reg = retrofit.create(ServerApi.class);
                        reg.createUser(login, password, email).enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Добро пожаловать!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Registration.this, Profile.class);
                                    intent.putExtra("login", login);
                                    startActivity(intent);
                                } else {
                                    switch(response.code()) {
                                        case 400:
                                            Toast.makeText(getApplicationContext(), "Логин занят!", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplicationContext(), "Подключите интернет!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}



