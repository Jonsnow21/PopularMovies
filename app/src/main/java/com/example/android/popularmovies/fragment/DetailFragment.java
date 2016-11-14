package com.example.android.popularmovies.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.adapter.ReviewAdapter;
import com.example.android.popularmovies.adapter.TrailerAdapter;
import com.example.android.popularmovies.database.MovieContract.MovieEntry;
import com.example.android.popularmovies.database.MovieDbHelper;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.Review;
import com.example.android.popularmovies.model.ReviewsCallResult;
import com.example.android.popularmovies.model.Trailer;
import com.example.android.popularmovies.model.TrailersCallResult;
import com.example.android.popularmovies.orm.MovieORM;
import com.example.android.popularmovies.rest.ApiClient;
import com.example.android.popularmovies.rest.ApiInterface;
import com.example.android.popularmovies.utils.Constants;
import com.example.android.popularmovies.utils.NetworkUtils;
import com.example.android.popularmovies.utils.SharedPrefUtil;
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
    private String mSortOrder;
    private SharedPreferences preferences;
    private SQLiteOpenHelper movieDbHelper;
    private Boolean isFavourite;
    private int id;
    private final String LOG_TAG = DetailFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.detail_content, container, false);
        Bundle bundle = getArguments();
        if(bundle != null) {
            movie = bundle.getParcelable("Movie");
            id = movie.getId();
        } else {
            Log.v(LOG_TAG, "null Movie");
        }

        mContext = getContext();
        networkUtils = new NetworkUtils();
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mSortOrder = preferences.getString(getString(R.string.sort_order), getString(R.string.pref_sort_most_popular));

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
                .load(Constants.POSTER_BASE_URL + movie.getPosterPath())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder_error)
                .into(thumbnail);

        recyclerViewForTrailers = (RecyclerView) rootView.findViewById(R.id.recyclerView_trailer);
        recyclerViewForTrailers.setLayoutManager(new LinearLayoutManager(mContext));

        recyclerViewForReviews = (RecyclerView) rootView.findViewById(R.id.recyclerView_reviews);
        recyclerViewForReviews.setLayoutManager(new LinearLayoutManager(mContext));

        if (mSortOrder.equals(mContext.getString(R.string.pref_sort_favourite))) {
            favouriteButton.setVisibility(View.GONE);
        } else {
            movieDbHelper = new MovieDbHelper(mContext);
            isFavourite = checkIfFavourite(movie);
            setDrawable();
            favouriteButton.setOnClickListener(makeFavourite);
        }

        if (networkUtils.isOnline(getActivity())) {
            getTrailers(id);
            getReviews(id);
        } else {
            Snackbar.make(detailContentCoordinatorLayout, R.string.no_internet_message, Snackbar.LENGTH_LONG).show();
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    View.OnClickListener makeFavourite = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SQLiteDatabase wdb = movieDbHelper.getWritableDatabase();
            if (isFavourite) {
                /*String query = "DELETE FROM " + MovieEntry.TABLE_NAME + " WHERE " +
                        MovieEntry.COLUMN_MOVIE_ID + " = " + id;
                wdb.rawQuery(query, null);*/
                wdb.delete(MovieEntry.TABLE_NAME, MovieEntry.COLUMN_MOVIE_ID + " = ?", new String[] {String.valueOf(movie.getId())});
                Snackbar.make(detailContentCoordinatorLayout, R.string.removed_from_favourite, Snackbar.LENGTH_SHORT).show();
                isFavourite = false;
            } else {
                wdb.insert(MovieEntry.TABLE_NAME, null, MovieORM.getMovieValue(movie));
                Snackbar.make(detailContentCoordinatorLayout, R.string.added_to_favourite, Snackbar.LENGTH_SHORT).show();
                isFavourite = true;
            }
            setDrawable();
            wdb.close();

        }
    };

    public Boolean checkIfFavourite (Movie movie){
        SQLiteDatabase rdb = movieDbHelper.getReadableDatabase();
        final String query = "SELECT * FROM " + MovieEntry.TABLE_NAME + " WHERE " +
                MovieEntry.COLUMN_MOVIE_ID + " = " + movie.getId();
        Cursor cursor = rdb.rawQuery(query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        rdb.close();
        return true;
    }

    private void setDrawable() {
        int resId = isFavourite ? R.drawable.ic_star_white_36dp : R.drawable.ic_star_border_white_36dp;
        favouriteButton.setImageResource(resId);
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
