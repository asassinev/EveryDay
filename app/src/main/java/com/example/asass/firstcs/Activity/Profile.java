package com.example.asass.firstcs.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.asass.firstcs.R;
import com.example.asass.firstcs.model.Token;
import com.example.asass.firstcs.model.Tweet;
import com.example.asass.firstcs.model.User;

import java.util.ArrayList;
import java.util.List;

import com.example.asass.firstcs.Adapter.MyAdapter;

import static com.example.asass.firstcs.utils.APIUtils.*;

public class Profile extends Activity {

    protected static EditText customDialog_Header, customDialog_Body;
    private Button customDialog_Update, customDialog_Dismiss;
    TextView Head, Body;
    FloatingActionButton floatingActionButton1;
    protected static User user = new User();
    protected static Token token = new Token();
    protected static RecyclerView recyclerView;
    protected static List<Tweet> data = new ArrayList<>();
    static final int CUSTOM_DIALOG_ID = 0;
    private MyAdapter adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        setView();
        getExtra();
        initRecyclerView();

        floatingActionButton1 = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(CUSTOM_DIALOG_ID);
            }
        });
    }

    private void setView(){
        Head = (TextView) findViewById(R.id.head);
        Body = (TextView) findViewById(R.id.body);
    }

    public void getExtra() {
        Intent intent = getIntent();
        user.setLogin(intent.getStringExtra("login"));
        token.setAccessToken(intent.getStringExtra("accessToken"));
        token.setRefreshToken(intent.getStringExtra("refreshToken"));
    }

    private Button.OnClickListener customDialog_UpdateOnClickListener
            = new Button.OnClickListener(){

        @Override
        public void onClick(View arg0) {
            createText();
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
        Dialog dialog = null;
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
                recyclerView = (RecyclerView)findViewById(R.id.my_recycler_view);
                recyclerView.setHasFixedSize(true);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(layoutManager);
                data = new ArrayList<>();
                adapter = new MyAdapter(data);
                recyclerView.setAdapter(adapter);
                loadText();
    }
}
