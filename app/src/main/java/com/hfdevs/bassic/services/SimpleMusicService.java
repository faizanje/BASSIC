package com.hfdevs.bassic.services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.media.MediaBrowserServiceCompat;

import com.hfdevs.bassic.R;
import com.hfdevs.bassic.loaders.MusicLibrary;
import com.hfdevs.bassic.utils.Constants;
import com.hfdevs.bassic.utils.MediaNotificationManager;
import com.hfdevs.bassic.utils.NotificationUtils;

import java.util.ArrayList;
import java.util.List;

public class SimpleMusicService extends MediaBrowserServiceCompat {

    MediaPlayer mediaPlayer;
    //    MediaSessionCompat mediaSessionCompat;
    private MediaSessionCompat mSession;
    private MediaNotificationManager mMediaNotificationManager;
    private PlayerAdapter mPlayback;
    private MediaSessionCallback mCallback;
    private boolean mServiceInStartedState;

    @Override
    public void onCreate() {
        super.onCreate();
        mSession = new MediaSessionCompat(getApplicationContext(), SimpleMusicService.class.getSimpleName());
        setSessionToken(mSession.getSessionToken());
        mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mCallback = new MediaSessionCallback();
        mSession.setCallback(mCallback);

        mMediaNotificationManager = new MediaNotificationManager(this);

        mPlayback = new MediaPlayerAdapter(this, new MediaPlayerListener());

//        mediaSessionCompat.setCallback(new MediaSessionCompat.Callback() {
//
//            @Override
//            public void onPrepareFromMediaId(String mediaId, Bundle extras) {
//                super.onPrepareFromMediaId(mediaId, extras);
//                Log.d(Constants.TAG, "onPrepareFromMediaId: " + mediaId);
//            }
//
//            @Override
//            public void onPrepareFromSearch(String query, Bundle extras) {
//                super.onPrepareFromSearch(query, extras);
//                Log.d(Constants.TAG, "onPrepareFromSearch: " + query);
//            }
//
//            @Override
//            public void onPrepareFromUri(Uri uri, Bundle extras) {
//                super.onPrepareFromUri(uri, extras);
//                Log.d(Constants.TAG, "onPrepareFromUri: " + uri);
//            }
//
//
//            @Override
//            public void onPlayFromMediaId(String mediaId, Bundle extras) {
//                Log.d(Constants.TAG, "onPlayFromMediaId: " + mediaId);
//                MediaBrowserCompat.MediaItem mediaItem = MusicLibrary.getMediaItemByID(mediaId);
//                Log.d(Constants.TAG, "onPlayFromMediaId: " + mediaItem.getDescription().getMediaUri());
//
//                NotificationUtils.createNotificationChannel(getApplicationContext(), "Playback");
//
//                NotificationCompat.Builder builder =
//                        new NotificationCompat.Builder(getApplicationContext(), "Playback");
//                builder.setOngoing(true);
//                builder.setContentTitle("Now Playing");
//                builder.setSmallIcon(R.drawable.ic_baseline_music_note_24);
//                builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
//
//                startForeground(-8000, builder.build());
//
//                SimpleMusicService.this.startService(
//                        new Intent(getApplicationContext(), SimpleMusicService.class));
//
//                mediaPlayer = MediaPlayer.create(getApplicationContext(), mediaItem.getDescription().getMediaUri());
//                mediaPlayer.start();
//            }
//
//            @Override
//            public void onPlayFromSearch(String query, Bundle extras) {
//                super.onPlayFromSearch(query, extras);
//                Log.d(Constants.TAG, "onPlayFromSearch: " + query);
//            }
//
//            @Override
//            public void onPlayFromUri(Uri uri, Bundle extras) {
//                super.onPlayFromUri(uri, extras);
//                Log.d(Constants.TAG, "onPlayFromUri: " + uri);
//            }
//
//            @Override
//            public void onPrepare() {
//                super.onPrepare();
//                Log.d(Constants.TAG, "onPrepare: ");
//            }
//
//            @Override
//            public void onPlay() {
//                super.onPlay();
//                Log.d(Constants.TAG, "onPlay: ");
//            }
//
//            @Override
//            public void onPause() {
//                super.onPause();
//                Log.d(Constants.TAG, "onPause: ");
//            }
//
//            @Override
//            public void onSkipToNext() {
//                super.onSkipToNext();
//                Log.d(Constants.TAG, "onSkipToNext: ");
//            }
//
//            @Override
//            public void onSkipToPrevious() {
//                super.onSkipToPrevious();
//                Log.d(Constants.TAG, "onSkipToPrevious: ");
//            }
//
//            @Override
//            public void onStop() {
//                super.onStop();
//                Log.d(Constants.TAG, "onStop: ");
//            }
//        });
    }

