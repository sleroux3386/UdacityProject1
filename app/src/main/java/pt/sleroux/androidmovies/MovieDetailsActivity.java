package pt.sleroux.androidmovies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.net.URL;
import java.util.List;

import pt.sleroux.androidmovies.data.Movie;
import pt.sleroux.androidmovies.utilities.NoNetworkException;
import pt.sleroux.androidmovies.utilities.TMDBUtilities;
import pt.sleroux.androidmovies.utilities.Utils;

public class MovieDetailsActivity extends AppCompatActivity {
    private TextView mMovieTitle;
    private ImageView mPoster;
    private TextView mReleaseDate;
    private TextView mAverage;
    private TextView mOverview;
    private TextView mErrorMessageDisplay;
    private LinearLayout mMovieDetails;
    private Context context;
    private Boolean isImageFitToScreen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.isImageFitToScreen = false;
        this.context = getApplicationContext();
        setContentView(R.layout.activity_movie_details);

        String id = getIntent().getStringExtra(context.getString(R.string.MOVIE_ID_INTENT_PARAMETER));

        mMovieTitle = (TextView) findViewById(R.id.tv_movie_title);
        mPoster = (ImageView)findViewById(R.id.iv_movie_poster);
        mReleaseDate = (TextView) findViewById(R.id.tv_release_date);
        mAverage = (TextView) findViewById(R.id.tv_average);
        mOverview = (TextView) findViewById(R.id.tv_overview);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mMovieDetails = (LinearLayout) findViewById(R.id.ll_movie_details);
        loadMovie(id);
    }

    private void loadMovie(String movieId) {
        new FetchMovieTask().execute(movieId);
    }

    public class FetchMovieTask extends AsyncTask<String, Void, Movie> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Movie doInBackground(String... params) {
            String movieID = params[0];
            URL movieURL = TMDBUtilities.buildMovieURL(movieID,context);

            try {
                String jsonMovieResponse = TMDBUtilities
                        .getResponseFromHttpUrl(movieURL, context);
                Movie movie = TMDBUtilities.parseJSONMovieResponse(MovieDetailsActivity.this, jsonMovieResponse,context);
                return movie;
            }catch(NoNetworkException nne){
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(final Movie movie) {
            if (movie != null) {
                showMovieDetails();
                mMovieTitle.setText(movie.getTitle());
                final URL posterURL = TMDBUtilities.buildThumbnailURL(movie,context);
                Picasso.with(MovieDetailsActivity.this.getApplicationContext()).load(posterURL.toString()).into(mPoster);
                mPoster.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Class destinationClass = PosterActivity.class;
                        Intent posterActivityIntent = new Intent(context, destinationClass);
                        final URL posterURL = TMDBUtilities.buildOriginalThumbnailURL(movie,context);
                        posterActivityIntent.putExtra(context.getString(R.string.POSTER_URL_INTENT_PARAMETER), posterURL.toString());
                        posterActivityIntent.putExtra(context.getString(R.string.MOVIE_ID_INTENT_PARAMETER), movie.getId());
                        startActivity(posterActivityIntent);
                    }
                });
                mReleaseDate.setText(Utils.formatDate(movie.getReleaseDate()));
                mAverage.setText(Double.toString(movie.getVoteAveage()));
                mOverview.setText(movie.getOverview());
            }else{
                showErrorMessage();
            }
        }
    }

    private void showMovieDetails() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mMovieDetails.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mMovieDetails.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }
}
