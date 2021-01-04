package com.example.personalproject;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;


public class PostFrag extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ArrayList<PostContentInfo> reviews;

    ListView listView;
    ListAdapter listAdapter;

    DatabaseReference postReviewRef;

    public PostFrag() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static PostFrag newInstance(String param1, String param2) {
        PostFrag fragment = new PostFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        reviews = new ArrayList<PostContentInfo>();
        postReviewRef = FirebaseDatabase.getInstance().getReference("Post");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_post, container, false);
        getFirebaseDatabase();

        listView = (ListView)view.findViewById(R.id.listView);
        listAdapter = new ListAdapter(Objects.requireNonNull(getActivity()), reviews);
        listAdapter.notifyDataSetChanged();
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), ReadPostActivity.class);

                intent.putExtra("Title", reviews.get(position).getTitle());
                intent.putExtra("MovieName", reviews.get(position).getMoviename());
                intent.putExtra("Writer", reviews.get(position).getWriter());
                intent.putExtra("Content", reviews.get(position).getContent());
                intent.putExtra("Tag", reviews.get(position).getTag());

                startActivity(intent);
            }
        });

        return view;
    }

    public void getFirebaseDatabase(){
        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reviews.clear();
                for(DataSnapshot postSnapShot : dataSnapshot.getChildren()){

                    final PostContentInfo getreview = postSnapShot.getValue(PostContentInfo.class);

                    reviews.add(0, getreview);
                }
                listAdapter.notifyDataSetChanged();;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        postReviewRef.addValueEventListener(postListener);
    }
}
