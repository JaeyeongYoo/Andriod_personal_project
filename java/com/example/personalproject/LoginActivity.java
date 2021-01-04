package com.example.personalproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.util.exception.KakaoException;

import java.security.Signature;


public class LoginActivity extends AppCompatActivity {
    private DatabaseReference mPostReference;

    EditText useremail, password;
    TextView signup;
    Button loginButton;
    String ID = "";
    String PW = "";
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent intent = getIntent();
        mPostReference = FirebaseDatabase.getInstance().getReference("user_list");

        //로그인 기능//

        useremail = (EditText)findViewById(R.id.id);
        password = (EditText)findViewById(R.id.pw);
        signup = (TextView)findViewById(R.id.signup);

        ID = intent.getStringExtra("ID");
        PW = intent.getStringExtra("PW");
        useremail.setText(ID);
        password.setText(PW);

        loginButton = (Button)findViewById(R.id.loginbutton);
        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if(useremail.getText().toString().equals("admin") && password.getText().toString().equals("admin")){
                    //관리자 계정으로
                    Toast.makeText(getApplicationContext(), "관리자", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                    startActivity(intent);
                } else {

                    mPostReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            ID = useremail.getText().toString();
                            PW = password.getText().toString();

                            for (DataSnapshot checksnapshot : dataSnapshot.getChildren()) {
                                key = checksnapshot.getKey();
                                FirebaseUserInfo getInfo = checksnapshot.getValue(FirebaseUserInfo.class);
                                String idCheck = getInfo.useremail;
                                String pwCheck = getInfo.password;

                                if (idCheck.equals(ID) && pwCheck.equals(PW)) {
                                    String fnCheck = getInfo.fullname;
                                    String bdCheck = getInfo.birthday;

                                    Intent intent = new Intent(LoginActivity.this, MainPageActivity.class);
                                    intent.putExtra("ID", idCheck);
                                    intent.putExtra("PW", pwCheck);
                                    intent.putExtra("FN", fnCheck);
                                    intent.putExtra("BD", bdCheck);
                                    intent.putExtra("KEY", key);

                                    startActivity(intent);
                                    return;
                                } else if (idCheck.equals(ID)) {
                                    ID = "";
                                    PW = "";
                                    Toast.makeText(LoginActivity.this, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                            ID = "";
                            PW = "";
                            Toast.makeText(LoginActivity.this, "등록된 계정이 없습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            }
        });
        Session.getCurrentSession().addCallback(sessionCallback);
        //로그인 기능//

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_signup = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent_signup);
            }
        });

    }

    private ISessionCallback sessionCallback = new ISessionCallback() {
        @Override
        public void onSessionOpened() {
            Log.i("KAKAO SESSION", "로그인 성공");
            //카카오 세션 open 시
            //firebase에서 해당 아이디 있는 지 찾기
            //없으면 firebase에 이메일, 이름, 비밀번호(랜덤값), 생일 입력해주기 -> mainpage로 넘어가기
            //있으면 firebase에서 해당 이메일 정보를 가지고 mainpage로 넘어가기

            Intent intent = new Intent(LoginActivity.this, MainPageActivity.class);
            startActivity(intent);
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Log.e("KAKAO SESSION", "로그인 실패", exception);
            Toast.makeText(getApplicationContext(), "some problem", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Session.getCurrentSession().removeCallback(sessionCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)){
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
