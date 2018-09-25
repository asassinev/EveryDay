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
import com.example.asass.firstcs.R;
import com.example.asass.firstcs.model.Tweet;
import com.example.asass.firstcs.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import com.example.asass.firstcs.Adapter.MyAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.asass.firstcs.utils.config.BASE_URL;

public class Profile extends Activity {

    public static User user = new User();
    private RecyclerView recyclerView;
    List<Tweet> data;
    private MyAdapter adapter;
    TextView Login, Head, Body;
    public String login;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        Intent intent = getIntent();
        login = intent.getStringExtra("login");
        Login = (TextView) findViewById(R.id.Login) ;
        Head = (TextView) findViewById(R.id.head) ;
        Body = (TextView) findViewById(R.id.body) ;
        user.setLogin((String) Login.getText());
        Login.setText(login);
        data = new ArrayList<>();
        initRecyclerView();
    }

    private void initRecyclerView(){
        recyclerView = (RecyclerView)findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MyAdapter(data);
        recyclerView.setAdapter(adapter);
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
        Call<List<Tweet>> call = request.getTweets(login);
        call.enqueue(new Callback<List<Tweet>>() {
            @Override
            public void onResponse(Call<List<Tweet>> call, Response<List<Tweet>> response) {
                if (response.isSuccessful()){
                data.addAll(response.body());
                recyclerView.getAdapter().notifyDataSetChanged();
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
            public void onFailure(Call<List<Tweet>> call, Throwable t) {
                Log.d("Error",t.getMessage());
            }
        });
    }
    public void Toast(String text){
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

}