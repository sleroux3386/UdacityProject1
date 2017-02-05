package pt.sleroux.androidmovies.utilities;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import pt.sleroux.androidmovies.MainActivity;
import pt.sleroux.androidmovies.MovieDetailsActivity;
import pt.sleroux.androidmovies.R;
import pt.sleroux.androidmovies.data.Movie;

/**
 * Created by benfi on 28/01/2017.
 */

public class TMDBUtilities {
    private static final String TAG = TMDBUtilities.class.getSimpleName();



    public static URL buildListMoviesURL(String page, MainActivity.Sort sort, Context context) {
        Uri builtUri = Uri.parse(sort== MainActivity.Sort.POPULARITY?context.getString(R.string.TMDB_MOVIE_POPULAR_ENDPOINT):context.getString(R.string.TMDB_MOVIE_TOPRATED_ENDPOINT)).buildUpon()
                .appendQueryParameter(context.getString(R.string.PAGE_PARAM), page)
                .appendQueryParameter(context.getString(R.string.API_KEY_PARAM), context.getString(R.string.TMBKey))
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Built URI " + url);
        return url;
    }

    public static URL buildMovieURL(String movieID, Context context) {
        Uri builtUri = Uri.parse(context.getString(R.string.TMDB_MOVIE_ENDPOINT)+movieID).buildUpon()
                .appendQueryParameter(context.getString(R.string.API_KEY_PARAM), context.getString(R.string.TMBKey))
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Built URI " + url);
        return url;
    }

    public static URL buildThumbnailURL(Movie movie,Context context) {
        String u = context.getString(R.string.TMDB_THUMBNAIL_ENDPOINT)+context.getString(R.string.IMAGE_SIZE)+movie.getPosterPath();
        Uri builtUri = Uri.parse(u).buildUpon()
                .appendQueryParameter(context.getString(R.string.API_KEY_PARAM), context.getString(R.string.TMBKey))
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Built URI " + url);
        return url;
    }

    public static String getResponseFromHttpUrl(URL url, Context context) throws IOException, NoNetworkException {
        Log.v(TAG,"Get response: "+url.toString());
        if(Utils.isOnline(context)){
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                InputStream in = urlConnection.getInputStream();

                Scanner scanner = new Scanner(in);
                scanner.useDelimiter("\\A");

                boolean hasInput = scanner.hasNext();
                if (hasInput) {
                    return scanner.next();
                } else {
                    return null;
                }
            } finally {
                urlConnection.disconnect();
            }
        }else{
            throw new NoNetworkException();
        }

    }

    public static List<Movie> parseJSONMoviesResponse(MainActivity mainActivity, String jsonMoviesResponse, Context context) throws JSONException, ParseException {
        List<Movie> movies = new ArrayList<Movie>();

        JSONObject moviesJSON = new JSONObject(jsonMoviesResponse);

        /* Is there an error? */
        /*
        if (moviesJSON.has(OWM_MESSAGE_CODE)) {
            int errorCode = forecastJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    return null;
                default:
                    return null;
            }
        }
        */
        JSONArray moviesArray = moviesJSON.getJSONArray(context.getString(R.string.TMDB_RESULT));

        for (int i = 0; i < moviesArray.length(); i++) {
            JSONObject movie = moviesArray.getJSONObject(i);
            Movie m = new Movie();
            m.setAdult(movie.optBoolean(context.getString(R.string.TMDB_ADULT)));
            m.setBackdropPath(movie.optString(context.getString(R.string.TMDB_BACKDROP_PATH)));
            m.setGenreIds(Utils.JSONArrayIntegerToIntegerList(movie.optJSONArray(context.getString(R.string.TMDB_GENRE_IDS))));
            m.setId(movie.optString(context.getString(R.string.TMDB_ID)));
            m.setOriginalLanguage(movie.optString(context.getString(R.string.TMDB_ORIGINAL_LANGUAGE)));
            m.setOriginalTitle(movie.optString(context.getString(R.string.TMDB_ORIGINAL_TITLE)));
            m.setOverview(movie.optString(context.getString(R.string.TMDB_OVERVIEW)));
            m.setPopularity(movie.optDouble(context.getString(R.string.TMDB_POPULARITY)));
            m.setPosterPath(movie.optString(context.getString(R.string.TMDB_POSTER_PATH)));
            m.setReleaseDate(Utils.parseDate(movie.optString(context.getString(R.string.TMDB_RELEASE_DATE))));
            m.setTitle(movie.optString(context.getString(R.string.TMDB_TITLE)));
            m.setVideo(movie.optBoolean(context.getString(R.string.TMDB_VIDEO)));
            m.setVoteAveage(movie.optDouble(context.getString(R.string.TMDB_VOTE_AVERAGE)));
            m.setVoteCount(movie.optInt(context.getString(R.string.TMDB_VOTE_COUNT)));
            movies.add(m);
        }

        return movies;
    }

    public static Movie parseJSONMovieResponse(MovieDetailsActivity movieDetailsActivity, String jsonMovieResponse, Context context) throws JSONException, ParseException {
        JSONObject movie = new JSONObject(jsonMovieResponse);

        Movie m = new Movie();
        m.setAdult(movie.optBoolean(context.getString(R.string.TMDB_ADULT)));
        m.setBackdropPath(movie.optString(context.getString(R.string.TMDB_BACKDROP_PATH)));
        m.setGenreIds(Utils.JSONArrayIntegerToIntegerList(movie.optJSONArray(context.getString(R.string.TMDB_GENRE_IDS))));
        m.setId(movie.optString(context.getString(R.string.TMDB_ID)));
        m.setOriginalLanguage(movie.optString(context.getString(R.string.TMDB_ORIGINAL_LANGUAGE)));
        m.setOriginalTitle(movie.optString(context.getString(R.string.TMDB_ORIGINAL_TITLE)));
        m.setOverview(movie.optString(context.getString(R.string.TMDB_OVERVIEW)));
        m.setPopularity(movie.optDouble(context.getString(R.string.TMDB_POPULARITY)));
        m.setPosterPath(movie.optString(context.getString(R.string.TMDB_POSTER_PATH)));
        m.setReleaseDate(Utils.parseDate(movie.optString(context.getString(R.string.TMDB_RELEASE_DATE))));
        m.setTitle(movie.optString(context.getString(R.string.TMDB_TITLE)));
        m.setVideo(movie.optBoolean(context.getString(R.string.TMDB_VIDEO)));
        m.setVoteAveage(movie.optDouble(context.getString(R.string.TMDB_VOTE_AVERAGE)));
        m.setVoteCount(movie.optInt(context.getString(R.string.TMDB_VOTE_COUNT)));
        return m;
    }

    public static URL buildOriginalThumbnailURL(Movie movie, Context context) {
        String u = context.getString(R.string.TMDB_THUMBNAIL_ENDPOINT)+context.getString(R.string.ORIGINAL_SIZE)+movie.getPosterPath();
        Uri builtUri = Uri.parse(u).buildUpon()
                .appendQueryParameter(context.getString(R.string.API_KEY_PARAM), context.getString(R.string.TMBKey))
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Built URI " + url);
        return url;
    }
}
