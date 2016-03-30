package com.yaseen.popularmovies.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.yaseen.popularmovies.Models.MovieItem;
import com.yaseen.popularmovies.Util.Utility;
import com.yaseen.popularmovies.tabs.MovieInfoFragment;
import com.yaseen.popularmovies.tabs.MovieReviewsFragment;
import com.yaseen.popularmovies.tabs.MovieTrailerFragment;


/**
 * Created by Pasonet on 29-03-2016.
 */
public class MovieDetailsPagerAdapter extends FragmentStatePagerAdapter {

    private String[] TABS_MOVIE_DETAILS={"OVERVIEW","TRAILER","REVIEWS"};
    private MovieItem mMovieItem;

    public MovieDetailsPagerAdapter(FragmentManager fm, MovieItem movie) {
        super(fm);
        this.mMovieItem = movie;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                MovieInfoFragment movieInfoFragment = new MovieInfoFragment();
                Bundle arg = new Bundle();
                arg.putParcelable(Utility.EXTRA_MOVIE_FRAGMENT, mMovieItem);
                movieInfoFragment.setArguments(arg);
                return movieInfoFragment;

            case 1:
                MovieTrailerFragment trailerFragment = new MovieTrailerFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable(Utility.EXTRA_MOVIE_FRAGMENT, mMovieItem);
                trailerFragment.setArguments(bundle);
                return trailerFragment;

            case 2:
                MovieReviewsFragment reviewsFragment = new MovieReviewsFragment();
                Bundle args = new Bundle();
                args.putParcelable(Utility.EXTRA_MOVIE_FRAGMENT, mMovieItem);
                reviewsFragment.setArguments(args);
                return reviewsFragment;

            default:
                return new Fragment();

        }
    }


    @Override
    public int getCount() {
        return TABS_MOVIE_DETAILS.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TABS_MOVIE_DETAILS[position];
    }
}
