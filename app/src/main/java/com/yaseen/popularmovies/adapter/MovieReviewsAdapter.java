package com.yaseen.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yaseen.popularmovies.Models.MovieReview;
import com.yaseen.popularmovies.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;



/**
 * Created by Pasonet on 29-03-2016.
 */
public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.ReviewViewHolder> {
    private static final String TAG=MovieReviewsAdapter.class.getName();

    private Context mContext;
    private ArrayList<MovieReview> mReviewsList;

    public MovieReviewsAdapter(Context context,ArrayList<MovieReview> reviewsList){
        this.mContext=context;
        this.mReviewsList=reviewsList;

    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_reviews_item, parent, false);

        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.tvAuthor.setText(mReviewsList.get(position).getAuthor());
        holder.tvContent.setText(mReviewsList.get(position).getContent());
        holder.tvLink.setText(mReviewsList.get(position).getUrl());

        Linkify.addLinks(holder.tvLink, Linkify.WEB_URLS);

    }

    @Override
    public int getItemCount() {
        return mReviewsList.size();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.textview_review_author)
        TextView tvAuthor;
        @Bind(R.id.textview_review_content)
        TextView tvContent;
        @Bind(R.id.textview_review_link)
        TextView tvLink;

        public ReviewViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

    }
}
