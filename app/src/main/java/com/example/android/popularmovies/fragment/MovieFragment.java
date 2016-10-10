package com.example.android.popularmovies.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.activity.SettingsActivity;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.MovieCallResult;
import com.example.android.popularmovies.rest.ApiClient;
import com.example.android.popularmovies.rest.ApiInterface;
import com.example.android.popularmovies.adapter.MovieAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static com.example.android.popularmovies.R.id.recyclerView;

public class MovieFragment extends Fragment {

    private MovieAdapter myAdapter;
    private final static String API_KEY = BuildConfig.TMDB_API_KEY;
    RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings) {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v("InOnStart", "call");
        getMovies();
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(dpToPx(0)));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        getMovies();
        return rootView;
    }

    public void getMovies() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOrder = preferences.getString(getString(R.string.sort_order), getString(R.string.pref_sort_most_popular));
        Log.v("sort", sortOrder);
        if (isOnline()) {

            final String APIID_PARAM = "api_key";

            Uri builtUri = Uri.parse(sortOrder).buildUpon()
                    .appendQueryParameter(APIID_PARAM, API_KEY)
                    .build();

            String url = builtUri.toString();

            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<MovieCallResult> call = apiService.getMovies(url);
            Log.v("URL", call.request().url().toString());
            call.enqueue(new Callback<MovieCallResult>() {
                @Override
                public void onResponse(Call<MovieCallResult> call, Response<MovieCallResult> response) {
                    Log.v("data", response.body().toString());
                    ArrayList<Movie> movies = response.body().getMovies();
                    //Log.v(TAG, movies.size() + "");
                    myAdapter = new MovieAdapter(
                            getActivity(),
                            R.layout.grid_item,
                            movies
                    );
                    recyclerView.setAdapter(myAdapter);

                }

                @Override
                public void onFailure(Call<MovieCallResult> call, Throwable t) {
                    Log.e(TAG, t.toString());
                    Log.v("onFailure", "problem");
                }
            });
        } else {
            Toast.makeText(getContext(), "Please turn on an active internet connection", Toast.LENGTH_SHORT).show();
        }

    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spacing) {
            this.spanCount = 2;
            this.spacing = spacing;
            this.includeEdge = true;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = spacing;
            outRect.right = spacing;
            outRect.bottom = spacing;

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.top = spacing;
            } else {
                outRect.top = 0;
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}