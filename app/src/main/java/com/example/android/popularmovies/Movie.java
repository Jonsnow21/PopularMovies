package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {

    private String title;
    private String posterPath;
    private String releaseDate;
    private float userRating;
    private String overView;
    private String backdropPath;

    Movie( String title,
           String posterPath,
           String releaseDate,
           float userRating,
           String overView,
           String backdropPath){

        this.title = title;
        this.posterPath = posterPath ;
        this.releaseDate = releaseDate;
        this.userRating = userRating;
        this.overView = overView;
        this.backdropPath = backdropPath;
    }

    public String  getPosterPath(){
        final String BASE_URL = "http://image.tmdb.org/t/p/w342/";
        return BASE_URL + posterPath;
    }

    public String getBackdropPath() {
        final String BASE_URL = "http://image.tmdb.org/t/p/w500/";
        return BASE_URL + backdropPath;
    }

    public String getTitle(){
        return title;
    }

    public String getOverView() {
        return overView;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public float getUserRating() {
        return userRating;
    }

    protected Movie(Parcel in) {
        title = in.readString();
        posterPath = in.readString();
        releaseDate = in.readString();
        userRating = in.readFloat();
        overView = in.readString();
        backdropPath = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(posterPath);
        dest.writeString(releaseDate);
        dest.writeFloat(userRating);
        dest.writeString(overView);
        dest.writeString(backdropPath);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}