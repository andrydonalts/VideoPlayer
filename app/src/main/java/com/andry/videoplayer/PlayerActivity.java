package com.andry.videoplayer;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.FileDataSource;

import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    private SimpleExoPlayer player;
    private SimpleExoPlayerView playerView;
    private Long currentPosition;
    private Boolean playWhenReady;

    private ArrayList<VideoDetails> videos;
    private boolean isPlaylist;
    private View nextButton;
    private int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        videos = getIntent().getParcelableArrayListExtra(MainActivity.ALL_VIDEOS_EXTRA);
        position = getIntent().getIntExtra(MainActivity.POSITION_EXTRA, 0);
        isPlaylist = getIntent().getBooleanExtra(MainActivity.IS_PLAYLIST_EXTRA, false);

        playerView = (SimpleExoPlayerView) findViewById(R.id.activity_player_player);
        nextButton = findViewById(R.id.exo_custom_next);
        View prevButton = findViewById(R.id.exo_prev);

        nextButton.setVisibility(View.VISIBLE);
        nextButton.setEnabled(true);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNextVideo();
            }
        });
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPrevVideo();
            }
        });

    }

    private void startNextVideo() {
        if (position < videos.size() - 1)
            position++;
        player.prepare(createMediaSource(position));
        setEnableNextButton();
    }

    private void startPrevVideo() {
        if (position > 0)
            position--;
        player.prepare(createMediaSource(position));
        setEnableNextButton();
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

            playerView.setPlayer(player);
            player.setPlayWhenReady(true);
            getDataAfterPlayerRelease();
            player.prepare(createMediaSource(position));
            player.addListener(new PlayerEventListener());
            setEnableNextButton();
        }
    }

    private void getDataAfterPlayerRelease() {
        if (currentPosition != null) {
            player.seekTo(currentPosition);
            player.setPlayWhenReady(playWhenReady);
        }
    }

    private MediaSource createMediaSource(int position) {
        DataSource.Factory factory = new DataSource.Factory() {
            @Override
            public DataSource createDataSource() {
                return new FileDataSource();
            }
        };

        return new ExtractorMediaSource(Uri.parse(videos.get(position).getPath()), factory,
                new DefaultExtractorsFactory(), null, null);
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

    // class is used to implement next video autoplay in playlist mode.
    class PlayerEventListener implements Player.EventListener {

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if (playbackState == Player.STATE_ENDED && isPlaylist && position != (videos.size()-1)) {
                startNextVideo();
            }
        }

        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {
        }
        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        }
        @Override
        public void onLoadingChanged(boolean isLoading) {
        }
        @Override
        public void onRepeatModeChanged(int repeatMode) {
        }
        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
        }
        @Override
        public void onPlayerError(ExoPlaybackException error) {
        }
        @Override
        public void onPositionDiscontinuity(int reason) {
        }
        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
        }
        @Override
        public void onSeekProcessed() {
        }
    }
}
