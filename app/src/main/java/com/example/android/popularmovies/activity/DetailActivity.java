package com.example.android.popularmovies.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

public class DetailActivity extends Activity {

    ImageView thumbnail, backdropImage;
    TextView movieTitle, releaseDate, movieDescription;
    RatingBar movieRating;
    private final String LOG_TAG = DetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        Movie movie = intent.getParcelableExtra(Intent.EXTRA_TEXT);

        thumbnail = (ImageView) findViewById(R.id.movie_thumbnail);
        movieTitle = (TextView) findViewById(R.id.movie_title);
        releaseDate = (TextView) findViewById(R.id.movie_release_date);
        movieDescription = (TextView) findViewById(R.id.movie_description);
        movieRating = (RatingBar) findViewById(R.id.movie_rating);
        backdropImage = (ImageView) findViewById(R.id.movie_backdrop_image);

        movieTitle.setText(movie.getTitle());
        releaseDate.setText(movie.getReleaseDate());
        movieDescription.setText(movie.getOverview());
        //movieRating.setRating(movie.getVoteAverage());
        //Log.v(LOG_TAG, movie.getVoteAverage().toString());

        Picasso.with( this )
                .load( movie.getPosterPath())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder_error)
                .into(thumbnail);

        Picasso.with(this)
                .load(movie.getBackdropPath())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder_error)
                .into(backdropImage);
    }
}
