package com.yaseen.popularmovies.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yaseen.popularmovies.Models.MovieVideo;
import com.yaseen.popularmovies.R;
import com.yaseen.popularmovies.Util.Utility;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Pasonet on 29-03-2016.
 */
public class MovieTrailorsAdapter extends RecyclerView.Adapter<MovieTrailorsAdapter.TrailorViewHolder> {

    private static final String TAG=MovieTrailorsAdapter.class.getName();
    private Context mContext;
    private ArrayList<MovieVideo> mTrailorsList;


    public MovieTrailorsAdapter(Context context,ArrayList<MovieVideo> trailorsList){
        this.mContext=context;
        this.mTrailorsList=trailorsList;

    }

    @Override
    public TrailorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_trailor_item, parent, false);

        return new TrailorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailorViewHolder holder, int position) {
        String url = Utility.YOUTUBE_THUMBNAIL_URL_BASE + mTrailorsList.get(position).getKey() + "/default.jpg";
        String title = mTrailorsList.get(position).getName();
        holder.textViewTrailerTitle.setText(title);
        Glide.with(holder.imageViewThumbnails.getContext())
                .load(url)
                .error(R.drawable.poster)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.imageViewThumbnails);
    }

    @Override
    public int getItemCount() {
        if (mTrailorsList!=null){
           return mTrailorsList.size();
        }
        return 0;
    }

    public class TrailorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @Bind(R.id.imageview_trailer_thumbnail)
        ImageView imageViewThumbnails;
        @Bind(R.id.textview_trailername)
        TextView textViewTrailerTitle;


        public TrailorViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ButterKnife.bind(this, itemView);

        }

        @Override
        public void onClick(View v) {
            String vId = mTrailorsList.get(getAdapterPosition()).getKey();
            String url = Utility.YOUTUBE_PLAYER_URL_BASE + vId;
            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            Log.d(TAG, "Playing video with URL " + url);

        }
    }
}
