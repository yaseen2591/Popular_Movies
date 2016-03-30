package com.yaseen.popularmovies.rest;

/**
 * Created by Pasonet on 18-02-2016.
 */
public class RestApi {
    public static final String EXTRA_RESPONSE_CODE = "com.rest.EXTRA_RESULT_CODE";
    public static final String EXTRA_PARAMS = "com.rest.EXTRA_PARAMS";
    public static final String EXTRA_URL="com.rest.EXTRA_URL";
    public static final String EXTRA_RESPONSE_DATA = "com.rest.EXTRA_RESPONSE";
    public static final String EXTRA_ACTION = "com.rest.EXTRA_ACTION";
    public static final int INVALID_ACTION = -1;

    public static final String ACTION_FETCH_MOVIES = "com.rest.FECTH_MOVIES";
    public static final String ACTION_FETCH_TRAILORS="com.yaseen.popularmovies.res.FETCH_TRAILORS";
    public static final String ACTION_FETCH_REVIEWS="com.rest.FETCH_REVIEWS";

    public static final String BASE_URL = "https://api.themoviedb.org/3/discover/movie?";

    public static final String PARAM_API_KEY = "api_key";
    public static final String PARAM_SORT_BY = "sort_by";
    public static final String PARAM_PAGE = "page";

}
