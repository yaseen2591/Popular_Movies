package com.yaseen.popularmovies.Util;

/**
 * Created by Pasonet on 12-03-2016.
 */
public class Utility {
    /* Constants*/
    public static final String BASE_URL = "http://api.themoviedb.org/3/movie";
    public static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    public static final String POSTER_SIZE = "w185/";
    public static final String BACKDROP_SIZE = "w342/";
    public static final String PATH_VIDEOS="videos";
    public static final String PATH_REVIEWS="reviews";

    public static final String PARAM_APIKEY="api_key";
    /* Constants*/

    /* Preference Keys*/
    public static final String MOVIE_PREFERENCE = "movie_pref";
    public static final String MOVIE_SORT_PREF = "sort_pref";
    public static final String SORT_BY_POPULARITY = "popular";
    public static final String SORT_BY_RATINGS = "top_rated";
    public static final String SORT_BY_FAVORITES = "favorites";

    /*Preference Keys*/

     public static String buildURL(String[] appenPaths){
         StringBuilder builder=new StringBuilder();
         builder.append(BASE_URL);

         for (String appendPath: appenPaths){
             builder.append("/"+appendPath);
         }

         return builder.toString();
     }

}
