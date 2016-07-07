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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.zip.Inflater;

import javax.net.ssl.HttpsURLConnection;

public class MovieFragment extends Fragment {

    private final String LOG_TAG = MovieFragment.class.getSimpleName();

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
        Log.v( LOG_TAG, "in on start");
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

    public class FetchMovies extends AsyncTask< Void, Void, String >{

        private final String LOG_TAG = FetchMovies.class.getSimpleName();

        @Override
        protected String doInBackground(Void... params) {

            Log.v( LOG_TAG, "dude doInBackground");

//            if( params.length == 0 ){
//                return null;
//            }

            String JSONStr = null;

            HttpsURLConnection httpsURLConnection = null;
            BufferedReader reader = null;

            try{
                final String BASE_URL = "https://api.themoviedb.org/3/movie/popular?";
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
                myAdapter.clear();

                JSONObject json = new JSONObject( jsonString );
                JSONArray jsonArray = json.optJSONArray( TMDB_RESULTS );

                String resultStr[] = new String[ jsonArray.length()];


                for( int i = 0; i < jsonArray.length(); i++ ){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String originalTile = jsonObject.getString( TMDB_ORIGINAL_TITLE);
                    String posterPath = jsonObject.getString( TMDB_POSTER_PATH);
                    String releaseDate = jsonObject.getString( TMDB_RELEASE_DATE);
                    String userRating = jsonObject.getString( TMDB_USER_RATING);
                    String overView = jsonObject.getString( TMDB_OVERVIEW);

                    resultStr[i] = originalTile + posterPath + releaseDate + userRating + overView;

                    myAdapter.add( resultStr[i] );
                }
            } catch ( JSONException j ){
                j.printStackTrace();
            }

        }
    }
}
