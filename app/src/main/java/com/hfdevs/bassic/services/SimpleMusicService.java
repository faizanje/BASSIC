package com.hfdevs.bassic.services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.media.session.MediaButtonReceiver;

import com.hfdevs.bassic.R;
import com.hfdevs.bassic.loaders.MusicLibrary;
import com.hfdevs.bassic.utils.Constants;
import com.hfdevs.bassic.utils.MediaNotificationManager;
import com.hfdevs.bassic.utils.NotificationUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class SimpleMusicService extends MediaBrowserServiceCompat {

    public MediaSessionCallback mCallback;
    MediaPlayer mediaPlayer;
    //    MediaSessionCompat mediaSessionCompat;
    private MediaSessionCompat mSession;
    private MediaNotificationManager mMediaNotificationManager;
    private PlayerAdapter mPlayback;
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


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MediaButtonReceiver.handleIntent(mSession, intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        mMediaNotificationManager.onDestroy();
        mPlayback.stop();
        mSession.release();
        Log.d(Constants.TAG, "onDestroy: MediaPlayerAdapter stopped, and MediaSession released");
    }

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
        private final HashMap<String, MediaSessionCompat.QueueItem> queueItemHashMap = new HashMap<>();
        private final List<MediaSessionCompat.QueueItem> mPlaylistOriginal = new ArrayList<>();
        private int mQueueIndex = -1;
        private MediaMetadataCompat mPreparedMedia;
        private boolean isRandom = false;

        @Override
        public void onAddQueueItem(MediaDescriptionCompat description) {
            Log.d(Constants.TAG, "onAddQueueItem: Called SimpleMusicService");
            Log.d(Constants.TAG, String.format("onAddQueueItem: %s. Index: %s", description.getTitle(), description.hashCode()));
            MediaSessionCompat.QueueItem queueItem = new MediaSessionCompat.QueueItem(description, description.hashCode());
            mPlaylist.add(queueItem);
            queueItemHashMap.put(description.getMediaId(),queueItem);
            mQueueIndex = (mQueueIndex == -1) ? 0 : mQueueIndex;
            mSession.setQueue(mPlaylist);
        }

        @Override
        public void onRemoveQueueItem(MediaDescriptionCompat description) {
            Log.d(Constants.TAG, "onRemoveQueueItem: Called SimpleMusicService");
            MediaSessionCompat.QueueItem queueItem = new MediaSessionCompat.QueueItem(description, description.hashCode());
            mPlaylist.remove(queueItem);
            queueItemHashMap.remove(description.getMediaId());
//            mPlaylist.remove(new MediaSessionCompat.QueueItem(description, description.hashCode()));
            mQueueIndex = (mPlaylist.isEmpty()) ? -1 : mQueueIndex;
            mSession.setQueue(mPlaylist);
        }


        @Override
        public void onPrepareFromMediaId(String mediaId, Bundle extras) {
            Log.d(Constants.TAG, "onPrepare: Called SimpleMusicService");
            if (mQueueIndex < 0 && mPlaylist.isEmpty()) {
                // Nothing to play.
                return;
            }

            mPreparedMedia = MusicLibrary.getMetadata(mediaId);
            mSession.setMetadata(mPreparedMedia);

            if (!mSession.isActive()) {
                mSession.setActive(true);
            }
        }

        @Override
        public void onPrepare() {
            Log.d(Constants.TAG, "onPrepare: Called SimpleMusicService");
            if (mQueueIndex < 0 && mPlaylist.isEmpty()) {
                // Nothing to play.
                return;
            }

            final String mediaId = mPlaylist.get(mQueueIndex).getDescription().getMediaId();
            mPreparedMedia = MusicLibrary.getMetadata(mediaId);
            mSession.setMetadata(mPreparedMedia);

            if (!mSession.isActive()) {
                mSession.setActive(true);
            }
        }

        @Override
        public void onPlay() {
            Log.d(Constants.TAG, "onPlay: Called SimpleMusicService");
            if (!isReadyToPlay()) {
                // Nothing to play.
                Log.d(Constants.TAG, "not ready to play: ");
                return;
            }

            if (mPreparedMedia == null) {
                onPrepare();
            }

            mPlayback.playFromMedia(mPreparedMedia);
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            Log.d(Constants.TAG, "onPlayFromMediaId: Called SimpleMusicService" + mediaId);
            if (!isReadyToPlay()) {
                // Nothing to play.
                Log.d(Constants.TAG, "not ready to play: ");
                return;
            }

//            if (mPreparedMedia == null) {
            onPrepareFromMediaId(mediaId, null);
//            }

            mQueueIndex = mPlaylist.indexOf(queueItemHashMap.get(mediaId));
            mPlayback.playFromMedia(mPreparedMedia);
        }

        @Override
        public void onPause() {
            Log.d(Constants.TAG, "onPause: Called");
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
            Log.d(Constants.TAG, "onSkipToNext: QueueIndex: " + mQueueIndex);
            mPreparedMedia = null;
            onPlay();
        }

        @Override
        public void onSetShuffleMode(int shuffleMode) {
            isRandom = shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_ALL;
            if (isRandom) {
                long seed = System.nanoTime();
                Collections.shuffle(mPlaylist, new Random(seed));
            } else {

            }
            super.onSetShuffleMode(shuffleMode);
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
        private final Handler handler;

        MediaPlayerListener() {
            mServiceManager = new ServiceManager();
            handler = new Handler();
        }


        @Override
        public void onPlaybackCompleted() {
//            mSession.getController().getTransportControls().skipToNext();
        }

        @Override
        public void onPlaybackStateChange(PlaybackStateCompat state) {
            // Report the state to the MediaSession.
            Log.d(Constants.TAG, "onPlaybackStateChange: Called");
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
            mSession.setPlaybackState(state);
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
