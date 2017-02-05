package pt.sleroux.androidmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class PosterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = getApplicationContext();
        setContentView(R.layout.activity_poster);
        String moviePosterURL = getIntent().getStringExtra(context.getString(R.string.POSTER_URL_INTENT_PARAMETER));
        final String movieId = getIntent().getStringExtra(context.getString(R.string.MOVIE_ID_INTENT_PARAMETER));
        ImageView poster = (ImageView) findViewById(R.id.iv_poster_full);
        Picasso.with(PosterActivity.this.getApplicationContext()).load(moviePosterURL).into(poster);
        poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Class destinationClass = MovieDetailsActivity.class;
                Intent movieDetailsActivity = new Intent(PosterActivity.this.getApplicationContext(), destinationClass);
                movieDetailsActivity.putExtra(context.getString(R.string.MOVIE_ID_INTENT_PARAMETER), movieId);
                startActivity(movieDetailsActivity);
            }
        });
    }
}
