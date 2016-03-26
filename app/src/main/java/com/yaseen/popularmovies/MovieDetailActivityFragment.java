package com.yaseen.popularmovies;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yaseen.popularmovies.Models.MovieItem;
import com.yaseen.popularmovies.Util.Utility;
import com.yaseen.popularmovies.rest.RestApi;
import com.yaseen.popularmovies.rest.RestService;

import java.net.HttpURLConnection;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {

    private static final String TAG = MovieDetailActivityFragment.class.getName();
    @Bind(R.id.movie_title) TextView title;
    @Bind(R.id.rating)TextView rating;
    @Bind(R.id.release_date)TextView release_date;
    @Bind(R.id.overview)TextView overview;
    @Bind(R.id.movie_thumb)ImageView movieThumbView;
    @Bind(R.id.backdrop_image) ImageView backdropImageView;

    private IntentFilter actionIntentFilter;

//    private TextView title, overview, rating, release_date;
//    private ImageView movieThumbView;
    private MovieItem movieItem;

    public MovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this,rootview);
//        title = (TextView) rootview.findViewById(R.id.movie_title);
//        rating = (TextView) rootview.findViewById(R.id.rating);
//        release_date = (TextView) rootview.findViewById(R.id.release_date);
//        overview = (TextView) rootview.findViewById(R.id.overview);
//        movieThumbView = (ImageView) rootview.findViewById(R.id.movie_thumb);
        actionIntentFilter=new IntentFilter();
        actionIntentFilter.addAction(RestApi.ACTION_FETCH_REVIEWS);
        actionIntentFilter.addAction(RestApi.ACTION_FETCH_TRAILORS);
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("movie")) {
            movieItem = (MovieItem) intent.getParcelableExtra("movie");
            title.setText(movieItem.getTitle());
            rating.setText("Rating : " + movieItem.getRating());
            release_date.setText("Release : " + movieItem.getReleaseDate());
            overview.setText(movieItem.getOverview());
            Picasso.with(getContext()).load(movieItem.getImageUrl()).into(movieThumbView);
            Picasso.with(getContext()).load(movieItem.getBackdropImage()).into(backdropImageView);
            getActivity().setTitle(movieItem.getTitle());

            fetchtrailors();
            fetchreviews();


//            Log.d(TAG, "Movie ID " + movieItem.getId());
//            Log.d(TAG,movieItem.getImageUrl());
//            Log.d(TAG,movieItem.getBackdropImage());
//            Log.d(TAG, Utility.buildURL(new String[]{movieItem.getId(),Utility.PATH_REVIEWS}));

        }

        return rootview;
    }



    private void fetchreviews(){
        HashMap<String, String> params = new HashMap<>();
        params.put(RestApi.PARAM_API_KEY, getString(R.string.moviedb_apikey));
        String url= Utility.buildURL(new String[]{movieItem.getId(),Utility.PATH_REVIEWS});
        Intent serviceIntent = new Intent(getActivity(), RestService.class);
        serviceIntent.putExtra(RestApi.EXTRA_ACTION, RestApi.ACTION_FETCH_REVIEWS);
        serviceIntent.putExtra(RestApi.EXTRA_PARAMS, params);
        serviceIntent.putExtra(RestApi.EXTRA_URL, url);
        getActivity().startService(serviceIntent);
    }

    private void fetchtrailors(){
        HashMap<String, String> params = new HashMap<>();
        params.put(RestApi.PARAM_API_KEY, getString(R.string.moviedb_apikey));
        String url= Utility.buildURL(new String[]{movieItem.getId(),Utility.PATH_VIDEOS});
        Intent serviceIntent = new Intent(getActivity(), RestService.class);
        serviceIntent.putExtra(RestApi.EXTRA_ACTION, RestApi.ACTION_FETCH_TRAILORS);
        serviceIntent.putExtra(RestApi.EXTRA_PARAMS, params);
        serviceIntent.putExtra(RestApi.EXTRA_URL, url);
        getActivity().startService(serviceIntent);
    }

    private BroadcastReceiver mFetchMovieDetailsReceiver= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(RestApi.ACTION_FETCH_REVIEWS)){
                int responsecode=intent.getIntExtra(RestApi.EXTRA_RESPONSE_CODE,-1);
                if (responsecode== HttpURLConnection.HTTP_OK){
                    Log.d(TAG,intent.getStringExtra(RestApi.EXTRA_RESPONSE_DATA));
                }
            }
            else if (intent.getAction().equals(RestApi.ACTION_FETCH_TRAILORS)){
                int responsecode=intent.getIntExtra(RestApi.EXTRA_RESPONSE_CODE,-1);
                if (responsecode== HttpURLConnection.HTTP_OK){
                    Log.d(TAG,intent.getStringExtra(RestApi.EXTRA_RESPONSE_DATA));
                }
            }
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mFetchMovieDetailsReceiver, actionIntentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mFetchMovieDetailsReceiver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
