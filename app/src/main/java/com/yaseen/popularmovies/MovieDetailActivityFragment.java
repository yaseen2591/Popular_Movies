package com.yaseen.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yaseen.popularmovies.Models.MovieItem;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {

    private static final String TAG = MovieDetailActivityFragment.class.getName();
    private TextView title, overview, rating, release_date;
    private ImageView movieThumbView;
    private MovieItem movieItem;

    public MovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        title = (TextView) rootview.findViewById(R.id.movie_title);
        rating = (TextView) rootview.findViewById(R.id.rating);
        release_date = (TextView) rootview.findViewById(R.id.release_date);
        overview = (TextView) rootview.findViewById(R.id.overview);
        movieThumbView = (ImageView) rootview.findViewById(R.id.movie_thumb);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("movie")) {
            movieItem = (MovieItem) intent.getParcelableExtra("movie");
            title.setText(movieItem.getTitle());
            rating.setText("Rating : " + movieItem.getRating());
            release_date.setText("Release : " + movieItem.getReleaseDate());
            overview.setText(movieItem.getOverview());
            Picasso.with(getContext()).load(movieItem.getImageUrl()).into(movieThumbView);
            getActivity().setTitle(movieItem.getTitle());
        }

        return rootview;
    }


}
