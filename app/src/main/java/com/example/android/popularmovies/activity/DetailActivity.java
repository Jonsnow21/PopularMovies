package com.example.android.popularmovies.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.fragment.DetailFragment;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.utils.Constants;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private ImageView backdropImage;
    private Movie movie;
    private final String LOG_TAG = DetailActivity.class.getSimpleName();
    private DetailFragment df;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        movie = getIntent().getParcelableExtra(Intent.EXTRA_TEXT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        backdropImage = (ImageView) findViewById(R.id.movie_backdrop_image);

        prepareFragment();
        setSupportActionBar(toolbar);
        initCollapsingToolbar();

        Picasso.with(this)
                .load(Constants.BACKDROP_BASE_URL + movie.getBackdropPath())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder_error)
                .into(backdropImage);

    }

    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(movie.getTitle());
                    collapsingToolbar.setCollapsedTitleTextAppearance(android.R.style.TextAppearance_Material_Widget_Toolbar_Title);
                    collapsingToolbar.setCollapsedTitleTextAppearance(android.R.style.TextAppearance_DeviceDefault_Large);
                    isShow = true;
                    df.changeVisibility(0);
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    df.changeVisibility(1);
                    isShow = false;
                }
            }
        });
    }

    /**
     * To prepare detail fragment
     * */
    private void prepareFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        df = new DetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("Movie", movie);
        df.setArguments(bundle);
        fragmentTransaction.add(R.id.movie_detail_container, df).commit();

    }

}
