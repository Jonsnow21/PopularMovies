package com.example.android.popularmovies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends ArrayAdapter<Movie> {

    public MovieAdapter( Context mContext, List<Movie> movieList ){
        super( mContext, R.layout.grid_item, movieList);
    }

    public static class ViewHolder{
        ImageView imageView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        Movie movie = getItem(position);
        ViewHolder viewHolder;
        String url = movie.getPosterPath();

        if( convertView == null ) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from( getContext());
            convertView = inflater.inflate(R.layout.grid_item, parent, false);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.movie_poster);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Picasso.with( getContext())
                .load(url)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder_error)
                .into(viewHolder.imageView);

        return convertView;
    }
}
