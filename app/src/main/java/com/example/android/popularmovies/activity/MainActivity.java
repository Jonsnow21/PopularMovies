package com.example.android.popularmovies.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.fragment.MovieFragment;

public class MainActivity extends AppCompatActivity {

    private final static String MF = "movie_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if( savedInstanceState == null ){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MovieFragment())
                    .commit();
        }
        getSupportActionBar().setElevation(0f);
    }
}
