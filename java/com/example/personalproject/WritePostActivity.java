package com.example.personalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WritePostActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final int PICK_IMAGE = 777;
    final long ONE_MEGABYTE = 20480 * 20480;
    private DatabaseReference mMovieReference, mPostReference;
    private StorageReference mStorageRef;
    ArrayAdapter<String> spinnerAdapter;

    DrawerLayout drawerLayout;

    Spinner spinner;
    EditText titleET, postET, tagET;
    Button postbt;
    String moviename;
    String reviewMovieName, reviewTitle, reviewContent, reviewTag;

    ImageView profileimage;
    String currentImageUrl;
    Uri currentImageUri;

    String useremail, password, fullname, birthday, key;
    int tab_num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writepost);

        mStorageRef = FirebaseStorage.getInstance().getReference("Profiles");
        Intent getIntent = getIntent();
        if(getIntent.getStringExtra("ID")!=null){ useremail = getIntent.getStringExtra("ID");}
        if(getIntent.getStringExtra("PW") != null){ password = getIntent.getStringExtra("PW");}
        if(getIntent.getStringExtra("FN") != null){ fullname = getIntent.getStringExtra("FN"); }
        if(getIntent.getStringExtra("BD") != null){ birthday = getIntent.getStringExtra("BD"); }
        if(getIntent.getStringExtra("KakaoIMG") != null){ currentImageUrl = getIntent.getStringExtra("KakaoIMG");}
        key = getIntent.getStringExtra("KEY");

        tab_num = getIntent.getIntExtra("Tab", 0);


        Toolbar tb = (Toolbar)findViewById(R.id.main_toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout_write);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_writepost);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, tb, R.string.app_name, R.string.app_name);
        drawerToggle.syncState();


        //profile 시작//

        View header = navigationView.getHeaderView(0);
        Menu menu = navigationView.getMenu();
        MenuItem fullnameItem = menu.findItem(R.id.item1);
        MenuItem birthdayItem = menu.findItem(R.id.item2);
        MenuItem emailItem = menu.findItem(R.id.item3);

        profileimage = (ImageView) header.findViewById(R.id.profileimage);

        fullnameItem.setTitle(fullname);
        birthdayItem.setTitle(birthday);
        emailItem.setTitle(useremail);

        if(currentImageUrl==null){
            mStorageRef.child(key + ".jpg").getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
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

        //profile 끝//


        mMovieReference = FirebaseDatabase.getInstance().getReference("movie");
        mPostReference = FirebaseDatabase.getInstance().getReference("Post");

        final ArrayList<String> list = new ArrayList<String>();

        mMovieReference = FirebaseDatabase.getInstance().getReference("movie");
        mMovieReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot checksnapshot : dataSnapshot.getChildren()) {
                    MovieInfo movieInfo = checksnapshot.getValue(MovieInfo.class);
                    moviename = movieInfo.moviename;
                    list.add(moviename);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        list.add("영화를 선택해주세요.");

        spinner = (Spinner)findViewById(R.id.spinner);
        spinnerAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, list);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                reviewMovieName = (String) spinner.getItemAtPosition(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        postbt = (Button)findViewById(R.id.postbutton);
        postbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleET = (EditText)findViewById(R.id.review_title);
                postET = (EditText)findViewById(R.id.review_post);
                tagET = (EditText)findViewById(R.id.review_tag);

                if (reviewMovieName.equals("영화를 선택해주세요.")){
                    Toast.makeText(getApplicationContext(), "영화를 선택해주세요.", Toast.LENGTH_SHORT).show();
                } else if ((postET.getText().toString().length() * titleET.getText().toString().length())== 0){
                    Toast.makeText(getApplicationContext(), "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseReference newPostRef;
                    String postID;
                    reviewTitle = titleET.getText().toString();
                    reviewContent = postET.getText().toString();
                    reviewTag = tagET.getText().toString();
                    Map<String, Object> postvalues = null;
                    newPostRef = mPostReference.push();
                    PostContentInfo post = new PostContentInfo(reviewMovieName, reviewTitle ,reviewContent, reviewTag, fullname);
                    postvalues = post.toMap();
                    newPostRef.updateChildren(postvalues);

                    intentFunction(useremail, password, fullname, birthday, tab_num);
                }

            }
        });


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.item1:{
                break;
            }
            case R.id.item2:
                break;
            case R.id.item3:
                break;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if(tab_num==0){
            intentFunction(useremail, password, fullname, birthday, 0);
        }
        else if(tab_num==1){
            intentFunction(useremail, password, fullname, birthday, 1);
        }
    }

    public void intentFunction(String usrem, String pw, String fn, String bd, int tab){
        Intent intent = new Intent(WritePostActivity.this, MainPageActivity.class);
        intent.putExtra("ID", usrem);
        intent.putExtra("PW", pw);
        intent.putExtra("FN", fn);
        intent.putExtra("BD", bd);
        intent.putExtra("KEY", key);
        intent.putExtra("Tab", tab);
        if(currentImageUrl != null){
            intent.putExtra("KakaoIMG", currentImageUrl);
        }
        startActivity(intent);
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
