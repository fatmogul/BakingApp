package com.example.android.bakingapp;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class StepFragment extends Fragment implements ExoPlayer.EventListener {
    private static SimpleExoPlayer mExoPlayer;
    private static MediaSessionCompat mMediaSession;
    private SimpleExoPlayerView mPlayerView;
    private PlaybackStateCompat.Builder mStateBuilder;
    private Button mNextButton;
    private Button mPreviousButton;
    private TextView mDescription;
    private int mStepId;
    private ArrayList<ContentValues> mStepsList;
    private String mVideoUrl;
    private boolean mTwoPane;
    private ImageView mThumbnailView;
    private long mPosition;


    public StepFragment() {
    }

    public static void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
            mMediaSession.setActive(false);

        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.step_fragment, container, false);
        mPlayerView = rootView.findViewById(R.id.player_view);
        mNextButton = rootView.findViewById(R.id.next_button);
        mPreviousButton = rootView.findViewById(R.id.previous_button);
        mDescription = rootView.findViewById(R.id.description);
        mThumbnailView = rootView.findViewById(R.id.thumbnail_view);
        if (savedInstanceState != null) {
            if (mExoPlayer != null) {
                mExoPlayer.setPlayWhenReady(true);
                mExoPlayer.getPlaybackState();
            }
            mStepsList = (ArrayList<ContentValues>) savedInstanceState.getSerializable("steps");
            mStepId = savedInstanceState.getInt("id");
            mTwoPane = savedInstanceState.getBoolean("twoPane");
            mPosition = savedInstanceState.getLong("position");
            mVideoUrl = savedInstanceState.getString("videoUrl");
            mMediaSession = null;
            initializeMediaSession();
            initializePlayer(Uri.parse(mVideoUrl));
            mExoPlayer.seekTo(mPosition);
            mExoPlayer.setPlayWhenReady(true);
        }
        if (mStepId <= 0) {
            mPreviousButton.setVisibility(View.INVISIBLE);
        } else {
            mPreviousButton.setVisibility(View.VISIBLE);
        }
        if (mStepId >= mStepsList.size() - 1) {
            mNextButton.setVisibility(View.INVISIBLE);
        } else {
            mNextButton.setVisibility(View.VISIBLE);
        }
        if (mTwoPane) {
            Button nextButton = rootView.findViewById(R.id.next_button);
            nextButton.setVisibility(View.GONE);
            Button previousButton = rootView.findViewById(R.id.previous_button);
            previousButton.setVisibility(View.GONE);
        }

        ContentValues thisStep = mStepsList.get(mStepId);
        mVideoUrl = thisStep.get("videoUrl").toString();
        String thumbnailUrl = thisStep.get("thumbnailUrl").toString();
        String description = thisStep.get("description").toString();
        mDescription.setText(description);
        if (TextUtils.isEmpty(thumbnailUrl)) {
            mThumbnailView.setVisibility(View.GONE);
        } else {
            mThumbnailView.setVisibility(View.VISIBLE);
            Picasso.get().load(thumbnailUrl).into(mThumbnailView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    mThumbnailView.setVisibility(View.GONE);
                }
            });
        }
        if (!TextUtils.isEmpty(mVideoUrl)) {
            mPlayerView.setVisibility(View.VISIBLE);
            if (mExoPlayer == null) {
                initializeMediaSession();
                initializePlayer(Uri.parse(thisStep.get("videoUrl").toString()));
            } else {

            }
        } else {
            mPlayerView.setVisibility(View.GONE);
        }

        return rootView;
    }

    public void setStepData(int mStepId, ArrayList<ContentValues> mStepsList, boolean mTwoPane) {
        this.mStepId = mStepId;
        this.mStepsList = mStepsList;
        this.mTwoPane = mTwoPane;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mExoPlayer != null) {
            mPosition = mExoPlayer.getCurrentPosition();
            mExoPlayer.setPlayWhenReady(false);
            mExoPlayer.getPlaybackState();
            }
            mExoPlayer.release();

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mExoPlayer != null) {
            mPosition = mExoPlayer.getCurrentPosition();
            mExoPlayer.setPlayWhenReady(false);
            mExoPlayer.getPlaybackState();
        }
        mExoPlayer.release();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mExoPlayer != null) {
            mExoPlayer.setPlayWhenReady(true);
            mExoPlayer.getPlaybackState();
        }
    }

    private void initializeMediaSession() {

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(getContext(), "StepActivity");

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());


        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);

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
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if((playbackState == ExoPlayer.STATE_READY) && playWhenReady){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if((playbackState == ExoPlayer.STATE_READY)){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getContext(), "BakingApp");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable("steps", mStepsList);
        outState.putInt("id", mStepId);
        outState.putBoolean("twoPane", mTwoPane);
        outState.putLong("position", mPosition);
        outState.putString("videoUrl",mVideoUrl);
        super.onSaveInstanceState(outState);
    }

    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }
}


