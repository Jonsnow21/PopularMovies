package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import javax.net.ssl.HttpsURLConnection;

public class MovieFragment extends Fragment {

    private final String LOG_TAG = MovieFragment.class.getSimpleName();
    private String sortOrder;

    public MovieFragment(){
    }

    private MovieAdapter myAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate( savedInstanceState);

        setHasOptionsMenu( true );
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if( id == R.id.sort_by_most_popular ){
            sortOrder = "/popular?";
            updateMovies( sortOrder);
            return true;
        } else if( id == R.id.sort_by_top_rated ){
            sortOrder = "/top_rated?";
            updateMovies(sortOrder);
            return true;
        }
        return super.onOptionsItemSelected( item );
    }

    @Override
    public void onStart(){
        super.onStart();
        sortOrder = "/popular?";
        updateMovies( sortOrder );
    }

    public void updateMovies( String order){
        if( isOnline() ) {
            FetchMovies fetchMovies = new FetchMovies();
            fetchMovies.execute( order );
            Log.v(LOG_TAG, "in on start");
        } else {
            Toast.makeText(getContext(),"Please turn on an active internet connection",Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        myAdapter = new MovieAdapter(
                getActivity(),
                new ArrayList<Movie>()
        );

        View rootView = inflater.inflate( R.layout.fragment_main, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.grid_for_posters);
        gridView.setAdapter( myAdapter );

        gridView.setOnItemClickListener( new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = myAdapter.getItem( position);
                //Toast.makeText( getContext(), "Movie " + movie.getTitle() + " Selected", Toast.LENGTH_SHORT ).show();

                Intent intent = new Intent( getActivity(), DetailActivity.class).putExtra(Intent.EXTRA_TEXT, movie);
                startActivity(intent);
            }
        });

        return rootView;
    }

    public class FetchMovies extends AsyncTask< String, Void, String >{

        private final String LOG_TAG = FetchMovies.class.getSimpleName();

        @Override
        protected String doInBackground(String... params) {

            Log.v( LOG_TAG, "dude doInBackground");

            String JSONStr = null;

            HttpsURLConnection httpsURLConnection = null;
            BufferedReader reader = null;

            try{
                final String BASE_URL = "https://api.themoviedb.org/3/movie" + params[0];
                final String APIID_PARAM = "api_key";

                Uri builtUri = Uri.parse( BASE_URL).buildUpon()
                        .appendQueryParameter( APIID_PARAM, BuildConfig.TMDB_API_KEY)
                        .build();
                Log.v( LOG_TAG, builtUri.toString());

                URL url = new URL( builtUri.toString());

                httpsURLConnection = (HttpsURLConnection) url.openConnection();
                httpsURLConnection.setRequestMethod( "GET" );
                httpsURLConnection.connect();

                StringBuffer stringBuffer = new StringBuffer();

                InputStream stream = httpsURLConnection.getInputStream();

                if( stream == null ){
                    return null;
                }

                reader = new BufferedReader ( new InputStreamReader( stream));

                String line;
                while( (line = reader.readLine() ) != null ){
                    stringBuffer.append( line + "\n");
                }

                if( stringBuffer.length() == 0){
                    return null;
                }

                JSONStr = stringBuffer.toString();
                Log.v(LOG_TAG, JSONStr);

            } catch ( IOException e ){
                e.printStackTrace();
                return null;
            } finally {
                if( httpsURLConnection != null ) {
                    httpsURLConnection.disconnect();
                }

                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            return JSONStr;
        }

        @Override
        protected void onPostExecute( String jsonString ) {

            final String TMDB_RESULTS = "results";
            final String TMDB_ORIGINAL_TITLE = "original_title";
            final String TMDB_POSTER_PATH = "poster_path";
            final String TMDB_RELEASE_DATE = "release_date";
            final String TMDB_USER_RATING = "vote_average";
            final String TMDB_OVERVIEW = "overview";

            try {
                    JSONObject json = new JSONObject( jsonString );
                JSONArray jsonArray = json.optJSONArray( TMDB_RESULTS );

                myAdapter.clear();

                Movie moviesArray[] = new Movie[jsonArray.length()];


                for( int i = 0; i < jsonArray.length(); i++ ){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String originalTitle = jsonObject.getString( TMDB_ORIGINAL_TITLE);
                    String posterPath = jsonObject.getString( TMDB_POSTER_PATH);
                    String releaseDate = jsonObject.getString( TMDB_RELEASE_DATE);
                    float userRating = Float.parseFloat(jsonObject.getString( TMDB_USER_RATING));
                    String overView = jsonObject.getString( TMDB_OVERVIEW);

                    moviesArray[i] = new Movie( originalTitle, posterPath, releaseDate, userRating, overView );

                    myAdapter.add(moviesArray[i]);
                }
            } catch ( JSONException j ){
                j.printStackTrace();
            }

        }
    }
}
