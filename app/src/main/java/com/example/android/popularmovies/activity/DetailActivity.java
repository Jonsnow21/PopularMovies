package com.example.android.popularmovies.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    ImageView thumbnail, backdropImage;
    TextView releaseDate, movieDescription;
    RatingBar movieRating;
    private Movie movie;
    private final String LOG_TAG = DetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initCollapsingToolbar();

        Intent intent = getIntent();
        movie = intent.getParcelableExtra(Intent.EXTRA_TEXT);

        thumbnail = (ImageView) findViewById(R.id.movie_thumbnail);
        //movieTitle = (TextView) findViewById(R.id.movie_title);
        releaseDate = (TextView) findViewById(R.id.movie_release_date);
        movieDescription = (TextView) findViewById(R.id.movie_description);
        movieRating = (RatingBar) findViewById(R.id.movie_rating);
        backdropImage = (ImageView) findViewById(R.id.movie_backdrop_image);

        //movieTitle.setText(movie.getTitle());
        releaseDate.setText(movie.getReleaseDate());
        movieDescription.setText(movie.getOverview());
        movieRating.setRating(movie.getVoteAverage());

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

    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(movie.getTitle());
                    collapsingToolbar.setCollapsedTitleTextAppearance(android.R.style.TextAppearance_Material_Widget_Toolbar_Title);
                    collapsingToolbar.setCollapsedTitleTextAppearance(android.R.style.TextAppearance_DeviceDefault_Large);
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

}
