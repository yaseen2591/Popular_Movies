package com.yaseen.popularmovies;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yaseen.popularmovies.Models.MovieItem;
import com.yaseen.popularmovies.Util.Utility;
import com.yaseen.popularmovies.adapter.MovieDetailsPagerAdapter;
import com.yaseen.popularmovies.rest.RestApi;

import java.net.HttpURLConnection;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {

    private static final String TAG = MovieDetailActivityFragment.class.getName();

    @Bind(R.id.toolbar_image_backdrop)
    ImageView backdropImageView;
    @Bind(R.id.viewpager_movie_detail)
    ViewPager viewPager;
    @Bind(R.id.tabs)
    TabLayout tabLayout;


    private IntentFilter actionIntentFilter;

    private boolean twoPane = false;

    private MovieItem movieItem;

    public MovieDetailActivityFragment() {
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        Toolbar toolbar2 = (Toolbar) getActivity().findViewById(R.id.toolbar_movie_detail);
        if (getActivity().findViewById(R.id.mutipanwrapper) != null) {
            twoPane = true;
            toolbar2.setTitle(movieItem.getTitle());
        }
        if (!twoPane)
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(movieItem.getTitle());

        try {
            setViewpager();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    private void setViewpager() {


        viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager_movie_detail);
        tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs);

        MovieDetailsPagerAdapter pagerAdapter = new MovieDetailsPagerAdapter(getActivity().getSupportFragmentManager(), movieItem);
        pagerAdapter.notifyDataSetChanged();

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, rootview);
        Intent intent = getActivity().getIntent();

        movieItem = intent.getParcelableExtra("movie");
        if (movieItem != null) {
            movieItem = intent.getParcelableExtra("movie");
        } else {
            movieItem = this.getArguments().getParcelable("movie");
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(Utility.EXTRA_MOVIE_FRAGMENT)) {
            movieItem = savedInstanceState.getParcelable(Utility.EXTRA_MOVIE_FRAGMENT);
        }

        Glide.with(getContext())
                .load(movieItem.getBackdropImage())
                .error(R.drawable.backdrop)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(backdropImageView);

        getActivity().setTitle(movieItem.getTitle());

        return rootview;
    }


    private BroadcastReceiver mFetchMovieDetailsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(RestApi.ACTION_FETCH_REVIEWS)) {
                int responsecode = intent.getIntExtra(RestApi.EXTRA_RESPONSE_CODE, -1);
                if (responsecode == HttpURLConnection.HTTP_OK) {
                    Log.d(TAG, intent.getStringExtra(RestApi.EXTRA_RESPONSE_DATA));
                }
            } else if (intent.getAction().equals(RestApi.ACTION_FETCH_TRAILORS)) {
                int responsecode = intent.getIntExtra(RestApi.EXTRA_RESPONSE_CODE, -1);
                if (responsecode == HttpURLConnection.HTTP_OK) {
                    Log.d(TAG, intent.getStringExtra(RestApi.EXTRA_RESPONSE_DATA));
                }
            }
        }
    };


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (movieItem != null) {
            outState.putParcelable(Utility.EXTRA_MOVIE_FRAGMENT, movieItem);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
