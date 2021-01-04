package com.example.personalproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Movie;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private Context context;
    private ArrayList<MovieInfo> movies;
    private StorageReference posterImageRef = FirebaseStorage.getInstance().getReference();

    final long ONE_MEGABYTE = 20480 * 20480;

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv1, tv2, tv3, username_tv;
        ImageView tv4, profileimg_tv;

        ViewHolder(View itemView){
            super(itemView);

            tv1 = itemView.findViewById(R.id.moviename_tv);
            tv2 = itemView.findViewById(R.id.director_tv);
            tv3 = itemView.findViewById(R.id.date_tv);
            tv4 = itemView.findViewById(R.id.posterimg_tv);

        }
    }

    RecyclerAdapter(Context context, ArrayList<MovieInfo> items){
        this.context = context;
        this.movies = new ArrayList<MovieInfo>();
        this.movies = items;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movieitem_layout, parent, false);
        RecyclerAdapter.ViewHolder holder = new RecyclerAdapter.ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerAdapter.ViewHolder holder, int position) {
        MovieInfo movie = movies.get(position);
        holder.tv1.setText(movie.getMoviename());
        holder.tv2.setText(movie.getDirector());
        holder.tv3.setText(movie.getDate());

        posterImageRef.child("movie/"+movie.moviename+".jpg").getBytes(ONE_MEGABYTE).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.tv4.setImageBitmap(bitmap);
            }
        });

    }


    @Override
    public int getItemCount() {
        return movies.size();
    }
}
