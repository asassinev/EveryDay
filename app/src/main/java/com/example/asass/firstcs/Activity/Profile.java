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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asass.firstcs.API.ServerApi;
import com.example.asass.firstcs.R;
import com.example.asass.firstcs.model.Tweet;
import com.example.asass.firstcs.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    static final int CUSTOM_DIALOG_ID = 0;
    TextView customDialog_TextView;
    EditText customDialog_Header, customDialog_Body;
    Button customDialog_Update, customDialog_Dismiss;

    private MyAdapter adapter;
    TextView Login, Head, Body;
    public String login, accessToken;
    FloatingActionButton floatingActionButton;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        Intent intent = getIntent();
        login = intent.getStringExtra("login");
        accessToken = intent.getStringExtra("accessToken");
        System.out.println("accessToken profile//" + accessToken);
        Login = (TextView) findViewById(R.id.Login);
        Head = (TextView) findViewById(R.id.head);
        Body = (TextView) findViewById(R.id.body);
        user.setLogin((String) Login.getText());
        Login.setText(login);
        data = new ArrayList<>();
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(CUSTOM_DIALOG_ID);
            }
        });
        initRecyclerView();
    }
            private Button.OnClickListener customDialog_UpdateOnClickListener
                    = new Button.OnClickListener(){

                @Override
                public void onClick(View arg0) {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    ServerApi request = retrofit.create(ServerApi.class);
                    Map<String, String> hashMap = new HashMap<>();
                    hashMap.put("head", customDialog_Header.getText().toString());
                    hashMap.put("body", customDialog_Body.getText().toString());
                    hashMap.put("login", login);
                    Call<Tweet> call = request.createText(hashMap, accessToken);
                    call.enqueue(new Callback<Tweet>() {
                        @Override
                        public void onResponse(Call<Tweet> call, Response<Tweet> response) {
                            if (response.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Добавлено!", Toast.LENGTH_SHORT).show();
                                recyclerView.getAdapter().notifyDataSetChanged();
                                loadJSON();
                            } else {
                                switch(response.code()) {
                                    case 400:
                                        Toast.makeText(getApplicationContext(), "Токен устарел!", Toast.LENGTH_SHORT).show();
                                        break;
                                    case 401:
                                        Toast.makeText(getApplicationContext(), "Неверный токен!", Toast.LENGTH_SHORT).show();
                                    case 500:
                                        // ошибка на сервере. можно использовать ResponseBody, см. ниже
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

                        dialog.setContentView(R.layout.customdialog);
                        dialog.setTitle("Добавить заметку");

                        customDialog_Header = (EditText)dialog.findViewById(R.id.dialogHeader);
                        customDialog_Body = (EditText)dialog.findViewById(R.id.dialogBody);
                        customDialog_TextView = (TextView)dialog.findViewById(R.id.dialogtextview);
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
        Call<List<Tweet>> call = request.getTweets(login, accessToken);
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
                        Toast.makeText(getApplicationContext(), "Токен устарел!", Toast.LENGTH_SHORT).show();
                        break;
                    case 401:
                        Toast.makeText(getApplicationContext(), "Неверный токен!", Toast.LENGTH_SHORT).show();
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