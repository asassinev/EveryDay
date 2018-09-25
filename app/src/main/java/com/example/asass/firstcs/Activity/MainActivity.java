package com.example.asass.firstcs.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.asass.firstcs.API.ServerApi;
import com.example.asass.firstcs.R;
import com.example.asass.firstcs.model.User;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.asass.firstcs.utils.config.BASE_URL;

public class MainActivity extends Activity {
    public static User user = new User();
    static private String login, password;
    private static String[] scope = new String[]{VKScope.PHOTOS, VKScope.EMAIL};
    ImageButton VKbutton;
    Button Auth, Reg;
    EditText Login, Password;
    LoginButton LoginButton;
    CallbackManager callbackManager;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setView();
        buttonListener();
    }

    public void setView(){
        LoginButton = (LoginButton) findViewById(R.id.login_button);
        VKbutton = (ImageButton) findViewById(R.id.VK_button);
        Auth = (Button) findViewById(R.id.Authorize);
        Reg = (Button) findViewById(R.id.Registration);
        Login = (EditText) findViewById(R.id.Login);
        Password = (EditText) findViewById(R.id.Password);
    }

    public void buttonListener(){
        callbackManager = CallbackManager.Factory.create();
        LoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast("Успешно");
                Intent intent = new Intent(MainActivity.this, Profile.class);
                startActivity(intent);
            }

            @Override
            public void onCancel() {
                Toast("Пожалуйста повторите попытку");
            }

            @Override
            public void onError(FacebookException exception) {
                Toast("Ошибка!");
            }
        });

        Auth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    login = Login.getText().toString();
                    password = Password.getText().toString();
                    if (login.isEmpty() || password.isEmpty()) {
                        Toast("Введите логин и пароль.");
                    } else {
                        loadJSON();
                    }
                } else {
                    Toast("Подключите интернет!");
                }
            }
        });

        Reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Registration.class);
                startActivity(intent);
            }
        });
    }

    public void AuthVK(View view) {
        if (isNetworkAvailable()) {
            VKSdk.login(this,scope);
        } else {
            Toast("Подключите интернет!");
        }
    }

    public void Toast(String text){
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                Toast("Успешно");
                Intent intent = new Intent(MainActivity.this, Profile.class);
                startActivity(intent);
            }

            @Override
            public void onError(VKError error) {
                Toast("Ошибка!");
            }

        })) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void loadJSON(){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ServerApi auth = retrofit.create(ServerApi.class);
        auth.getUser(login, password).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Toast("Добро пожаловать!");
                    Intent intent = new Intent(MainActivity.this, Profile.class);
                    user.setLogin(login);
                    user.setAccessToken(response.body().getAccessToken());
                    intent.putExtra("login", login);
                    startActivity(intent);
                } else {
                    switch(response.code()) {
                        case 400:
                            Toast("Неправильный логин или пароль!");
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
                Toast("An error occurred during networking");
            }
        });
    }
}
