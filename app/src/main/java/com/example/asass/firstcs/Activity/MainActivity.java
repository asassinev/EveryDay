package com.example.asass.firstcs.Activity;

import android.app.Activity;
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
import com.example.asass.firstcs.model.Token;
import com.example.asass.firstcs.utils.APIUtils;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends Activity {

    Button FBbutton;
    Button Auth, Reg, VKbutton;
    EditText Login, Password;
    FacebookCallback callbackManager2;
    CallbackManager callbackManager;

    static private String login, password;
    private static String[] scope = new String[]{VKScope.PHOTOS, VKScope.EMAIL};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setView();
        buttonListener();
    }

    public void setView(){
        FBbutton = (Button) findViewById(R.id.FB_Button);
        VKbutton = (Button) findViewById(R.id.VK_Button);
        Auth = (Button) findViewById(R.id.Authorize);
        Reg = (Button) findViewById(R.id.Registration);
        Login = (EditText) findViewById(R.id.Login);
        Password = (EditText) findViewById(R.id.Password);
    }

    public void buttonListener(){
        FBbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callbackManager2 = new FacebookCallback<LoginResult>() {
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
                    public void onError(FacebookException error) {
                        Toast("Ошибка!");
                    }
                };
            }
        });

        VKbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    VKSdk.login(MainActivity.this,scope);
                } else {
                    Toast("Подключите интернет!");
                }
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
        Retrofit retrofit = APIUtils.getRetrofit();
        ServerApi auth = retrofit.create(ServerApi.class);
        Call<Token> call = auth.getUser(login, password);
        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if (response.isSuccessful()) {
                    response.body();
                    String accessToken = response.body().getAccessToken();
                    String refreshToken = response.body().getRefreshToken();
                    Toast("Добро пожаловать!");

                    Intent intent = new Intent(MainActivity.this, Profile.class);
                    intent.putExtra("login", login);
                    intent.putExtra("accessToken", accessToken);
                    intent.putExtra("refreshToken", refreshToken);
                    startActivity(intent);
                } else {
                    switch(response.code()) {
                        case 400:
                            Toast("Неправильный логин или пароль!");
                            break;
                        case 500:
                            Toast("Пожалуйста, повторите позже!");
                            break;
                    }
                    System.out.println(response.errorBody());
                }
        }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Toast("Ошибка сети");
            }
        });
    }
}
