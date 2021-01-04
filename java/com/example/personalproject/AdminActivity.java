package com.example.personalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class AdminActivity extends AppCompatActivity {
    private DatabaseReference announceRef;

    EditText titleET, contentET;
    Button postannounce;
    String title, content, announce_date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Intent getIntent = getIntent();
        announceRef = FirebaseDatabase.getInstance().getReference("announce");

        titleET = (EditText)findViewById(R.id.announce_title);
        contentET = (EditText)findViewById(R.id.announce_post);
        postannounce = (Button)findViewById(R.id.announce_button);

        postannounce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((titleET.getText().toString().length() * contentET.getText().toString().length()) == 0) {
                    Toast.makeText(getApplicationContext(), "공지를 작성해주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    title = titleET.getText().toString();
                    content = contentET.getText().toString();
                    Date currentTime = Calendar.getInstance().getTime();
                    SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
                    SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.getDefault());
                    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
                    String year = yearFormat.format(currentTime);
                    String month = monthFormat.format(currentTime);
                    String day = dayFormat.format(currentTime);
                    announce_date = year+"."+month+"."+day+".";
                    postAnnouncement();
                }
            }
        });

    }

    public void postAnnouncement() {
        Map<String, Object> postvalues = null;
        AnnounceInfo announce = new AnnounceInfo(title, content, announce_date);
        postvalues = announce.toMap();

        DatabaseReference newAnnounce = announceRef.push();
        newAnnounce.updateChildren(postvalues);
        Toast.makeText(AdminActivity.this, "업데이트 완료", Toast.LENGTH_SHORT).show();

        titleET.setText("");
        contentET.setText("");
        title="";
        content="";
        announce_date="";
    }
}
