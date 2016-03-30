package com.yaseen.popularmovies;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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
import android.widget.Toast;

import com.yaseen.popularmovies.Models.MovieItem;
import com.yaseen.popularmovies.Util.Utility;
import com.yaseen.popularmovies.adapter.MoviesAdapter;
import com.yaseen.popularmovies.db.MovieContract;
import com.yaseen.popularmovies.rest.RestApi;
import com.yaseen.popularmovies.rest.RestService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private final String TAG = MainActivityFragment.class.getName();

    @Bind(R.id.movies_grid)
    GridView moviesGridview;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.error_button)
    Button errorButton;
    private final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    private final String POSTER_SIZE = "w185/";
    private final String BACKDROP_SIZE = "w342/";

    private IntentFilter actionIntentFilter = null;
    private List<MovieItem> mMoviesList;
    private MoviesAdapter mAdapter;

    private boolean mTwoPane = false;

    public MainActivityFragment() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity().findViewById(R.id.mutipanwrapper) != null) {
            mTwoPane = true;
            Toast.makeText(getActivity(), "TwoPane", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootview);

        actionIntentFilter = new IntentFilter();
        actionIntentFilter.addAction(RestApi.ACTION_FETCH_MOVIES);
        mMoviesList = new ArrayList<>();

        if (savedInstanceState != null && savedInstanceState.containsKey("movie_list")) {
            mMoviesList = savedInstanceState.getParcelableArrayList("movie_list");
        } else {
            if (isNetworkAvailable()) {
                fetchMoviesDetails();
            }else {
                //if there is no internet load favs from List
                loadFavorites();
            }
        }
        setHasOptionsMenu(true);
        mAdapter = new MoviesAdapter(getActivity(), mMoviesList);
        moviesGridview.setAdapter(mAdapter);
        loadFirstItemInDetailFragment();
        moviesGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieItem selectedMovie = mAdapter.getItem(position);

                if (!mTwoPane) {
                    Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
                    intent.putExtra("movie", selectedMovie);
                    startActivity(intent);
                } else {
                    Fragment fragment = new MovieDetailActivityFragment();
                    Bundle arg = new Bundle();
                    arg.putParcelable("movie", selectedMovie);
                    fragment.setArguments(arg);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_framelayout, fragment).commit();
                }

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
            final String[] sortList = {"Most Popular", "Top Rated", "Favorites"};
            final int selected_index;
            switch (getSortByFromPreference()) {
                case Utility.SORT_BY_POPULARITY:
                    selected_index = 0;
                    break;
                case Utility.SORT_BY_RATINGS:
                    selected_index = 1;
                    break;
                case Utility.SORT_BY_FAVORITES:
                    selected_index = 2;
                    break;
                default:
                    selected_index = 0;
                    break;
            }
//           selected_index = getSortByFromPreference().equals(Utility.SORT_BY_POPULARITY) ? 0 : 1;
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getResources().getString(R.string.sort_alert_title))
                    .setSingleChoiceItems(sortList, selected_index, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int index) {
                            SharedPreferences.Editor editor = getActivity().getSharedPreferences(Utility.MOVIE_PREFERENCE, Context.MODE_PRIVATE).edit();
                            String sortPref;
                            switch (index) {
                                case 0:
                                    sortPref = Utility.SORT_BY_POPULARITY;
                                    break;
                                case 1:
                                    sortPref = Utility.SORT_BY_RATINGS;
                                    break;
                                case 2:
                                    sortPref = Utility.SORT_BY_FAVORITES;
                                    break;
                                default:
                                    sortPref = Utility.SORT_BY_POPULARITY;
                                    break;
                            }
                            editor.putString(Utility.MOVIE_SORT_PREF, sortPref).apply();
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

    private void loadFavorites() {
        Cursor cursor = getContext().getContentResolver().query(MovieContract.Movie.CONTENT_URI, null, null, null, null);
        Log.d(TAG, String.valueOf(cursor.getColumnIndex(MovieContract.Movie.COLUMN_TITLE)));
        mMoviesList = new ArrayList<>();
        while (cursor.moveToNext()) {
            MovieItem resultModel = new MovieItem();

            resultModel.setTitle(cursor.getString(cursor.getColumnIndex(MovieContract.Movie.COLUMN_TITLE)));
            resultModel.setImageUrl(cursor.getString(cursor.getColumnIndex(MovieContract.Movie.COLUMN_POSTER_URL)));
            resultModel.setBackdropImage(cursor.getString(cursor.getColumnIndex(MovieContract.Movie.COLUMN_BACK_DROP_URL)));
            resultModel.setOverview(cursor.getString(cursor.getColumnIndex(MovieContract.Movie.COLUMN_PLOT)));
            resultModel.setRating(cursor.getString(cursor.getColumnIndex(MovieContract.Movie.COLUMN_RATING)));
            resultModel.setReleaseDate(cursor.getString(cursor.getColumnIndex(MovieContract.Movie.COLUMN_RELEASE_DATE)));
            resultModel.setId(cursor.getString(cursor.getColumnIndex(MovieContract.Movie.COLUMN_MOVIE_ID)));

            mMoviesList.add(resultModel);
        }
        cursor.close();
        mAdapter = new MoviesAdapter(getActivity(), mMoviesList);
        mAdapter.notifyDataSetChanged();
        moviesGridview.setAdapter(mAdapter);
    }


    private void fetchMoviesDetails() {
            if (!getSortByFromPreference().equals(Utility.SORT_BY_FAVORITES)) {
                if (isNetworkAvailable()) {
                    progressBar.setVisibility(View.VISIBLE);
                    HashMap<String, String> params = new HashMap<>();
                    params.put(RestApi.PARAM_API_KEY, getString(R.string.moviedb_apikey));
                    String url = Utility.buildURL(new String[]{getSortByFromPreference()});
                    Intent serviceIntent = new Intent(getActivity(), RestService.class);
                    serviceIntent.putExtra(RestApi.EXTRA_ACTION, RestApi.ACTION_FETCH_MOVIES);
                    serviceIntent.putExtra(RestApi.EXTRA_PARAMS, params);
                    serviceIntent.putExtra(RestApi.EXTRA_URL, url);
                    getActivity().startService(serviceIntent);
                }
            } else {
                loadFavorites();
            }
    }


    //returns sort type from preference
    private String getSortByFromPreference() {
        SharedPreferences prefs = getActivity().getSharedPreferences(Utility.MOVIE_PREFERENCE, Context.MODE_PRIVATE);

        return prefs.getString(Utility.MOVIE_SORT_PREF, Utility.SORT_BY_POPULARITY);
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
                movieItem.setId(movie.getString("id"));
                movieItem.setBackdropImage(POSTER_BASE_URL + BACKDROP_SIZE + movie.getString("backdrop_path"));
                mMoviesList.add(movieItem);
            }
            mAdapter.notifyDataSetChanged();
           loadFirstItemInDetailFragment();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void loadFirstItemInDetailFragment(){
        if (mTwoPane){
            if (mAdapter.getCount()>0) {
                moviesGridview.performItemClick(mAdapter.getView(0,null,null),0,mAdapter.getItemId(0));
                //if it is Twopane load the first item into detailFragment by default
            }
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
