package com.example.android.popularmovies.rest;

import com.example.android.popularmovies.model.MovieCallResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiInterface {

    @GET
    Call<MovieCallResult> getMovies(@Url String url);
}
