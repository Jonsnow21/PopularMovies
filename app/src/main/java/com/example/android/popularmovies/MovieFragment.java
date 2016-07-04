package com.example.android.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.zip.Inflater;

import javax.net.ssl.HttpsURLConnection;

public class MovieFragment extends Fragment {
    public MovieFragment(){
    }

    private ArrayAdapter<String> myAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate( savedInstanceState);
    }

    @Override
    public void onStart(){
        super.onStart();
        FetchMovies fetchMovies = new FetchMovies();
        fetchMovies.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        myAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.grid_item,
                R.id.movie_poster,
                new ArrayList<String>()
        );

        View rootView = inflater.inflate( R.layout.fragment_main, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.grid_for_posters);
        gridView.setAdapter( myAdapter );

        return rootView;
    }

    public class FetchMovies extends AsyncTask< Void, Void, Void >{

        private final String LOG_TAG = FetchMovies.class.getSimpleName();

        @Override
        protected Void doInBackground(Void... params) {

            if( params.length == 0 ){
                return null;
            }

            HttpsURLConnection httpsURLConnection = null;
            BufferedReader reader = null;

            try{
                final String BASE_URL = "http://api.themoviedb.org/3/movie/popular?";
                final String APIID_PARAM = "api_key";

                Uri builtUri = Uri.parse( BASE_URL).buildUpon()
                        .appendQueryParameter( APIID_PARAM, BuildConfig.TMDB_API_KEY)
                        .build();
                Log.v( LOG_TAG, builtUri.toString());

            } catch ( Exception e ){
                e.printStackTrace();
                return null;
            }

            return null;
        }
    }
}
