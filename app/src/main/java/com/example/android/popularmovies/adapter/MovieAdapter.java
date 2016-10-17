package com.example.android.popularmovies.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.activity.DetailActivity;
import com.example.android.popularmovies.fragment.DetailFragment;
import com.example.android.popularmovies.fragment.MovieFragment;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.utils.SharedPrefUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.example.android.popularmovies.utils.Constants.PAN_KEY;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> movieList;
    private Context context;
    private int layout;

    public MovieAdapter( Context mContext, int layout, List<Movie> movieList ){
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
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, final int position) {

        Picasso.with(context)
                .load(movieList.get(position).getPosterPath())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder_error)
                .into(holder.imageView);

        holder.view.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MovieFragment.DetailFragmentCallback) context).onItemSelected(movieList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        int count = movieList.size();
        return count;
    }
}
