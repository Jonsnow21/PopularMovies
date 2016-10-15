package com.example.android.popularmovies.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.adapter.TrailerAdapter;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.Trailer;
import com.example.android.popularmovies.model.TrailersCallResult;
import com.example.android.popularmovies.rest.ApiClient;
import com.example.android.popularmovies.rest.ApiInterface;
import com.example.android.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static com.example.android.popularmovies.R.string.trailers;
import static com.example.android.popularmovies.utils.Constants.APIID_PARAM;
import static com.example.android.popularmovies.utils.Constants.API_KEY;

public class DetailActivity extends AppCompatActivity {

    private ImageView thumbnail, backdropImage;
    private TextView movieTitle, releaseDate, movieDescription;
    private RatingBar movieRating;
    private Movie movie;
    private final String LOG_TAG = DetailActivity.class.getSimpleName();
    private NetworkUtils networkUtils;
    private TrailerAdapter trailerAdapter;
    private RecyclerView recyclerView;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        networkUtils = new NetworkUtils();
        mContext = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initCollapsingToolbar();

        Intent intent = getIntent();
        movie = intent.getParcelableExtra(Intent.EXTRA_TEXT);

        thumbnail = (ImageView) findViewById(R.id.movie_thumbnail);
        movieTitle = (TextView) findViewById(R.id.movie_title);
        releaseDate = (TextView) findViewById(R.id.movie_release_date);
        movieDescription = (TextView) findViewById(R.id.movie_description);
        movieRating = (RatingBar) findViewById(R.id.movie_rating);
        backdropImage = (ImageView) findViewById(R.id.movie_backdrop_image);

        movieTitle.setText(movie.getTitle());
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

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_trailer);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        int id = movie.getId();
        getTrailers(id);
    }

    private void getTrailers(int id) {
        if (networkUtils.isOnline(this)) {
            String trailerString = id + "/videos";
            Uri builtUri = Uri.parse(trailerString).buildUpon()
                    .appendQueryParameter(APIID_PARAM, API_KEY)
                    .build();
            String url = builtUri.toString();

            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<TrailersCallResult> call = apiService.getVideos(url);
            Log.v("URL", call.request().url().toString());
            call.enqueue(new Callback<TrailersCallResult>() {
                @Override
                public void onResponse(Call<TrailersCallResult> call, Response<TrailersCallResult> response) {
                    Log.v("data", response.body().toString());
                    ArrayList<Trailer> trailers = response.body().getTrailers();
                    trailerAdapter = new TrailerAdapter(
                            mContext,
                            R.layout.list_item_trailer,
                            trailers
                    );
                    recyclerView.setAdapter(trailerAdapter);
                }

                @Override
                public void onFailure(Call<TrailersCallResult> call, Throwable t) {
                    Log.e(TAG, t.toString());
                    Log.v("onFailure", "problem");
                }
            });
        } else {
            Toast.makeText(this, "Please turn on an active internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    /*private void getReviews(int id) {
        if (networkUtils.isOnline(this)) {
            String trailerString = id + "/reviews";
            Uri builtUri = Uri.parse(trailerString).buildUpon()
                    .appendQueryParameter(APIID_PARAM, API_KEY)
                    .build();
            String url = builtUri.toString();

            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<TrailersCallResult> call = apiService.getVideos(url);
            Log.v("URL", call.request().url().toString());
            call.enqueue(new Callback<TrailersCallResult>() {
                @Override
                public void onResponse(Call<TrailersCallResult> call, Response<TrailersCallResult> response) {
                    Log.v("data", response.body().toString());
                    List<Trailer> trailers = response.body().getTrailers();
                    *//*myAdapter = new MovieAdapter(
                            getActivity(),
                            R.layout.grid_item,
                            movies
                    );
                    recyclerView.setAdapter(myAdapter);*//*
                }

                @Override
                public void onFailure(Call<TrailersCallResult> call, Throwable t) {
                    Log.e(TAG, t.toString());
                    Log.v("onFailure", "problem");
                }
            });
        } else {
            Toast.makeText(this, "Please turn on an active internet connection", Toast.LENGTH_SHORT).show();
        }
    }*/

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
                    movieTitle.setVisibility(View.GONE);
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    movieTitle.setVisibility(View.VISIBLE);
                    isShow = false;
                }
            }
        });
    }

}
