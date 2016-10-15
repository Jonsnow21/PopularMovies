package com.example.android.popularmovies.rest;

import com.example.android.popularmovies.model.MoviesCallResult;
import com.example.android.popularmovies.model.ReviewsCallResult;
import com.example.android.popularmovies.model.TrailersCallResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface ApiInterface {

    @GET
    Call<MoviesCallResult> getMovies(@Url String urlGetMovies);

    @GET
    Call<TrailersCallResult> getVideos(@Url String urlGetVideos);

    @GET
    Call<ReviewsCallResult> getReviews(@Url String urlGetReviews);
}
