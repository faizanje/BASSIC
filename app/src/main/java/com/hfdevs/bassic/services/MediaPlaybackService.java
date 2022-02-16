package com.hfdevs.bassic.services;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.media.MediaBrowserServiceCompat;

import com.hfdevs.bassic.R;
import com.hfdevs.bassic.utils.Constants;
import com.hfdevs.bassic.utils.NotificationUtils;
import com.hfdevs.bassic.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MediaPlaybackService extends MediaBrowserServiceCompat {

    private final ArrayList<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();
    private MediaSessionCompat mediaSession;
    private MediaPlayer mediaPlayer;
    private ArrayList<MediaSessionCompat.QueueItem> currentQueue;
    private MediaBrowserCompat.MediaItem nowPlaying;
    private Long activeQueueItemId;

    MediaSessionCompat.Callback mediaCallback = new MediaSessionCompat.Callback() {
        @Override
        public void onPlay() {
            if (mediaPlayer != null) {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
            }

        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
/**
 * Start in foreground and display ongoing notification
 */
            NotificationUtils.createNotificationChannel(getApplicationContext(), "Playback");

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(getApplicationContext(), "Playback");
            builder.setOngoing(true);
            builder.setContentTitle("Now Playing");
            builder.setSmallIcon(R.drawable.ic_baseline_music_note_24);
            builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

            startForeground(-8000, builder.build());


            /**
             * start service so it isn't bound to the UI and can continue playing in the background
             */
            MediaPlaybackService.this.startService(
                    new Intent(getApplicationContext(), MediaPlaybackService.class));

            MediaBrowserCompat.MediaItem mediaItem = findMediaItemById(mediaId, mediaItems);
            playMediaItem(mediaItem);
            currentQueue = generateQueue(mediaId);
            activeQueueItemId = (long) Utils.findItemPositionInList(currentQueue, nowPlaying.getMediaId());
            mediaSession.setQueue(currentQueue);
        }


        @Override
        public void onSkipToQueueItem(long id) {
            super.onSkipToQueueItem(id);
        }

        @Override
        public void onPause() {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            }
        }

        @Override
        public void onSkipToNext() {
            if (currentQueue != null && nowPlaying != null) {
                /**
                 * Get the position of the nowPlaying mediaItem on the queue
                 */

                int activeQueItemPosition = Utils.findItemPositionInList(currentQueue, nowPlaying.getMediaId());


                /**
                 * if active queue position is last on queue, do nothing
                 */
                if (activeQueItemPosition == currentQueue.size() - 1) {
                    return;
                }

                MediaBrowserCompat.MediaItem nextMediaItem = new MediaBrowserCompat.MediaItem(
                        currentQueue.get(activeQueItemPosition + 1).getDescription(), 0);

                /**
                 * PLay the nex media item
                 */
                playMediaItem(nextMediaItem);
                activeQueueItemId = activeQueItemPosition + 1L;
            }
        }

        @Override
        public void onSkipToPrevious() {
            if (currentQueue != null && nowPlaying != null) {
                /**
                 * Get the position of the nowPlaying mediaItem on the queue
                 */

                int activeQueItemPosition = Utils.findItemPositionInList(currentQueue, nowPlaying.getMediaId());


                /**
                 * if active queue position is first on queue, do nothing
                 */
                if (activeQueItemPosition == 0) {
                    return;
                }


                /**
                 * Get the mediaDescription of the next item on the ques and create the mediaItem to be played
                 */

                MediaBrowserCompat.MediaItem nextMediaItem = new MediaBrowserCompat.MediaItem(
                        currentQueue.get(activeQueItemPosition - 1).getDescription(),
                        0
                );

                /**
                 * PLay the nex media item
                 */
                playMediaItem(nextMediaItem);
                activeQueueItemId = activeQueItemPosition - 1L;
            }
        }

        @Override
        public void onStop() {
            mediaSession.getMediaSession();
            if (mediaSession != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                MediaPlaybackService.this.stopForeground(true);
                MediaPlaybackService.this.stopSelf();
            }
        }

        @Override
        public void onSeekTo(long pos) {
            if (mediaPlayer != null) {
                mediaPlayer.seekTo((int) pos);
            }
        }


    };

    @Override
    public void onCreate() {
        super.onCreate();
        mediaSession = new MediaSessionCompat(getApplication(), MediaPlaybackService.class.getSimpleName());
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder().setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PAUSE);
        mediaSession.setPlaybackState(stateBuilder.build());
        mediaSession.setCallback(mediaCallback);
        setSessionToken(mediaSession.getSessionToken());
        mediaSession.setActive(true);
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return new BrowserRoot(
                getString(R.string.app_name),
                null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        Log.d(Constants.TAG, "onLoadChildren: parentId " + parentId);
        result.sendResult(null);
    }


    void onFinishedPlaying() {
        mediaCallback.onSkipToNext();
    }

    ArrayList<MediaSessionCompat.QueueItem> generateQueue(String mediaId) {
        long queueCount = 0L;
        ArrayList<MediaSessionCompat.QueueItem> queueItems = new ArrayList<>();
        for (MediaBrowserCompat.MediaItem mediaItem : mediaItems) {
            queueItems.add(new MediaSessionCompat.QueueItem(mediaItem.getDescription(), queueCount));
            queueCount++;
        }

        return queueItems;
    }

    void playMediaItem(MediaBrowserCompat.MediaItem mediaItem) {
        /**
         * Stop mediaPlayer if active before creating a new instance
         */
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.release();
                nowPlaying = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * Create new instance of mediaPlayer with the given mediaUri
         */
        mediaPlayer = MediaPlayer.create(getApplication(), mediaItem.getDescription().getMediaUri());
        mediaPlayer.start();
        nowPlaying = mediaItem;
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                onFinishedPlaying();
            }
        });

        /**
         * Start the playbackStateUpdateJob to send playback updates to listeners while playing
         */
        startPlaybackStateUpdateJob();
    }
    private void startPlaybackStateUpdateJob() {

//        mediaPlayBackStateUpdateJob?.let {
//            if (it.isActive) {
//                return
//            } else if (it.isCompleted) {
//                it.start()
//                return
//            }
//        }
//        mediaPlayBackStateUpdateJob = musicPlaybackServiceScope.launch {
//            while (true) {
//                try {
//                    if (mediaPlayer!!.isPlaying) {
//                        stateBuilder.setActiveQueueItemId(activeQueueItemId!!).setState(
//                                PlaybackStateCompat.STATE_PLAYING,
//                                mediaPlayer!!.currentPosition.toLong(),
//                                1F,
//                                1
//                        ).setActions(
//                                PlaybackStateCompat.ACTION_PAUSE or
//                                PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
//                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
//                        )
//                    } else {
//                        stateBuilder.setActiveQueueItemId(activeQueueItemId!!).setState(
//                                PlaybackStateCompat.STATE_PAUSED,
//                                mediaPlayer!!.currentPosition.toLong(),
//                                1F,
//                                1
//                        ).setActions(
//                                PlaybackStateCompat.ACTION_PLAY or
//                                PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
//                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
//                        )
//                    }
//
//                    val bundle = Bundle()
//                    bundle.putString("title", nowPlaying!!.description.title.toString())
//                    bundle.putString("subtitle", nowPlaying!!.description.subtitle.toString())
//                    bundle.putString("id", nowPlaying!!.description.mediaId)
//                    bundle.putString("uri", nowPlaying!!.description.mediaUri.toString())
//                    bundle.putInt("duration", mediaPlayer!!.duration)
//
//                    stateBuilder.setExtras(bundle)
//                    mediaSession?.setPlaybackState(stateBuilder.build())
//                    delay(500)
//                } catch (e: Exception) {
//                }
//
//                if (mediaSession == null || mediaPlayer == null || nowPlaying == null) {
//                    break
//                }
//            }
//            mediaPlayBackStateUpdateJob?.cancel()
//        }
    }


    MediaBrowserCompat.MediaItem findMediaItemById(
            String mediaId,
            List<MediaBrowserCompat.MediaItem> list) {
        MediaBrowserCompat.MediaItem mediaItem = null;
        for (MediaBrowserCompat.MediaItem item : list) {
            if (item.getDescription().getMediaId().equals(mediaId))
                mediaItem = item;
        }
        return mediaItem;
    }
}
