package com.handrisunjaya.movies;

import retrofit.Callback;
import retrofit.http.GET;

public interface MoviesApiService {
    @GET("/movie/popular")
    void getPopularMovies(Callback<Movie.MovieResult> cb);

    @GET("/search/movie")
    void pencarianMovies(Callback<Movie.MovieResult> cb);

}

