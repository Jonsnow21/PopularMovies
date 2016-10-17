package com.example.android.popularmovies.fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.activity.MainActivity;
import com.example.android.popularmovies.activity.SettingsActivity;
import com.example.android.popularmovies.adapter.MovieAdapter;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.MoviesCallResult;
import com.example.android.popularmovies.rest.ApiClient;
import com.example.android.popularmovies.rest.ApiInterface;
import com.example.android.popularmovies.utils.NetworkUtils;
import com.example.android.popularmovies.utils.SharedPrefUtil;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static com.example.android.popularmovies.utils.Constants.APIID_PARAM;
import static com.example.android.popularmovies.utils.Constants.API_KEY;
import static com.example.android.popularmovies.utils.Constants.PAN_KEY;

public class MovieFragment extends Fragment {

    private MovieAdapter myAdapter;
    private RecyclerView recyclerView;
    private String mSortOrder;
    private SharedPreferences preferences;
    private NetworkUtils networkUtils;
    private ArrayList<Movie> movies;

    public interface DetailFragmentCallback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onItemSelected(Movie movie);
    }

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        networkUtils = new NetworkUtils();
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mSortOrder = preferences.getString(getString(R.string.sort_order), getString(R.string.pref_sort_most_popular));
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        }
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        getMovies(mSortOrder);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        String sortOrder = preferences.getString(getString(R.string.sort_order), getString(R.string.pref_sort_most_popular));
        if (!sortOrder.equals(mSortOrder)) {
            mSortOrder = sortOrder;
            getMovies(mSortOrder);
        }
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

    public void getMovies(String sortOrder) {
        if (networkUtils.isOnline(getActivity())) {
            Uri builtUri = Uri.parse(sortOrder).buildUpon()
                    .appendQueryParameter(APIID_PARAM, API_KEY)
                    .build();
            String url = builtUri.toString();

            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<MoviesCallResult> call = apiService.getMovies(url);
            Log.v("URL", call.request().url().toString());
            call.enqueue(new Callback<MoviesCallResult>() {
                @Override
                public void onResponse(Call<MoviesCallResult> call, Response<MoviesCallResult> response) {
                    Log.v("data", response.body().toString());
                    movies = response.body().getMovies();

                    myAdapter = new MovieAdapter(
                            getActivity(),
                            R.layout.grid_item,
                            movies
                    );
                    recyclerView.setAdapter(myAdapter);
                    getFirstMovie();
                }

                @Override
                public void onFailure(Call<MoviesCallResult> call, Throwable t) {
                    Log.e(TAG, t.toString());
                    Log.v("onFailure", "problem");
                }
            });
        } else {
            Toast.makeText(getContext(), "Please turn on an active internet connection", Toast.LENGTH_SHORT).show();
        }
    }
    /*
    * In case of tablets if the detail fragment is empty it would be automatically set to 1st movie
    * */
    public void getFirstMovie(){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        SharedPrefUtil sharedPrefUtil = new SharedPrefUtil(getActivity());
        boolean twoPan = sharedPrefUtil.getBooleanPreference(PAN_KEY, false);
        DetailFragment df = (DetailFragment) fragmentManager.findFragmentByTag(MainActivity.DF_TAG);
        if(df == null && twoPan) {
            ((MovieFragment.DetailFragmentCallback) getActivity()).onItemSelected(movies.get(0));
        }
    }
}