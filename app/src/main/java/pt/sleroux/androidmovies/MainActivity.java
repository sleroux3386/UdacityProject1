package pt.sleroux.androidmovies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import pt.sleroux.androidmovies.MoviesAdapter.MoviesAdapterOnClickHandler;
import pt.sleroux.androidmovies.data.Movie;
import pt.sleroux.androidmovies.utilities.NoNetworkException;
import pt.sleroux.androidmovies.utilities.TMDBUtilities;

import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MoviesAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    private EndlessRecyclerViewScrollListener scrollListener;

    private RecyclerView mRecyclerView;
    private MoviesAdapter mMoviesAdapter;
    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;
    private GridLayoutManager layoutManager;

    public enum Sort {
        POPULARITY,TOP_RATED
    }
    private Sort currentSort;

    private Menu mOptionsMenu;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getApplicationContext();
        currentSort = Sort.POPULARITY;

        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);

        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        layoutManager = new GridLayoutManager(this,2);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

        mMoviesAdapter = new MoviesAdapter(this,this);

        mRecyclerView.setAdapter(mMoviesAdapter);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadMoviesData(page);
            }
        };
        // Adds the scroll listener to RecyclerView
        mRecyclerView.addOnScrollListener(scrollListener);

        loadMoviesData(1);
    }

    private void loadMoviesData(int page) {
        showMoviesDataView();
        new FetchMoviesTask().execute(String.valueOf(page),currentSort.toString());
    }

    @Override
    public void onClick(String movieID) {
        Context context = this;
        Class destinationClass = MovieDetailsActivity.class;
        Intent detailsActivityIntent = new Intent(context, destinationClass);
        detailsActivityIntent.putExtra(MovieDetailsActivity.MOVIE_ID_INTENT_PARAMETER, movieID);
        startActivity(detailsActivityIntent);
    }

    private void showMoviesDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, List<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Movie> doInBackground(String... params) {
            String page = params[0];
            Sort sort = Sort.valueOf(params[1]);
            URL popularURL = TMDBUtilities.buildListMoviesURL(page,sort,context);

            try {
                String jsonMoviesResponse = TMDBUtilities
                        .getResponseFromHttpUrl(popularURL,context);
                List<Movie> movies = TMDBUtilities.parseJSONMoviesResponse(MainActivity.this,jsonMoviesResponse,context);

                return movies;

            } catch(NoNetworkException nnee){
                return null;
            } catch(Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Movie> moviesData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (moviesData != null) {
                showMoviesDataView();
                mMoviesAdapter.setMoviesData(moviesData);
            } else {
                showErrorMessage();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mOptionsMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.mi_sort_popularity) {
            mOptionsMenu.findItem(R.id.mi_sort_popularity).setVisible(false);
            mOptionsMenu.findItem(R.id.mi_sort_rated).setVisible(true);
            currentSort = Sort.POPULARITY;
            mMoviesAdapter.setMoviesData(null);
            scrollListener.resetState();
            loadMoviesData(1);
            return true;
        } else if (id == R.id.mi_sort_rated) {
            mOptionsMenu.findItem(R.id.mi_sort_popularity).setVisible(true);
            mOptionsMenu.findItem(R.id.mi_sort_rated).setVisible(false);
            currentSort = Sort.TOP_RATED;
            mMoviesAdapter.setMoviesData(null);
            scrollListener.resetState();
            loadMoviesData(1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
