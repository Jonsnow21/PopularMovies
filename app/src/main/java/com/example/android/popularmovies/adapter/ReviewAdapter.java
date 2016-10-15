package com.example.android.popularmovies.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.activity.DetailActivity;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.Review;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by neeraj on 15/10/16.
 */

public class ReviewAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> movieList;
    private Context context;
    private int layout;

    public ReviewAdapter(Context mContext, int layout, List<Movie> movieList ){
        this.movieList = movieList;
        this.context = mContext;
        this.layout = layout;
//        Log.v("MovieList", movieList.size() + "");
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        View view;

        public MovieViewHolder(View v) {
            super(v);
            view = v;
            imageView = (ImageView) v.findViewById(R.id.movie_poster);
        }
    }

    @Override
    public MovieAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new MovieAdapter.MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapter.MovieViewHolder holder, final int position) {

        Picasso.with(context)
                .load(movieList.get(position).getPosterPath())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder_error)
                .into(holder.imageView);

        holder.view.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class).putExtra(Intent.EXTRA_TEXT, movieList.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        int count = movieList.size();
        return count;
    }
}