    @Override
    public void onDestroy() {
        mMediaNotificationManager.onDestroy();
        mPlayback.stop();
        mSession.release();
        Log.d(Constants.TAG, "onDestroy: MediaPlayerAdapter stopped, and MediaSession released");
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

    // MediaSession Callback: Transport Controls -> MediaPlayerAdapter
    public class MediaSessionCallback extends MediaSessionCompat.Callback {
        private final List<MediaSessionCompat.QueueItem> mPlaylist = new ArrayList<>();
        private int mQueueIndex = -1;
        private MediaMetadataCompat mPreparedMedia;

        @Override
        public void onAddQueueItem(MediaDescriptionCompat description) {
            mPlaylist.add(new MediaSessionCompat.QueueItem(description, description.hashCode()));
            mQueueIndex = (mQueueIndex == -1) ? 0 : mQueueIndex;
            mSession.setQueue(mPlaylist);
        }

        @Override
        public void onRemoveQueueItem(MediaDescriptionCompat description) {
            mPlaylist.remove(new MediaSessionCompat.QueueItem(description, description.hashCode()));
            mQueueIndex = (mPlaylist.isEmpty()) ? -1 : mQueueIndex;
            mSession.setQueue(mPlaylist);
        }

        @Override
        public void onPrepare() {
            if (mQueueIndex < 0 && mPlaylist.isEmpty()) {
                // Nothing to play.
                return;
            }

            final String mediaId = mPlaylist.get(mQueueIndex).getDescription().getMediaId();
            mPreparedMedia = MusicLibrary.getMetadata(SimpleMusicService.this.getApplicationContext(), mediaId);
            mSession.setMetadata(mPreparedMedia);

            if (!mSession.isActive()) {
                mSession.setActive(true);
            }
        }

        @Override
        public void onPlay() {
            Log.d(Constants.TAG, "onPlay: Called");
            if (!isReadyToPlay()) {
                // Nothing to play.
                Log.d(Constants.TAG, "not ready to play: ");
                return;
            }

            if (mPreparedMedia == null) {
                onPrepare();
            }

            mPlayback.playFromMedia(mPreparedMedia);
            Log.d(Constants.TAG, "onPlayFromMediaId: MediaSession active");
        }

        @Override
        public void onPause() {
            mPlayback.pause();
        }

        @Override
        public void onStop() {
            mPlayback.stop();
            mSession.setActive(false);
        }

        @Override
        public void onSkipToNext() {
            mQueueIndex = (++mQueueIndex % mPlaylist.size());
            mPreparedMedia = null;
            onPlay();
        }

        @Override
        public void onSkipToPrevious() {
            mQueueIndex = mQueueIndex > 0 ? mQueueIndex - 1 : mPlaylist.size() - 1;
            mPreparedMedia = null;
            onPlay();
        }

        @Override
        public void onSeekTo(long pos) {
            mPlayback.seekTo(pos);
        }

        private boolean isReadyToPlay() {
            return (!mPlaylist.isEmpty());
        }
    }

    // MediaPlayerAdapter Callback: MediaPlayerAdapter state -> MusicService.
    public class MediaPlayerListener extends PlaybackInfoListener {

        private final ServiceManager mServiceManager;

        MediaPlayerListener() {
            mServiceManager = new ServiceManager();
        }

        @Override
        public void onPlaybackStateChange(PlaybackStateCompat state) {
            // Report the state to the MediaSession.
            mSession.setPlaybackState(state);

            // Manage the started state of this service.
            switch (state.getState()) {
                case PlaybackStateCompat.STATE_PLAYING:
                    mServiceManager.moveServiceToStartedState(state);
                    break;
                case PlaybackStateCompat.STATE_PAUSED:
                    mServiceManager.updateNotificationForPause(state);
                    break;
                case PlaybackStateCompat.STATE_STOPPED:
                    mServiceManager.moveServiceOutOfStartedState(state);
                    break;
            }
        }

        class ServiceManager {

            private void moveServiceToStartedState(PlaybackStateCompat state) {
                Notification notification =
                        mMediaNotificationManager.getNotification(
                                mPlayback.getCurrentMedia(), state, getSessionToken());

                Log.d(Constants.TAG, "moveServiceToStartedState: called");
                if (!mServiceInStartedState) {
                    ContextCompat.startForegroundService(
                            SimpleMusicService.this,
                            new Intent(SimpleMusicService.this, SimpleMusicService.class));
                    mServiceInStartedState = true;
                }

                startForeground(MediaNotificationManager.NOTIFICATION_ID, notification);
            }

            private void updateNotificationForPause(PlaybackStateCompat state) {
                stopForeground(false);
                Notification notification =
                        mMediaNotificationManager.getNotification(
                                mPlayback.getCurrentMedia(), state, getSessionToken());
                mMediaNotificationManager.getNotificationManager()
                        .notify(MediaNotificationManager.NOTIFICATION_ID, notification);
            }

            private void moveServiceOutOfStartedState(PlaybackStateCompat state) {
                stopForeground(true);
                stopSelf();
                mServiceInStartedState = false;
            }
        }

    }
}
