package com.example.asass.firstcs.Adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.asass.firstcs.R;
import com.example.asass.firstcs.model.Tweet;

import java.util.List;

import static com.example.asass.firstcs.utils.APIUtils.deleteText;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ItemHolder> {

    private List<Tweet> tweets;
    public MyAdapter(List<Tweet> tweets) {
        this.tweets = tweets;
    }

    @Override
    public MyAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_text_view, parent, false);

        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(MyAdapter.ItemHolder holder, int i) {
        holder.head.setText(tweets.get(i).getHead());
        holder.body.setText(tweets.get(i).getBody());
        String time = tweets.get(i).getTextCreatedAt();
        holder.textCreatedAt.setText(time);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        TextView head,body,textCreatedAt;
        CardView mCard;

        public ItemHolder(View v) {
            super(v);

            mCard = (CardView) v.findViewById(R.id.card);
            head = (TextView)v.findViewById(R.id.head);
            body = (TextView)v.findViewById(R.id.body);
            textCreatedAt = (TextView)v.findViewById(R.id.textCreatedAt);

            mCard.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                switch (v.getId()) {
                    case R.id.card:
                        itemClick(position);
                        break;
                }
            }
        }

        private void itemClick(int position){
            Tweet tweet = tweets.get(position);
            deleteText(tweet.getTextCreatedAt());
        }
    }
}

