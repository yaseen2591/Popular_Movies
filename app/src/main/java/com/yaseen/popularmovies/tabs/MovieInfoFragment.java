package com.yaseen.popularmovies.tabs;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yaseen.popularmovies.Models.MovieItem;
import com.yaseen.popularmovies.R;
import com.yaseen.popularmovies.Util.Utility;
import com.yaseen.popularmovies.db.MovieContract;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by Pasonet on 29-03-2016.
 */
public class MovieInfoFragment extends Fragment {
    @Bind(R.id.movie_title)
    TextView title;
    @Bind(R.id.rating)
    TextView rating;
    @Bind(R.id.release_date)
    TextView release_date;
    @Bind(R.id.overview)
    TextView overview;
    @Bind(R.id.movie_thumb)
    ImageView movieThumbView;
    @Bind(R.id.favorite_button)
    ImageButton favoriteButton;
    private MovieItem movieItem;
    private boolean isFavoriteMovie=false;

    public MovieInfoFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.movie_info, container, false);
        ButterKnife.bind(this, view);


        movieItem = this.getArguments().getParcelable(Utility.EXTRA_MOVIE_FRAGMENT);

        title.setText(movieItem.getTitle());
        rating.setText("Rating : " + movieItem.getRating());
        release_date.setText(movieItem.getReleaseDate());
        overview.setText(movieItem.getOverview());


//        Picasso.with(getContext()).load(movieItem.getImageUrl())
//                .error(R.drawable.poster)
//                .centerCrop()
//                .centerCrop()
//                .into(movieThumbView);


        Glide.with(getContext())
                .load(movieItem.getImageUrl())
                .error(R.drawable.poster)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(movieThumbView);


        if (movieItem.getImageUrl()==null)
            movieItem.setImageUrl("Url not found");
        if (movieItem.getBackdropImage()==null)
            movieItem.setBackdropImage("url not found");

        Cursor cursor = getContext().getContentResolver().query(MovieContract.Movie.CONTENT_URI,
                new String[]{MovieContract.Movie.COLUMN_MOVIE_ID},
                MovieContract.Movie.COLUMN_MOVIE_ID + "= ? ",
                new String[]{movieItem.getId()},
                null);

        if (cursor!=null && cursor.getCount() > 0) {
            favoriteButton.setImageResource(R.drawable.favorite);
            isFavoriteMovie=true;
        }
        cursor.close();;



        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFavoriteMovie)
                {
                    int rowDeleted = getContext().getContentResolver().delete(MovieContract.Movie.CONTENT_URI, MovieContract.Movie.COLUMN_MOVIE_ID + "= ?", new String[]{movieItem.getId()});
                    if (rowDeleted > 0)
                        favoriteButton.setImageResource(R.drawable.unfavorite);
                        Toast.makeText(getContext(), "Removed  " + movieItem.getTitle() + " from favourites", Toast.LENGTH_SHORT).show();

                }
                else {
                    ContentValues values=new ContentValues();
                    values.put(MovieContract.Movie.COLUMN_TITLE, movieItem.getTitle());
                    values.put(MovieContract.Movie.COLUMN_POSTER_URL, movieItem.getImageUrl());
                    values.put(MovieContract.Movie.COLUMN_BACK_DROP_URL, movieItem.getBackdropImage());
                    values.put(MovieContract.Movie.COLUMN_PLOT, movieItem.getOverview());
                    values.put(MovieContract.Movie.COLUMN_RATING, movieItem.getRating());
                    values.put(MovieContract.Movie.COLUMN_RELEASE_DATE, movieItem.getReleaseDate());
                    values.put(MovieContract.Movie.COLUMN_MOVIE_ID, movieItem.getId());
                    Uri rowUri;
                    rowUri = getContext().getContentResolver().insert(MovieContract.Movie.CONTENT_URI, values);
                    long rowId = ContentUris.parseId(rowUri);
                    if (rowId > 0)
                        Toast.makeText(getContext(), "Favourited  " + movieItem.getTitle(), Toast.LENGTH_SHORT).show();
                    favoriteButton.setImageResource(R.drawable.favorite);

                }
                isFavoriteMovie=!isFavoriteMovie;
            }
        });

        return view;

    }

}
