package com.yaseen.popularmovies.tabs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yaseen.popularmovies.Models.MovieItem;
import com.yaseen.popularmovies.Models.MovieReview;
import com.yaseen.popularmovies.R;
import com.yaseen.popularmovies.Util.Utility;
import com.yaseen.popularmovies.adapter.MovieReviewsAdapter;
import com.yaseen.popularmovies.rest.RestApi;
import com.yaseen.popularmovies.rest.RestService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by Pasonet on 29-03-2016.
 */
public class MovieReviewsFragment extends Fragment {
    private static final String TAG = MovieReviewsFragment.class.getName();

    @Bind(R.id.recycler_view_movie_reviews)
    RecyclerView mRecyclerview;

    private MovieItem movieItem;
    private IntentFilter actionIntentFilter;

    private ArrayList<MovieReview> movieReviewsList = null;
    private MovieReviewsAdapter mReviewAdapter;

    public MovieReviewsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reviews_info, container, false);
        ButterKnife.bind(this, view);
        actionIntentFilter = new IntentFilter();
        actionIntentFilter.addAction(RestApi.ACTION_FETCH_REVIEWS);
        movieReviewsList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerview.setLayoutManager(linearLayoutManager);
        movieItem = this.getArguments().getParcelable(Utility.EXTRA_MOVIE_FRAGMENT);

        if (savedInstanceState != null && savedInstanceState.containsKey(Utility.EXTRA_REVIEW)) {
            movieReviewsList = savedInstanceState.getParcelableArrayList(Utility.EXTRA_REVIEW);
            mReviewAdapter = new MovieReviewsAdapter(getActivity(), movieReviewsList);
            mReviewAdapter.notifyDataSetChanged();
            mRecyclerview.setAdapter(mReviewAdapter);
        } else {
            fetchreviews();
        }


        return view;
    }

    private void fetchreviews() {
        HashMap<String, String> params = new HashMap<>();
        params.put(RestApi.PARAM_API_KEY, getString(R.string.moviedb_apikey));
        String url = Utility.buildURL(new String[]{movieItem.getId(), Utility.PATH_REVIEWS});
        Intent serviceIntent = new Intent(getActivity(), RestService.class);
        serviceIntent.putExtra(RestApi.EXTRA_ACTION, RestApi.ACTION_FETCH_REVIEWS);
        serviceIntent.putExtra(RestApi.EXTRA_PARAMS, params);
        serviceIntent.putExtra(RestApi.EXTRA_URL, url);
        getActivity().startService(serviceIntent);
    }


    private BroadcastReceiver mFetchMovieDetailsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(RestApi.ACTION_FETCH_REVIEWS)) {
                int responsecode = intent.getIntExtra(RestApi.EXTRA_RESPONSE_CODE, -1);
                if (responsecode == HttpURLConnection.HTTP_OK) {
//                    Log.d(TAG, intent.getStringExtra(RestApi.EXTRA_RESPONSE_DATA));

                    try {
                        JSONObject responseJSON = new JSONObject(intent.getStringExtra(RestApi.EXTRA_RESPONSE_DATA));
                        JSONArray reviews = responseJSON.getJSONArray("results");
                        for (int i = 0; i < reviews.length(); i++) {
                            MovieReview review = new MovieReview();
                            review.setUrl(reviews.getJSONObject(i).getString("url"));
                            review.setAuthor(reviews.getJSONObject(i).getString("author"));
                            review.setContent(reviews.getJSONObject(i).getString("content"));
                            review.setId(reviews.getJSONObject(i).getString("id"));
                            movieReviewsList.add(review);
                        }
                        mReviewAdapter = new MovieReviewsAdapter(getActivity(), movieReviewsList);
                        mReviewAdapter.notifyDataSetChanged();
                        mRecyclerview.setAdapter(mReviewAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    };


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (movieReviewsList.size() > 0) {
            outState.putParcelableArrayList(Utility.EXTRA_REVIEW, movieReviewsList);
        }
    }

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
