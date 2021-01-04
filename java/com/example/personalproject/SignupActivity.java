package com.example.personalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    private DatabaseReference mPostReference;

    String useremail="", password="", fullname="", birthday=""; //sign up information

    Button button;
    EditText usremET, pwET, fnET, bdET;

    ArrayList<String> data;

    private ValueEventListener checkRegister = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                FirebaseUserInfo getInfo = snapshot.getValue(FirebaseUserInfo.class);
                if (useremail.equals(getInfo.getUseremail())) {
                    Toast.makeText(SignupActivity.this, "다른 E-mail 계정을 사용해주세요.", Toast.LENGTH_SHORT).show();
                    mPostReference.removeEventListener(this);
                    return;
                }
            }

            Intent intent2 = new Intent(SignupActivity.this, LoginActivity.class);
            intent2.putExtra("ID", useremail); //id 정보 담아서 login page로 넘어가기
            postFirebaseDatabase(true); //회원가입 정보 firebase에 올리기
            startActivity(intent2);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mPostReference = FirebaseDatabase.getInstance().getReference("user_list");
        data = new ArrayList<String>();

        usremET = (EditText)findViewById(R.id.em);
        pwET = (EditText)findViewById(R.id.pw);
        fnET = (EditText)findViewById(R.id.fn);
        bdET = (EditText)findViewById(R.id.bd);

        button = (Button)findViewById(R.id.signupbt);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                useremail = usremET.getText().toString();
                password = pwET.getText().toString();
                fullname = fnET.getText().toString();
                birthday = bdET.getText().toString();

                //check blank
                if((useremail.length() * password.length() * fullname.length() * birthday.length()) == 0) {
                    Toast.makeText(SignupActivity.this, "빈 칸을 채워주세요.", Toast.LENGTH_SHORT).show();

                }else {

                    mPostReference.addListenerForSingleValueEvent(checkRegister);
                }
            }
        });

    }

    public void postFirebaseDatabase(boolean add) {
        Map<String, Object> postvalues = null;
        if(add){
            FirebaseUserInfo post = new FirebaseUserInfo(useremail, password, fullname, birthday);
            postvalues = post.toMap();
        }
        DatabaseReference newAccount = mPostReference.push();
        newAccount.updateChildren(postvalues);
        Toast.makeText(SignupActivity.this, "업데이트 완료", Toast.LENGTH_SHORT).show();

        clearET();
    }

    public void clearET() {
        usremET.setText("");
        pwET.setText("");
        fnET.setText("");
        bdET.setText("");
        useremail = "";
        password = "";
        fullname="";
        birthday="";
    }

}
