package com.example.android.popularmovies.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.popularmovies.fragment.MovieFragment;
import com.example.android.popularmovies.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if( savedInstanceState == null ){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MovieFragment() )
                    .commit();
        }
    }
}
