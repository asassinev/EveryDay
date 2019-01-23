package com.example.asass.firstcs.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.asass.firstcs.API.ServerApi;
import com.example.asass.firstcs.R;
import com.example.asass.firstcs.model.Token;
import com.example.asass.firstcs.model.User;
import com.example.asass.firstcs.utils.APIUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends Activity {

    protected static User user = new User();
    EditText Login, Password;
    private Context context;

    static private String login, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;
        init();
    }

    public void init(){
        Login = (EditText) findViewById(R.id.Login);
        Password = (EditText) findViewById(R.id.Password);

        findViewById(R.id.Authorize).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Auth(context);
            }
        });
        findViewById(R.id.Registration).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Registration(context);
            }
        });
    }

    public void Auth(Context context) {
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

    public void Registration(Context context) {
        Intent intent = new Intent(MainActivity.this, Registration.class);
        startActivity(intent);
    }

    public void Toast(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
                }
        }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Toast("Ошибка сети");
            }
        });
    }
}
