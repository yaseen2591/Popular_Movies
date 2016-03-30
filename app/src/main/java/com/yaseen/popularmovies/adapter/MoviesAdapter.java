package com.yaseen.popularmovies.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yaseen.popularmovies.Models.MovieItem;
import com.yaseen.popularmovies.R;

import java.util.List;

/**
 * Created by Pasonet on 19-02-2016.
 */
public class MoviesAdapter extends BaseAdapter {

    private Context mContext;
    private List<MovieItem> mMoviesList;
    private static LayoutInflater layoutInflater = null;

    public MoviesAdapter(Context context, List<MovieItem> moviesList) {
        mContext = context;
        mMoviesList = moviesList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public void clear() {
        mMoviesList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mMoviesList.size();
    }

    @Override
    public MovieItem getItem(int position) {
        return mMoviesList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        @Bind(R.id.image_view) ImageView imageView;
        ImageView imageView;
        View rootView;
        rootView = layoutInflater.inflate(R.layout.grid_movie_item_layout, null);
        if (convertView == null) {
            imageView = (ImageView) rootView.findViewById(R.id.image_view);
        } else {
            imageView = (ImageView) convertView;
        }
        MovieItem movieItem = mMoviesList.get(position);
        Glide.with(mContext)
                .load(movieItem.getImageUrl())
                .error(R.drawable.poster)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageView);
        return imageView;
    }
}
