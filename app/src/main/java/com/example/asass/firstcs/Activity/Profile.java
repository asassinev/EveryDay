package com.example.asass.firstcs.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asass.firstcs.API.ServerApi;
import com.example.asass.firstcs.model.JSONResponse;
import com.example.asass.firstcs.R;
import com.example.asass.firstcs.model.Tweet;
import com.example.asass.firstcs.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;

import com.example.asass.firstcs.Adapter.MyAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.asass.firstcs.utils.config.BASE_URL;

public class Profile extends Activity {

    private RecyclerView recyclerView;
    private ArrayList<Tweet> data;
    private MyAdapter adapter;
    public static User user = new User();
    TextView Login, Head, Body;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        Intent intent = getIntent();
        String login = intent.getStringExtra("login");
        Login = (TextView) findViewById(R.id.Login) ;
        Head = (TextView) findViewById(R.id.head) ;
        Body = (TextView) findViewById(R.id.body) ;
        Login.setText(login);
        user.setLogin(Login.getText().toString());

        initRecyclerView();
    }

    private void initRecyclerView(){
        recyclerView = (RecyclerView)findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        loadJSON();
    }

    public void loadJSON(){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        ServerApi request = retrofit.create(ServerApi.class);
        Call<JSONResponse> call = request.getTweets(user.getLogin());
        call.enqueue(new Callback<JSONResponse>() {
            @Override
            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                if (response.isSuccessful()){
                JSONResponse jsonResponse = response.body();
                data = new ArrayList<>(Arrays.asList(jsonResponse.getTweets()));
                    System.out.print("BEFORE");
                    System.out.print(response.toString());
                adapter = new MyAdapter(data);
                recyclerView.setAdapter(adapter);
                } else {
                switch(response.code()) {
                    case 400:
                        Toast.makeText(getApplicationContext(), "Ошибка!", Toast.LENGTH_SHORT).show();
                        break;
                    case 500:
                        // ошибка на сервере. можно использовать ResponseBody, см. ниже
                        break;
                }
                System.out.println(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) {
                Log.d("Error",t.getMessage());
            }
        });
    }
    public void Toast(String text){
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

}