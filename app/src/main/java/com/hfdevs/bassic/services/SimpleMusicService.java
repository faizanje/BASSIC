package com.hfdevs.bassic.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.media.MediaBrowserServiceCompat;

import com.hfdevs.bassic.R;
import com.hfdevs.bassic.loaders.MusicLibrary;
import com.hfdevs.bassic.utils.Constants;
import com.hfdevs.bassic.utils.NotificationUtils;

import java.util.List;

public class SimpleMusicService extends MediaBrowserServiceCompat {

    MediaSessionCompat mediaSessionCompat;
    MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaSessionCompat = new MediaSessionCompat(getApplicationContext(), SimpleMusicService.class.getSimpleName());
        setSessionToken(mediaSessionCompat.getSessionToken());

        mediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);


        mediaSessionCompat.setCallback(new MediaSessionCompat.Callback() {

            @Override
            public void onPrepareFromMediaId(String mediaId, Bundle extras) {
                super.onPrepareFromMediaId(mediaId, extras);
                Log.d(Constants.TAG, "onPrepareFromMediaId: " + mediaId);
            }

            @Override
            public void onPrepareFromSearch(String query, Bundle extras) {
                super.onPrepareFromSearch(query, extras);
                Log.d(Constants.TAG, "onPrepareFromSearch: " + query);
            }

            @Override
            public void onPrepareFromUri(Uri uri, Bundle extras) {
                super.onPrepareFromUri(uri, extras);
                Log.d(Constants.TAG, "onPrepareFromUri: " + uri);
            }


            @Override
            public void onPlayFromMediaId(String mediaId, Bundle extras) {
                Log.d(Constants.TAG, "onPlayFromMediaId: " + mediaId);
                MediaBrowserCompat.MediaItem mediaItem = MusicLibrary.getMediaItemByID(mediaId);
                Log.d(Constants.TAG, "onPlayFromMediaId: " + mediaItem.getDescription().getMediaUri());

                NotificationUtils.createNotificationChannel(getApplicationContext(), "Playback");

                NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(getApplicationContext(), "Playback");
                builder.setOngoing(true);
                builder.setContentTitle("Now Playing");
                builder.setSmallIcon(R.drawable.ic_baseline_music_note_24);
                builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                startForeground(-8000, builder.build());

                SimpleMusicService.this.startService(
                        new Intent(getApplicationContext(), SimpleMusicService.class));

                mediaPlayer = MediaPlayer.create(getApplicationContext(), mediaItem.getDescription().getMediaUri());
                mediaPlayer.start();
            }

            @Override
            public void onPlayFromSearch(String query, Bundle extras) {
                super.onPlayFromSearch(query, extras);
                Log.d(Constants.TAG, "onPlayFromSearch: " + query);
            }

            @Override
            public void onPlayFromUri(Uri uri, Bundle extras) {
                super.onPlayFromUri(uri, extras);
                Log.d(Constants.TAG, "onPlayFromUri: " + uri);
            }

            @Override
            public void onPrepare() {
                super.onPrepare();
                Log.d(Constants.TAG, "onPrepare: ");
            }

            @Override
            public void onPlay() {
                super.onPlay();
                Log.d(Constants.TAG, "onPlay: ");
            }

            @Override
            public void onPause() {
                super.onPause();
                Log.d(Constants.TAG, "onPause: ");
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                Log.d(Constants.TAG, "onSkipToNext: ");
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                Log.d(Constants.TAG, "onSkipToPrevious: ");
            }

            @Override
            public void onStop() {
                super.onStop();
                Log.d(Constants.TAG, "onStop: ");
            }
        });
    }

//    void playMediaItem(MediaBrowserCompat.MediaItem mediaItem) {
//        /**
//         * Stop mediaPlayer if active before creating a new instance
//         */
//        if (mediaPlayer != null) {
//            try {
//                mediaPlayer.stop();
//                mediaPlayer.release();
//                nowPlaying = null;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        /**
//         * Create new instance of mediaPlayer with the given mediaUri
//         */
//        mediaPlayer = MediaPlayer.create(getApplication(), mediaItem.getDescription().getMediaUri());
//        mediaPlayer.start();
//        nowPlaying = mediaItem;
//        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                onFinishedPlaying();
//            }
//        });
//
//        /**
//         * Start the playbackStateUpdateJob to send playback updates to listeners while playing
//         */
//        startPlaybackStateUpdateJob();
//    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        Log.d(Constants.TAG, "onGetRoot: ");
        return new BrowserRoot(SimpleMusicService.class.getSimpleName(), null);
//        return null;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        Log.d(Constants.TAG, "SimpleMusicService onLoadChildren called: ");
        Log.d(Constants.TAG, "SimpleMusicService onLoadChildren parentId: " + parentId);
        Log.d(Constants.TAG, "SimpleMusicService onLoadChildren result: " + result);

        result.sendResult(MusicLibrary.getMediaItems());
    }
}
