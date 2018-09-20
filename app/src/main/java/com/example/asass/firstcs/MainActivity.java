package com.example.asass.firstcs;
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
import android.widget.TextView;
import android.widget.Toast;
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


public class MainActivity extends Activity {

    TextView label;
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
        LoginButton = (LoginButton) findViewById(R.id.login_button);
        VKbutton = (ImageButton) findViewById(R.id.VK_button);
        Auth = (Button) findViewById(R.id.Authorize);
        Reg = (Button) findViewById(R.id.Registration);
        Login = (EditText) findViewById(R.id.Login);
        Password = (EditText) findViewById(R.id.Password);
        label = (TextView) findViewById(R.id.internet_access);
        callbackManager = CallbackManager.Factory.create();
        LoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(getApplicationContext(), "Успешно", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, Profile.class);
                startActivity(intent);
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Пожалуйста повторите попытку", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(getApplicationContext(), "Ошибка!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void AuthVK(View view) {
        VKSdk.login(this,scope);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    static private String BASE_URL = "http://79.139.231.194:10202";
    public void Auth(View view) {
            if (isNetworkAvailable()) {
                login = Login.getText().toString();
                password = Password.getText().toString();

                if (login.equals("") || password.equals("")) {
                    Toast.makeText(getApplicationContext(), "Введите логин и пароль.", Toast.LENGTH_SHORT).show();
                } else {
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
                                Toast.makeText(getApplicationContext(), "Добро пожаловать!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(MainActivity.this, Profile.class);
                                startActivity(intent);
                            } else {
                                switch(response.code()) {
                                    case 400:
                                        Toast.makeText(getApplicationContext(), "Неправильный логин или пароль!", Toast.LENGTH_LONG).show();
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
                            Toast.makeText(MainActivity.this, "An error occurred during networking", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else
            {
                Toast.makeText(getApplicationContext(), "Подключите интернет!", Toast.LENGTH_LONG).show();
            }
    }

    public void Reg(View view){
        Intent intent = new Intent(MainActivity.this, Registration.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                Toast.makeText(getApplicationContext(),"Успешно",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, Profile.class);
                startActivity(intent);
            }
            @Override
            public void onError(VKError error) {
                Toast.makeText(getApplicationContext(),"Ошибка!",Toast.LENGTH_LONG).show();
            }
        })) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
