package com.example.personalproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReadPostActivity extends AppCompatActivity {
    TextView titleTV, movienameTV, writerTV, contentTV, tagTV;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readpost);

        Intent getIntent = getIntent();

        titleTV = (TextView)findViewById(R.id.read_title);
        movienameTV = (TextView)findViewById(R.id.read_moviename);
        writerTV = (TextView)findViewById(R.id.read_writer);
        contentTV = (TextView)findViewById(R.id.read_post);
        tagTV = (TextView)findViewById(R.id.read_tag);

        titleTV.setText(getIntent.getStringExtra("Title"));
        movienameTV.setText(getIntent.getStringExtra("MovieName"));
        writerTV.setText("작성자: "+getIntent.getStringExtra("Writer"));
        contentTV.setText(getIntent.getStringExtra("Content"));
        tagTV.setText(getIntent.getStringExtra("Tag"));


    }
}
