package com.yaseen.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MovieDetailActivity extends AppCompatActivity {
    private static final String TAG = MovieDetailActivity.class.getName();
    @Bind(R.id.toolbar)Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null) {
            MovieDetailActivityFragment fragment = new MovieDetailActivityFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, fragment).commit();
        }

    }


}
