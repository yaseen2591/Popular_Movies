package com.yaseen.popularmovies.rest;

/**
 * Created by Pasonet on 18-02-2016.
 */
public class RestApi {
    public static final String EXTRA_RESPONSE_CODE = "com.yaseen.popularmovies.rest.EXTRA_RESULT_CODE";
    public static final String EXTRA_PARAMS = "com.yaseen.popularmovies.rest.EXTRA_PARAMS";
    public static final String EXTRA_RESPONSE_DATA = "com.yaseen.popularmovies.rest.EXTRA_RESPONSE";
    public static final String EXTRA_ACTION = "com.yaseen.popularmovies.rest.EXTRA_ACTION";
    public static final int INVALID_ACTION = -1;
    public static final String ACTION_FETCH_MOVIES = "com.yaseen.pupularmovies.rest.FECTH_MOVIES";


    public static final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";


    public static final String PARAM_API_KEY = "api_key";
    public static final String PARAM_SORT_BY = "sort_by";
    public static final String PARAM_PAGE = "page";

}
