package com.andry.videoplayer;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.FileDataSource;

import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    private SimpleExoPlayer player;
    private SimpleExoPlayerView playerView;
    private MediaSource[] mediaSources;
    private Long currentPosition;
    private Boolean playWhenReady;

    private ArrayList<VideoDetails> videos;
    private View nextButton;
    private int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        videos = getIntent().getParcelableArrayListExtra(MainActivity.ALL_VIDEOS_EXTRA);
        position = getIntent().getIntExtra(MainActivity.POSITION_EXTRA, 0);

        playerView = (SimpleExoPlayerView) findViewById(R.id.activity_player_player);
        nextButton = findViewById(R.id.exo_custom_next);
        View prevButton = findViewById(R.id.exo_prev);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position < videos.size() - 1)
                    position++;
                player.prepare(mediaSources[position]);
                setEnableNextButton();
            }
        });
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position > 0)
                    position--;
                player.prepare(mediaSources[position]);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializePlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
    }

    private void releasePlayer() {
        currentPosition = player.getCurrentPosition();
        playWhenReady = player.getPlayWhenReady();
        player.release();
        player = null;
    }

    private void initializePlayer() {
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());

            createMediaSourceArr();
            playerView.setPlayer(player);
            player.setPlayWhenReady(true);
            getDataAfterPlayerRelease();
            player.prepare(mediaSources[position]);
            setEnableNextButton();
        }
    }

    private void getDataAfterPlayerRelease() {
        if (currentPosition != null) {
            player.seekTo(currentPosition);
            player.setPlayWhenReady(playWhenReady);
        }
    }

    private void createMediaSourceArr() {
        DataSource.Factory factory = new DataSource.Factory() {
            @Override
            public DataSource createDataSource() {
                return new FileDataSource();
            }
        };

        mediaSources = new MediaSource[videos.size()];
        for (int i = 0; i < videos.size(); i++) {
            MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(videos.get(i).getPath()), factory,
                    new DefaultExtractorsFactory(), null, null);
            mediaSources[i] = mediaSource;
        }
    }

    private void setEnableNextButton() {
        if (position == videos.size() - 1)
            setButtonEnabled(false, nextButton);
        else
            setButtonEnabled(true, nextButton);
    }

    private void setButtonEnabled(boolean enabled, View view) {
        if (view == null) {
            return;
        }
        view.setEnabled(enabled);
        view.setAlpha(enabled ? 1f : 0.3f);
        view.setVisibility(View.VISIBLE);
    }
}
