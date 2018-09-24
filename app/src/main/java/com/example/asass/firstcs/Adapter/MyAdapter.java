package com.example.asass.firstcs.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.asass.firstcs.R;
import com.example.asass.firstcs.model.Tweet;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private ArrayList<Tweet> tweets;

    public MyAdapter(ArrayList<Tweet> tweets) {
        this.tweets = tweets;
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_text_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder holder, int i) {
        holder.head.setText(tweets.get(i).getHead());
        holder.body.setText(tweets.get(i).getBody());
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView head,body;
        public ViewHolder(View view) {
            super(view);

            head = (TextView)view.findViewById(R.id.head);
            body = (TextView)view.findViewById(R.id.body);

        }
    }

}
