package com.yaseen.popularmovies;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.yaseen.popularmovies.Models.MovieItem;
import com.yaseen.popularmovies.rest.RestApi;
import com.yaseen.popularmovies.rest.RestService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private final String TAG = MainActivityFragment.class.getName();
    private final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    private final String POSTER_SIZE = "w185/";
    private final String BACKDROP_SIZE = "w342/";

    public final String MOVIE_PREFERENCE = "movie_pref";
    public final String MOVIE_SORT_PREF = "sort_pref";
    public final String SORT_BY_POPULARITY = "popularity.desc";
    public final String SORT_BY_RATINGS = "vote_average.desc";
    //sort_by=vote_average.desc&api_key=aa0359d9b234e81518b7ccab7d87ae31&page=1
    private IntentFilter actionIntentFilter = null;
    private List<MovieItem> mMoviesList;
    private MoviesAdapter mAdapter;
    private GridView moviesGridview;
    Button errorButton;
    private ProgressBar progressBar;

    public MainActivityFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_main, container, false);

        actionIntentFilter = new IntentFilter();
        actionIntentFilter.addAction(RestApi.ACTION_FETCH_MOVIES);
        mMoviesList = new ArrayList<>();
        moviesGridview = (GridView) rootview.findViewById(R.id.movies_grid);
        progressBar = (ProgressBar) rootview.findViewById(R.id.progressBar);
        errorButton = (Button) rootview.findViewById(R.id.error_button);

        if (savedInstanceState != null && savedInstanceState.containsKey("movie_list")) {
            mMoviesList = savedInstanceState.getParcelableArrayList("movie_list");
        } else {
            fetchMoviesDetails();
        }
        setHasOptionsMenu(true);
        mAdapter = new MoviesAdapter(getActivity(), mMoviesList);
        moviesGridview.setAdapter(mAdapter);
        moviesGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieItem selectedMovie = mAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
                intent.putExtra("movie", selectedMovie);
                startActivity(intent);

            }
        });
        errorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorButton.setVisibility(View.GONE);
                fetchMoviesDetails();
            }
        });

        return rootview;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.menu_main_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort_movies) {
            String[] sortList = {"Sort by Popularity", "Sort by Ratings"};
            int selected_index = getSortByFromPreference().equals(SORT_BY_POPULARITY) ? 0 : 1;
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getResources().getString(R.string.sort_alert_title))
                    .setSingleChoiceItems(sortList, selected_index, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int index) {
                            SharedPreferences.Editor editor = getActivity().getSharedPreferences(MOVIE_PREFERENCE, Context.MODE_PRIVATE).edit();
                            String sort_pref = index > 0 ? SORT_BY_RATINGS : SORT_BY_POPULARITY;
                            editor.putString(MOVIE_SORT_PREF, sort_pref).apply();
                            dialog.dismiss();
                            mAdapter.clear();
                            fetchMoviesDetails();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void fetchMoviesDetails() {
        if (!isNetworkAvailable()) {
            moviesGridview.setVisibility(View.GONE);
            errorButton.setVisibility(View.VISIBLE);

        } else {
            progressBar.setVisibility(View.VISIBLE);
            HashMap<String, String> params = new HashMap<String, String>();
            params.put(RestApi.PARAM_API_KEY, getString(R.string.moviedb_apikey));
            params.put(RestApi.PARAM_SORT_BY, getSortByFromPreference());
            Intent serviceIntent = new Intent(getActivity(), RestService.class);
            serviceIntent.putExtra(RestApi.EXTRA_ACTION, RestApi.ACTION_FETCH_MOVIES);
            serviceIntent.putExtra(RestApi.EXTRA_PARAMS, (Serializable) params);
            getActivity().startService(serviceIntent);
        }
    }


    //returns sort type from preference
    private String getSortByFromPreference() {
        SharedPreferences prefs = getActivity().getSharedPreferences(MOVIE_PREFERENCE, Context.MODE_PRIVATE);
        String sortPreference = prefs.getString(MOVIE_SORT_PREF, SORT_BY_POPULARITY);

        return sortPreference;
    }


    /*
    *   converts String to MovieItem Objects using JSON Parsing
    */
    private void parseMovieResponse(String data) {
        try {
            JSONObject response = new JSONObject(data);
            JSONArray moviesList = response.getJSONArray("results");
            for (int i = 0; i < moviesList.length(); i++) {
                JSONObject movie = moviesList.getJSONObject(i);
                MovieItem movieItem = new MovieItem();
                movieItem.setTitle(movie.getString("original_title"));
                movieItem.setImageUrl(POSTER_BASE_URL + POSTER_SIZE + movie.getString("poster_path"));
                movieItem.setOverview(movie.getString("overview"));
                movieItem.setRating(movie.getString("vote_average"));
                movieItem.setReleaseDate(movie.getString("release_date"));
                movieItem.setBackdropImage(POSTER_BASE_URL + BACKDROP_SIZE + movie.getString("backdrop_path"));
                mMoviesList.add(movieItem);
            }
            mAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private BroadcastReceiver mFetchMoviesReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(RestApi.ACTION_FETCH_MOVIES)) {
                moviesGridview.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                int responseCode = intent.getIntExtra(RestApi.EXTRA_RESPONSE_CODE, -1);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    parseMovieResponse(intent.getStringExtra(RestApi.EXTRA_RESPONSE_DATA));
                }
            }
        }
    };


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("movie_list", (ArrayList<? extends Parcelable>) mMoviesList);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mFetchMoviesReceiver, actionIntentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mFetchMoviesReceiver);
    }

}
