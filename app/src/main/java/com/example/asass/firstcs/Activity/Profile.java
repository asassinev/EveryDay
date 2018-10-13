package com.example.asass.firstcs.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asass.firstcs.API.ServerApi;
import com.example.asass.firstcs.R;
import com.example.asass.firstcs.model.Token;
import com.example.asass.firstcs.model.Tweet;
import com.example.asass.firstcs.model.User;
import com.example.asass.firstcs.utils.APIUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.asass.firstcs.Adapter.MyAdapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Profile extends Activity {

    EditText customDialog_Header, customDialog_Body;
    Button customDialog_Update, customDialog_Dismiss;
    TextView Login, Head, Body;
    FloatingActionButton floatingActionButton;

    public static User user = new User();
    public Token token = new Token();
    private RecyclerView recyclerView;
    private List<Tweet> data = new ArrayList<>();;
    static final int CUSTOM_DIALOG_ID = 0;

    private MyAdapter adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        setView();
        getExtra();
        initRecyclerView();

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(CUSTOM_DIALOG_ID);
            }
        });
    }

    public void setView(){
        Login = (TextView) findViewById(R.id.Login);
        Head = (TextView) findViewById(R.id.head);
        Body = (TextView) findViewById(R.id.body);
    }

    public void getExtra() {
        Intent intent = getIntent();
        user.setLogin(intent.getStringExtra("login"));
        token.setAccessToken(intent.getStringExtra("accessToken"));
        token.setRefreshToken(intent.getStringExtra("refreshToken"));
        Login.setText(user.getLogin());
    }

    private Button.OnClickListener customDialog_UpdateOnClickListener
            = new Button.OnClickListener(){

        @Override
        public void onClick(View arg0) {
            loadText();
            dismissDialog(CUSTOM_DIALOG_ID);
        }
    };

    private Button.OnClickListener customDialog_DismissOnClickListener
            = new Button.OnClickListener(){

        @Override
        public void onClick(View arg0) {
            dismissDialog(CUSTOM_DIALOG_ID);
        }

    };

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;;
        switch(id) {
            case CUSTOM_DIALOG_ID:
                dialog = new Dialog(Profile.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.customdialog);

                customDialog_Header = (EditText)dialog.findViewById(R.id.dialogHeader);
                customDialog_Body = (EditText)dialog.findViewById(R.id.dialogBody);
                customDialog_Update = (Button)dialog.findViewById(R.id.dialogAdd);
                customDialog_Dismiss = (Button)dialog.findViewById(R.id.dialogdismiss);

                customDialog_Update.setOnClickListener(customDialog_UpdateOnClickListener);
                customDialog_Dismiss.setOnClickListener(customDialog_DismissOnClickListener);

                break;
        }
        return dialog;
    }

    private void initRecyclerView(){
        recyclerView = (RecyclerView)findViewById(R.id.my_recyle_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        data = new ArrayList<>();
        adapter = new MyAdapter(data);
        recyclerView.setAdapter(adapter);
        loadTweet();
    }

    public void Toast(String text){
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    public void loadText() {
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
                    Toast.makeText(getApplicationContext(), "Добавлено!", Toast.LENGTH_SHORT).show();
                    loadTweet();
                    recyclerView.getAdapter().notifyDataSetChanged();
                    customDialog_Header.setText("");
                    customDialog_Body.setText("");
                } else {
                    switch(response.code()) {
                        case 400:
                            //old token
                            updateToken();
                            loadText();
                            break;
                        case 401:
                            //invalid token
                            Intent intent = new Intent(Profile.this, MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(getApplicationContext(), "Пожалуйста, авторизируйтесь заново!", Toast.LENGTH_SHORT).show();
                        case 500:
                            Toast.makeText(getApplicationContext(), "Пожалуйста, повторите позже!", Toast.LENGTH_SHORT).show();
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

    public void loadTweet(){
        Retrofit retrofit = APIUtils.getRetrofit();
        ServerApi request = retrofit.create(ServerApi.class);

        Call<List<Tweet>> call = request.getTweets(user.getLogin(), token.getAccessToken());
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
                            updateToken();
                            loadTweet();
                            break;
                        case 401:
                            //invalid token
                            Intent intent = new Intent(Profile.this, MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(getApplicationContext(), "Пожалуйста, авторизируйтесь заново!", Toast.LENGTH_SHORT).show();
                        case 500:
                            Toast.makeText(getApplicationContext(), "Пожалуйста, повторите позже!", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Tweet>> call, Throwable t) {
                Toast.makeText(Profile.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateToken() {
        Retrofit retrofit = APIUtils.getRetrofit();
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
                Toast.makeText(Profile.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
            }
        });
    }

}