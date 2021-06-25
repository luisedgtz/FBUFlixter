package com.example.flixter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixter.databinding.ActivityMovieDetailsBinding;
import com.example.flixter.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

public class MovieDetailsActivity extends AppCompatActivity {

    //Movie to display
    Movie movie;
    TextView tvTitleDetails;
    TextView tvOverviewDetails;
    RatingBar rbVoteAverage;
    ImageView ivTrailer;
    ImageView btnYt;
    Context context = this;

    public static final String VIDEO_URL = "https://api.themoviedb.org/3/movie/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMovieDetailsBinding binding = ActivityMovieDetailsBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();

        setContentView(view);

        tvTitleDetails = (TextView) findViewById(R.id.tvTitleDetails);
        tvOverviewDetails = (TextView) findViewById(R.id.tvOverviewDetails);
        rbVoteAverage = (RatingBar) findViewById(R.id.rbVoteAverage);
        ivTrailer = (ImageView) findViewById(R.id.ivTrailer);
        btnYt = (ImageView) findViewById(R.id.btnYt);

        //Unwrap movie, using simple name a key
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Log.d("MovieDDetailsActivity", String.format("Showing details for '%s", movie.getTitle()));

        //Set title and overview
        tvTitleDetails.setText(movie.getTitle());
        tvOverviewDetails.setText(movie.getOverview());

        float voteAverage = (float)movie.getVoteAverage();
        rbVoteAverage.setRating(voteAverage / 2.0f);

        int radius = 100;
        int margin = 10;

        ivTrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("VideoOnCLick", "VideoClicked");

                String movieId = Integer.toString(movie.getId());

                //Call to endpoint for videoKey
                AsyncHttpClient client = new AsyncHttpClient();
                client.get(VIDEO_URL + movieId + "/videos?api_key=362f4a5d04b13985dcd5acca329bb949", new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Headers headers, JSON json) {
                        Log.d("GetVideoId", "onSuccess");
                        JSONObject jsonObject = json.jsonObject;
                        try {
                            JSONArray results = jsonObject.getJSONArray("results");
                            String IdVideo = results.getJSONObject(0).getString("key");
                            Log.i("GetVideoId", "Results: " + IdVideo);

                            Intent intent = new Intent(context, MovieTrailerActivity.class);
                            intent.putExtra(Movie.class.getSimpleName(), Parcels.wrap(IdVideo));
                            context.startActivity(intent);
                        } catch (JSONException e) {
                            Log.e("GetVideoId", "Hit json exception");

                        }
                    }

                    @Override
                    public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                        Log.d("GetVideoId", "onFailure");
                    }
                });
            }
        });

        Glide.with(this).load(movie.getBackdropPath()).transform(new RoundedCornersTransformation(radius, margin)).into(ivTrailer);
    }
}