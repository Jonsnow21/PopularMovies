package com.example.android.popularmovies.orm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.android.popularmovies.database.MovieContract.MovieEntry;
import com.example.android.popularmovies.database.MovieDbHelper;
import com.example.android.popularmovies.model.Movie;

import java.util.ArrayList;

public class MovieORM {

    public static ContentValues getMovieValue (Movie movie) {
        ContentValues cv = new ContentValues();
        cv.put(MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
        cv.put(MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
        cv.put(MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        cv.put(MovieEntry.COLUMN_MOVIE_ID, movie.getId());
        cv.put(MovieEntry.COLUMN_TITLE, movie.getTitle());
        cv.put(MovieEntry.COLUMN_BACKDROP_PATH, movie.getBackdropPath());
        cv.put(MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
        return cv;
    }

    public static ArrayList<Movie> getAllMovies (Context context) {
        ArrayList<Movie> movies = new ArrayList<>();
        MovieDbHelper dbHelper = new MovieDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        final String query = "SELECT * FROM " + MovieEntry.TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Movie movie = new Movie();
                movie.setPosterPath(cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_POSTER_PATH)));
                movie.setOverview(cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_OVERVIEW)));
                movie.setReleaseDate(cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE)));
                movie.setId(cursor.getInt(cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_ID)));
                movie.setTitle(cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_TITLE)));
                movie.setBackdropPath(cursor.getString(cursor.getColumnIndex(MovieEntry.COLUMN_BACKDROP_PATH)));
                movie.setVoteAverage(cursor.getFloat(cursor.getColumnIndex(MovieEntry.COLUMN_VOTE_AVERAGE)));
                movies.add(movie);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return movies;
    }
}
