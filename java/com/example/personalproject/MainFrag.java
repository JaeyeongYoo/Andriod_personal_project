package com.example.personalproject;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class MainFrag extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<MovieInfo> movies;
    private ArrayList<AnnounceInfo> announces;

    String username;

    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;

    ListView listView;
    AnnAdapter listAdapter;

    DatabaseReference postMovieRef, announceRef;
    StorageReference postPosterRef;

    public MainFrag() {
        // Required empty public constructor
    }

    public MainFrag(ArrayList<MovieInfo> movie, String username){
        this.movies = movie;
        this.username = username;
    }

    // TODO: Rename and change types and number of parameters
    public static MainFrag newInstance(String param1, String param2) {
        MainFrag fragment = new MainFrag();
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

        movies = new ArrayList<MovieInfo>();
        announces = new ArrayList<AnnounceInfo>();
        postMovieRef = FirebaseDatabase.getInstance().getReference("movie");
        postPosterRef = FirebaseStorage.getInstance().getReference("movie");
        announceRef = FirebaseDatabase.getInstance().getReference("announce");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_main, container, false);

        recyclerView = (RecyclerView)view.findViewById(R.id.scrollview_list_movie);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true));
        getFirebaseDatabase();
        recyclerAdapter = new RecyclerAdapter(getActivity(), movies);
        recyclerAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(recyclerAdapter);

        //데이터 베이스에서 공지사항 얻기
        getAnnounceDatabase();
        listView = (ListView)view.findViewById(R.id.announceList);
        listAdapter = new AnnAdapter(getActivity(), announces);
        listAdapter.notifyDataSetChanged();
        listView.setAdapter(listAdapter);

        return view;
    }

    public void getFirebaseDatabase(){
        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                movies.clear();
                for(DataSnapshot postSnapShot : dataSnapshot.getChildren()){
                    String key_moviename = postSnapShot.getKey();
                    final MovieInfo getmovie = postSnapShot.getValue(MovieInfo.class);
                    movies.add(getmovie);
                }
                recyclerAdapter.notifyDataSetChanged();;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        postMovieRef.addValueEventListener(postListener);
    }

    public void getAnnounceDatabase(){
        final ValueEventListener announceListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                announces.clear();
                for(DataSnapshot postSnapShot : dataSnapshot.getChildren()){
                    String key_moviename = postSnapShot.getKey();
                    AnnounceInfo getannounce = postSnapShot.getValue(AnnounceInfo.class);
                    announces.add(0, getannounce);
                }
                listAdapter.notifyDataSetChanged();;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        announceRef.addValueEventListener(announceListener);
    }
}
