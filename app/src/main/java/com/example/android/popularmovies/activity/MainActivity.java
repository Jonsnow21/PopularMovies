package com.example.android.popularmovies.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.fragment.DetailFragment;
import com.example.android.popularmovies.fragment.MovieFragment;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.utils.SharedPrefUtil;

import static com.example.android.popularmovies.utils.Constants.PAN_KEY;

public class MainActivity extends AppCompatActivity implements MovieFragment.DetailFragmentCallback{

    public final static String DF_TAG = "detail_fragment";
    private boolean mTwoPane = false;
    private SharedPrefUtil sharedPrefUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPrefUtil = new SharedPrefUtil(this);

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
            sharedPrefUtil.setBooleanPreference(PAN_KEY, mTwoPane);
            if (savedInstanceState != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailFragment(), DF_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            sharedPrefUtil.setBooleanPreference(PAN_KEY, mTwoPane);
            getSupportActionBar().setElevation(0f);
        }
    }

    @Override
    public void onItemSelected(Movie movie) {
        if (mTwoPane) {
            DetailFragment df;
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            df = new DetailFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("Movie", movie);
            df.setArguments(bundle);
            fragmentTransaction.replace(R.id.movie_detail_container, df).commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class).putExtra(Intent.EXTRA_TEXT, movie);
            startActivity(intent);
        }
    }
}
