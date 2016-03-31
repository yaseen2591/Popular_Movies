package com.yaseen.popularmovies.tabs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.yaseen.popularmovies.Models.MovieItem;
import com.yaseen.popularmovies.Models.MovieVideo;
import com.yaseen.popularmovies.R;
import com.yaseen.popularmovies.Util.Utility;
import com.yaseen.popularmovies.adapter.MovieTrailorsAdapter;
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
public class MovieTrailerFragment extends Fragment {

    private static final String TAG = MovieTrailerFragment.class.getName();

    @Bind(R.id.recycler_view_movie_trailors)
    RecyclerView mRecyclerview;


    private IntentFilter actionIntentFilter;
    private MovieItem movieItem;
    private ArrayList<MovieVideo> mTrailorsList=null;
    private MovieTrailorsAdapter mAdapter;


    public MovieTrailerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.trailor_info, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        getActivity().supportInvalidateOptionsMenu();

        actionIntentFilter = new IntentFilter();
        actionIntentFilter.addAction(RestApi.ACTION_FETCH_TRAILORS);
        mTrailorsList=new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerview.setLayoutManager(linearLayoutManager);
        movieItem = this.getArguments().getParcelable(Utility.EXTRA_MOVIE_FRAGMENT);

        if (savedInstanceState != null && savedInstanceState.containsKey(Utility.EXTRA_VIDEO)) {
            mTrailorsList = savedInstanceState.getParcelableArrayList(Utility.EXTRA_REVIEW);
            mAdapter = new MovieTrailorsAdapter(getActivity(),mTrailorsList);
            mAdapter.notifyDataSetChanged();
            mRecyclerview.setAdapter(mAdapter);
        }
            fetchtrailors();

        return view;
    }

    private void fetchtrailors() {
        HashMap<String, String> params = new HashMap<>();
        params.put(RestApi.PARAM_API_KEY, getString(R.string.moviedb_apikey));
        String url = Utility.buildURL(new String[]{movieItem.getId(), Utility.PATH_VIDEOS});
        Intent serviceIntent = new Intent(getActivity(), RestService.class);
        serviceIntent.putExtra(RestApi.EXTRA_ACTION, RestApi.ACTION_FETCH_TRAILORS);
        serviceIntent.putExtra(RestApi.EXTRA_PARAMS, params);
        serviceIntent.putExtra(RestApi.EXTRA_URL, url);
        getActivity().startService(serviceIntent);
    }

    private BroadcastReceiver mFetchMovieDetailsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(RestApi.ACTION_FETCH_TRAILORS)) {
                int responsecode = intent.getIntExtra(RestApi.EXTRA_RESPONSE_CODE, -1);
                if (responsecode == HttpURLConnection.HTTP_OK) {
                    try {
                        mTrailorsList= new ArrayList<>();
                        JSONObject responseJSON = new JSONObject(intent.getStringExtra(RestApi.EXTRA_RESPONSE_DATA));
                        JSONArray trailors = responseJSON.getJSONArray("results");
                        for (int i = 0; i < trailors.length(); i++) {
                            MovieVideo movieVideo = new MovieVideo();
                            movieVideo.setKey(trailors.getJSONObject(i).getString("key"));
                            movieVideo.setName(trailors.getJSONObject(i).getString("name"));
                            movieVideo.setSite(trailors.getJSONObject(i).getString("site"));
                            mTrailorsList.add(movieVideo);
                        }
                        mAdapter = new MovieTrailorsAdapter(getActivity(), mTrailorsList);
                        mAdapter.notifyDataSetChanged();
                        mRecyclerview.setAdapter(mAdapter);

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
            outState.putParcelableArrayList(Utility.EXTRA_VIDEO, mTrailorsList);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_share_trailor, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share_trailor);
        String url = "No video found";

        ShareActionProvider shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        if (mTrailorsList!=null && mTrailorsList.size()>0){
            url = Utility.YOUTUBE_PLAYER_URL_BASE + mTrailorsList.get(0).getKey();
        }
        intent.putExtra(Intent.EXTRA_TEXT, url);
        shareActionProvider.setShareIntent(intent);
        MenuItemCompat.setActionProvider(menuItem, shareActionProvider);

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
