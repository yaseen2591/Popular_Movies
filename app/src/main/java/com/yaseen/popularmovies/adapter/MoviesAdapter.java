package com.yaseen.popularmovies.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
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
        ViewHolderItem viewHolderItem;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.grid_movie_item_layout, parent, false);
            viewHolderItem = new ViewHolderItem();
            viewHolderItem.posterImageView = (ImageView) convertView.findViewById(R.id.image_view);
            convertView.setTag(viewHolderItem);

        } else {
            viewHolderItem = (ViewHolderItem) convertView.getTag();
        }
        MovieItem movieItem = mMoviesList.get(position);

        Picasso.with(mContext).load(movieItem.getImageUrl())
                .error(R.drawable.poster)
                .into(viewHolderItem.posterImageView);

        //TODO replace Picasso with Glide -find why it is not working with Glide

//        Glide.with(mContext)
//                .load(movieItem.getImageUrl())
//                .error(R.drawable.poster)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(viewHolderItem.posterImageView);

        return convertView;
    }

    public static class ViewHolderItem {
        ImageView posterImageView;
    }

}
