package com.example.android.popularmovies.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.activity.DetailActivity;
import com.example.android.popularmovies.adapter.ReviewAdapter;
import com.example.android.popularmovies.adapter.TrailerAdapter;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.Review;
import com.example.android.popularmovies.model.ReviewsCallResult;
import com.example.android.popularmovies.model.Trailer;
import com.example.android.popularmovies.model.TrailersCallResult;
import com.example.android.popularmovies.rest.ApiClient;
import com.example.android.popularmovies.rest.ApiInterface;
import com.example.android.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static com.example.android.popularmovies.utils.Constants.APIID_PARAM;
import static com.example.android.popularmovies.utils.Constants.API_KEY;

public class DetailFragment extends Fragment {

    private Context mContext;
    private ImageView thumbnail;
    private TextView movieTitle, releaseDate, movieDescription, noTrailers, noReviews;
    private RatingBar movieRating;
    private FloatingActionButton favouriteButton;
    private CoordinatorLayout detailContentCoordinatorLayout;
    private RecyclerView recyclerViewForTrailers, recyclerViewForReviews;
    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;
    private Movie movie;
    private NetworkUtils networkUtils;
    private final String LOG_TAG = DetailFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.detail_content, container, false);
        Bundle bundle = getArguments();
        if(bundle != null) {
            movie = bundle.getParcelable("Movie");
        } else {
            Log.v(LOG_TAG, "null Movie");
        }

        networkUtils = new NetworkUtils();
        mContext = getContext();

        movieTitle = (TextView) rootView.findViewById(R.id.movie_title);
        thumbnail = (ImageView) rootView.findViewById(R.id.movie_thumbnail);
        releaseDate = (TextView) rootView.findViewById(R.id.movie_release_date);
        movieDescription = (TextView) rootView.findViewById(R.id.movie_description);
        movieRating = (RatingBar) rootView.findViewById(R.id.movie_rating);
        noTrailers = (TextView) rootView.findViewById(R.id.no_trailers);
        noReviews = (TextView) rootView.findViewById(R.id.no_reviews);
        favouriteButton = (FloatingActionButton) rootView.findViewById(R.id.fab);
        detailContentCoordinatorLayout = (CoordinatorLayout) rootView
                .findViewById(R.id.detail_content_coordinator_layout);

        movieTitle.setText(movie.getTitle());
        releaseDate.setText(movie.getReleaseDate());
        movieDescription.setText(movie.getOverview());
        movieRating.setRating(movie.getVoteAverage());

        Picasso.with(mContext)
                .load( movie.getPosterPath())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder_error)
                .into(thumbnail);

        recyclerViewForTrailers = (RecyclerView) rootView.findViewById(R.id.recyclerView_trailer);
        recyclerViewForTrailers.setLayoutManager(new LinearLayoutManager(mContext));

        recyclerViewForReviews = (RecyclerView) rootView.findViewById(R.id.recyclerView_reviews);
        recyclerViewForReviews.setLayoutManager(new LinearLayoutManager(mContext));

        favouriteButton.setOnClickListener(makeFavourite);
        int resId = movie.isFavourite() ? R.drawable.ic_star_white_36dp : R.drawable.ic_star_border_white_36dp;
        favouriteButton.setImageResource(resId);

        int id = movie.getId();
        if (networkUtils.isOnline(getActivity())) {
            getTrailers(id);
            getReviews(id);
        } else {
            Snackbar.make(detailContentCoordinatorLayout, "Please turn on an active internet connection",
                    Snackbar.LENGTH_SHORT).show();
        }

        return rootView;
    }

    View.OnClickListener makeFavourite = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Todo : Add movie to favourite database
            setDrawable();
        }
    };

    private void setDrawable() {
        if (movie.isFavourite()) {
            movie.setFavourite(false);
            favouriteButton.setImageResource(R.drawable.ic_star_border_white_36dp);
            Snackbar.make(detailContentCoordinatorLayout, "Movie removed from favourite", Snackbar.LENGTH_SHORT).show();
        } else {
            movie.setFavourite(true);
            favouriteButton.setImageResource(R.drawable.ic_star_white_36dp);
            Snackbar.make(detailContentCoordinatorLayout, "Movie added to favourite", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void getTrailers(int id) {

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
                if(trailers.size() == 0) {
                    recyclerViewForTrailers.setVisibility(View.GONE);
                    noTrailers.setVisibility(View.VISIBLE);
                } else {
                    trailerAdapter = new TrailerAdapter(
                            mContext,
                            R.layout.list_item_trailer,
                            trailers
                    );
                    recyclerViewForTrailers.setAdapter(trailerAdapter);
                }
            }

            @Override
            public void onFailure(Call<TrailersCallResult> call, Throwable t) {
                Log.e(TAG, t.toString());
                Log.v("onFailure", "problem");
            }
        });
    }

    private void getReviews(int id) {

        String trailerString = id + "/reviews";
        Uri builtUri = Uri.parse(trailerString).buildUpon()
                .appendQueryParameter(APIID_PARAM, API_KEY)
                .build();
        String url = builtUri.toString();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ReviewsCallResult> call = apiService.getReviews(url);
        Log.v("URL", call.request().url().toString());
        call.enqueue(new Callback<ReviewsCallResult>() {
            @Override
            public void onResponse(Call<ReviewsCallResult> call, Response<ReviewsCallResult> response) {
                Log.v("data", response.body().toString());
                ArrayList<Review> reviews = response.body().getReviews();
                if (reviews.size() == 0) {
                    recyclerViewForReviews.setVisibility(View.GONE);
                    noReviews.setVisibility(View.VISIBLE);
                } else {
                    reviewAdapter = new ReviewAdapter(
                            mContext,
                            R.layout.list_item_review,
                            reviews
                    );
                    recyclerViewForReviews.setAdapter(reviewAdapter);
                }
            }
            @Override
            public void onFailure(Call<ReviewsCallResult> call, Throwable t) {
                Log.e(TAG, t.toString());
                Log.v("onFailure", "problem");
            }
        });
    }

    public void changeVisibility(int visibility) {
        if(visibility == 0) movieTitle.setVisibility(View.GONE);
        else movieTitle.setVisibility(View.VISIBLE);
    }
}
