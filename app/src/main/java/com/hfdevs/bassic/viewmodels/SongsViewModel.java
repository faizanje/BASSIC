package com.hfdevs.bassic.viewmodels;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.media.browse.MediaBrowser;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hfdevs.bassic.loaders.MusicLibrary;
import com.hfdevs.bassic.loaders.SongProvider;
import com.hfdevs.bassic.models.Song;
import com.hfdevs.bassic.services.MediaBrowserHelper;
import com.hfdevs.bassic.services.SimpleMusicService;
import com.hfdevs.bassic.utils.Constants;
import com.hfdevs.bassic.utils.NotificationUtils;

import java.io.File;
import java.util.List;

public class SongsViewModel extends AndroidViewModel {

    MutableLiveData<List<Song>> songsMutableLiveData = new MutableLiveData<>();
    MutableLiveData<Song> nowPlaying = new MutableLiveData<Song>();
    MediaBrowserCompat mediaBrowserCompat;
    MediaBrowserCompat.ConnectionCallback connectionCallback;
    MediaControllerCompat mediaControllerCompat;
    MediaBrowserCompat.SubscriptionCallback subscriptionCallback;
    private boolean mIsPlaying;
    private MediaBrowserHelper mMediaBrowserHelper;

    public SongsViewModel(@NonNull Application application) {
        super(application);
        connectToMediaPlaybackService();
    }

    private void connectToMediaPlaybackService() {
        mMediaBrowserHelper = new MediaBrowserConnection(getApplication().getApplicationContext());
        mMediaBrowserHelper.registerCallback(new MediaBrowserListener());

        mMediaBrowserHelper.onStart();

//        subscriptionCallback = new MediaBrowserCompat.SubscriptionCallback() {
//
//            @Override
//            public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children) {
//                Log.d(Constants.TAG, "onChildrenLoaded inside subscriptionCallback called: ");
//                super.onChildrenLoaded(parentId, children);
//            }
//
//            @Override
//            public void onError(@NonNull String parentId) {
//                super.onError(parentId);
//            }
//
//            @Override
//            public void onError(@NonNull String parentId, @NonNull Bundle options) {
//                super.onError(parentId, options);
//            }
//        };
//        connectionCallback = new MediaBrowserCompat.ConnectionCallback() {
//
//
//            @Override
//            public void onConnected() {
//                super.onConnected();
//                try {
//                    Log.d(Constants.TAG, "onConnected: Called");
//                    mediaControllerCompat = new MediaControllerCompat(
//                            getApplication().getApplicationContext(),
//                            mediaBrowserCompat.getSessionToken()
//                    );
//
//                    mediaBrowserCompat.subscribe(mediaBrowserCompat.getRoot(), subscriptionCallback);
//                    mediaControllerCompat.registerCallback(new MediaControllerCompat.Callback() {
//                        @Override
//                        public void onPlaybackStateChanged(PlaybackStateCompat state) {
//                            Log.d(Constants.TAG, "onPlaybackStateChanged: " + state);
//                            super.onPlaybackStateChanged(state);
//                        }
//                    });
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                    Log.d(Constants.TAG, "onConnected: Exception" + e.getLocalizedMessage())
//                    ;
//                }
//            }
//
//            @Override
//            public void onConnectionSuspended() {
//                super.onConnectionSuspended();
//                Log.d(Constants.TAG, "onConnectionSuspended: ");
//            }
//
//            @Override
//            public void onConnectionFailed() {
//                super.onConnectionFailed();
//                Log.d(Constants.TAG, "onConnectionFailed: ");
//            }
//        };
//        mediaBrowserCompat = new MediaBrowserCompat(
//                getApplication().getApplicationContext(),
//                new ComponentName(getApplication(), SimpleMusicService.class),
//                connectionCallback,
//                null
//        );
//        mediaBrowserCompat.connect();
    }

