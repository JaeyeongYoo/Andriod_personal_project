package com.example.personalproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {
    LayoutInflater inflater;
    Context context;
    private ArrayList<PostContentInfo> reviews;


    public ListAdapter (Context context, ArrayList<PostContentInfo> reviews){
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.reviews = reviews;
    }

    @Override
    public int getCount() {
        return reviews.size();
    }

    @Override
    public Object getItem(int position) {
        return reviews.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = inflater.inflate(R.layout.review_item_layout, parent, false);

        }
        PostContentInfo review = reviews.get(position);

        TextView tv1 = (TextView)convertView.findViewById(R.id.review_title_tv);
        TextView tv2 = (TextView)convertView.findViewById(R.id.review_movie_tv);
        TextView tv3 = (TextView)convertView.findViewById(R.id.review_writer_tv);

        tv1.setText(review.getTitle());
        tv2.setText(review.getMoviename());
        tv3.setText(review.getWriter());

        return convertView;
    }
}
