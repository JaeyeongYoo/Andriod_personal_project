package com.example.personalproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.BirthdayType;
import com.kakao.usermgmt.response.model.Profile;
import com.kakao.usermgmt.response.model.UserAccount;
import com.kakao.util.OptionalBoolean;

import java.util.ArrayList;
import java.util.Map;


public class MainPageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final int PICK_IMAGE = 777;
    final long ONE_MEGABYTE = 20480 * 20480;
    private DatabaseReference mKakaoReference;
    private StorageReference mStorageRef;
    private long lastTimeBackPressed;
    DrawerLayout drawerLayout;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter adapter;
    ImageButton writePostbt;

    Uri currentImageUri;
    String currentImageUrl;
    Bitmap currentImageBm;
    byte[] curImageBmBytes;
    ImageView profileimage;

    int tab_num = 0;
    String useremail, password, fullname, birthday, key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);

        Intent getIntent = getIntent();

        mStorageRef = FirebaseStorage.getInstance().getReference("Profiles");

        //카카오 계정//
        mKakaoReference = FirebaseDatabase.getInstance().getReference("user_list_kakao");
        UserManagement.getInstance().me(new MeV2ResponseCallback() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.e("KAKAO API", "Session closed" + errorResult);
            }

            @Override
            public void onSuccess(MeV2Response result) {

                Log.i("KAKAO API", "user id : " + result.getId());
                final UserAccount kakaoAccount = result.getKakaoAccount();
                if(kakaoAccount != null) {
                    final Profile profile = kakaoAccount.getProfile();
                    final String email= kakaoAccount.getEmail();
                    final String birthy = kakaoAccount.getBirthyear();
                    final String birthd = kakaoAccount.getBirthday();
                    //firebase에서 계정 체크//
                    mKakaoReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot checksnapshot : dataSnapshot.getChildren()){
                                FirebaseUserInfo getInfo = checksnapshot.getValue(FirebaseUserInfo.class);
                                String idCheck = getInfo.useremail;

                                if(idCheck.equals(email)){
                                    useremail = email;
                                    fullname = profile.getNickname();
                                    currentImageUrl = profile.getProfileImageUrl();
                                    if(birthy == null){
                                        birthday = birthd;
                                    }else{
                                        birthday = birthy + birthd;
                                    }

                                    return;
                                }
                            }
                            //디비에 초기 설정이 안되어있음 -> 등록
                            Map<String, Object> postvalues = null;
                            String bd;
                            if(birthy == null){ bd = birthd; }
                            else{ bd = birthy + birthd; }

                            FirebaseUserInfo post = new FirebaseUserInfo(email, "kakao", profile.getNickname(), bd);
                            postvalues = post.toMap();
                            DatabaseReference newAccount = mKakaoReference.push();
                            newAccount.updateChildren(postvalues);
                            useremail = email;
                            fullname = profile.getNickname();
                            birthday = birthy + birthd;

                            return;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    if(profile != null && email != null && birthday != null){
                        //myText.setText(profile.getNickname());
                        //emailText.setText(email);
                        //birthText.setText(birth);

                    }else if(kakaoAccount.profileNeedsAgreement() == OptionalBoolean.TRUE){

                    }else {

                    }
                }
            }
        });

        if(getIntent.getStringExtra("ID") != null){ useremail = getIntent.getStringExtra("ID");}
        if(getIntent.getStringExtra("PW") != null){ password = getIntent.getStringExtra("PW");}
        if(getIntent.getStringExtra("FN") != null){ fullname = getIntent.getStringExtra("FN"); }
        if(getIntent.getStringExtra("BD") != null){ birthday = getIntent.getStringExtra("BD"); }
        if(getIntent.getStringExtra("KakaoIMG") != null){ currentImageUrl = getIntent.getStringExtra("KakaoIMG");}
        key = getIntent.getStringExtra("KEY");
        tab_num = getIntent.getIntExtra("Tab", 0);


        Toolbar tb = (Toolbar)findViewById(R.id.main_toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, tb, R.string.app_name, R.string.app_name);
        drawerToggle.syncState();

        //profile Function 시작//
        View header = navigationView.getHeaderView(0);
        Menu menu = navigationView.getMenu();
        MenuItem fullnameItem = menu.findItem(R.id.item1);
        MenuItem birthdayItem = menu.findItem(R.id.item2);
        MenuItem emailItem = menu.findItem(R.id.item3);

        profileimage = (ImageView) header.findViewById(R.id.profileimage);

        fullnameItem.setTitle(fullname);
        birthdayItem.setTitle(birthday);
        emailItem.setTitle(useremail);

        if(currentImageUrl == null){
            mStorageRef.child(key + ".jpg").getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    curImageBmBytes = bytes;
                    profileimage.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        } else{
          Glide.with(this).load(currentImageUrl).into(profileimage);
        }


        profileimage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, PICK_IMAGE);
            }
        });


        //profile Function 끝//

        ArrayList<MovieInfo> movies = new ArrayList<MovieInfo>();
        MovieInfo movietest = new MovieInfo("해리포터", "조앤롤링", "20200611");
        MovieInfo movietest2 = new MovieInfo("날씨의 아이", "신카이마코토", "20201111");
        movies.add(movietest);
        movies.add(movietest2);

        adapter = new ViewPagerAdapter(getSupportFragmentManager(), 2, movies , fullname);
        viewPager = (ViewPager)findViewById(R.id.pager);
        viewPager.setAdapter(adapter);

        viewPager.setCurrentItem(tab_num);
        tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);


        writePostbt = (ImageButton)findViewById(R.id.writebutton);
        writePostbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tab_num = viewPager.getCurrentItem();

                Intent intent = new Intent(MainPageActivity.this, WritePostActivity.class);
                intent.putExtra("ID", useremail);
                intent.putExtra("FN", fullname);
                intent.putExtra("BD", birthday);
                intent.putExtra("Tab", tab_num);
                if (currentImageUrl!=null){
                    intent.putExtra("KakaoIMG", currentImageUrl);
                }

                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onBackPressed(){
        if(System.currentTimeMillis() - lastTimeBackPressed < 2000){
            UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                @Override
                public void onCompleteLogout() {
                    Log.i("KAKAO_API", "로그아웃 완료");
                }
            });
            finish();
            return;
        }
        lastTimeBackPressed = System.currentTimeMillis();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE) {

            currentImageUri = data.getData();
            profileimage.setImageURI(data.getData());

            //upload image
            StorageReference ref = mStorageRef.child(key + ".jpg");
            UploadTask uploadTask = ref.putFile(currentImageUri);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Toast.makeText(MainpageActivity.this, "Profile Image Register", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