    public void refreshSongsList() {
        final List<Song> songs = SongProvider.getSongs(SongProvider.makeSongCursor(
                getApplication(), SongProvider.getSongLoaderSortOrder())
        );
        for (Song song : songs) {
//            Log.d(Constants.TAG, "getFiles: " + song);
        }
        songsMutableLiveData.setValue(songs);
        MusicLibrary.buildMediaItems(songs);
    }

    public LiveData<List<Song>> getSongsLiveData() {
        return songsMutableLiveData;
    }

    public LiveData<Song> getNowPlaying() {
        return nowPlaying;
    }

    public void setNowPlaying(Song nowPlaying) {
        this.nowPlaying.setValue(nowPlaying);
    }

    public void nextSong() {
        List<Song> songs = songsMutableLiveData.getValue();
        int currentIndex = songs.indexOf(nowPlaying.getValue());
        currentIndex++;
        int numSongs = songs.size();
        Song song = songs.get(currentIndex % numSongs);
        nowPlaying.setValue(song);
    }

    public void prevSong() {
        List<Song> songs = songsMutableLiveData.getValue();
        int currentIndex = songs.indexOf(nowPlaying.getValue());
        currentIndex--;
        int numSongs = songs.size();
        Song song = songs.get((currentIndex % numSongs + numSongs) % numSongs);
        nowPlaying.setValue(song);
    }

    public void playFromUri(String path) {
        mediaControllerCompat.getTransportControls().playFromUri(Uri.fromFile(new File(path)), null);
    }

    public void play() {
        mMediaBrowserHelper.getTransportControls().play();
//        mediaControllerCompat.getTransportControls().playFromMediaId(nowPlaying.getValue().getmId(), null);
    }

    /**
     * and implement our app specific desires.
     */
    private class MediaBrowserConnection extends MediaBrowserHelper {
        private MediaBrowserConnection(Context context) {
            super(context, SimpleMusicService.class);
        }

        @Override
        protected void onConnected(@NonNull MediaControllerCompat mediaController) {
//            mSeekBarAudio.setMediaController(mediaController);
        }

        @Override
        protected void onChildrenLoaded(@NonNull String parentId,
                                        @NonNull List<MediaBrowserCompat.MediaItem> children) {
            super.onChildrenLoaded(parentId, children);

            final MediaControllerCompat mediaController = getMediaController();

            // Queue up all media items for this simple sample.
            for (final MediaBrowserCompat.MediaItem mediaItem : children) {
                mediaController.addQueueItem(mediaItem.getDescription());
            }

            // Call prepare now so pressing play just works.
            mediaController.getTransportControls().prepare();
        }
    }

    /**
     * Implementation of the {@link MediaControllerCompat.Callback} methods we're interested in.
     * <p>
     * Here would also be where one could override
     * {@code onQueueChanged(List<MediaSessionCompat.QueueItem> queue)} to get informed when items
     * are added or removed from the queue. We don't do this here in order to keep the UI
     * simple.
     */
    private class MediaBrowserListener extends MediaControllerCompat.Callback {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat playbackState) {
            Log.d(Constants.TAG, "onPlaybackStateChanged: Called");
            mIsPlaying = playbackState != null &&
                    playbackState.getState() == PlaybackStateCompat.STATE_PLAYING;
//            mMediaControlsImage.setPressed(mIsPlaying);
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat mediaMetadata) {
            if (mediaMetadata == null) {
                return;
            }
            Log.d(Constants.TAG, "onMetadataChanged: called");
//            mTitleTextView.setText(
//                    mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
//            mArtistTextView.setText(
//                    mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));
//            mAlbumArt.setImageBitmap(MusicLibrary.getAlbumBitmap(
//                    MainActivity.this,
//                    mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)));
        }

        @Override
        public void onSessionDestroyed() {
            super.onSessionDestroyed();
        }

        @Override
        public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {
            super.onQueueChanged(queue);
        }
    }
}
