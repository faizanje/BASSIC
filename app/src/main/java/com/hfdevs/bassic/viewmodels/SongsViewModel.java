package com.hfdevs.bassic.viewmodels;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.media.browse.MediaBrowser;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.widget.Toast;

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

    private final MutableLiveData<Boolean> mIsPlaying = new MutableLiveData<Boolean>(false);
    MutableLiveData<List<Song>> songsMutableLiveData = new MutableLiveData<>();
    MutableLiveData<List<MediaBrowserCompat.MediaItem>> mediaItemsMutableLiveData = new MutableLiveData<>();
    MutableLiveData<Song> nowPlaying = new MutableLiveData<Song>();
    MutableLiveData<PlaybackStateCompat> playingPlaybackState = new MutableLiveData<PlaybackStateCompat>();
    //    MediaBrowserCompat mediaBrowserCompat;
//    MediaBrowserCompat.ConnectionCallback connectionCallback;
//    MediaControllerCompat mediaControllerCompat;
//    MediaBrowserCompat.SubscriptionCallback subscriptionCallback;
    private MediaBrowserHelper mMediaBrowserHelper;

    public SongsViewModel(@NonNull Application application) {
        super(application);
        connectToMediaPlaybackService();

    }

    public LiveData<PlaybackStateCompat> getPlayingPlaybackState() {
        return playingPlaybackState;
    }

    public MediaControllerCompat getmMediaController() {
        return mMediaBrowserHelper.getmMediaController();
    }

    private void connectToMediaPlaybackService() {
        if (mMediaBrowserHelper == null) {
            mMediaBrowserHelper = new MediaBrowserConnection(getApplication().getApplicationContext());
            mMediaBrowserHelper.registerCallback(new MediaBrowserListener());
            mMediaBrowserHelper.onStart();
        }
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


    public LiveData<List<MediaBrowserCompat.MediaItem>> getMediaItemsMutableLiveData() {
        return mediaItemsMutableLiveData;
    }

    public LiveData<Song> getNowPlaying() {
        return nowPlaying;
    }


    public void nextSong() {
        mMediaBrowserHelper.getTransportControls().skipToNext();
    }

    public void prevSong() {
        mMediaBrowserHelper.getTransportControls().skipToPrevious();

    }

    public void playFromMediaId(String mediaId) {
        mMediaBrowserHelper.getTransportControls().playFromMediaId(mediaId, null);
    }


    public void PlayPauseResume() {
        if (mIsPlaying.getValue()) {
            mMediaBrowserHelper.getTransportControls().pause();
        } else {
            mMediaBrowserHelper.getTransportControls().play();
        }
    }


    public void shuffle() {

        mMediaBrowserHelper.getTransportControls().setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_ALL);
    }

    public LiveData<Boolean> getIsPlaying() {
        return mIsPlaying;
    }

    public void seekto(int progress) {
        mMediaBrowserHelper.getTransportControls().seekTo(progress);
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

            Log.d(Constants.TAG, "onChildrenLoaded:  in songsViewModel: MediaItems size" + children.size());

            final MediaControllerCompat mediaController = getMediaController();
            mediaItemsMutableLiveData.setValue(children);
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
            super.onPlaybackStateChanged(playbackState);
            Log.d(Constants.TAG, "onPlaybackStateChanged: Called inside songsViewModel");
            playingPlaybackState.setValue(playbackState);

            if (playbackState != null &&
                    playbackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
                mIsPlaying.setValue(true);
                Log.d(Constants.TAG, "onPlaybackStateChanged: inside songsViewModel position: " + playbackState.getPosition());
            } else {
                mIsPlaying.setValue(false);
            }
//            mMediaControlsImage.setPressed(mIsPlaying);
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat mediaMetadata) {
            if (mediaMetadata == null) {
                return;
            }
            Log.d(Constants.TAG, "onMetadataChanged: called inside songsViewModel");
            Log.d(Constants.TAG, "onMetadataChanged: Title " + mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));

//            for (String s : mediaMetadata.getBundle().keySet()) {
//                Log.d(Constants.TAG, String.format("%s: %s", s, mediaMetadata.getString(s)));
//            }

            String TITLE = mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE);
            String ARTIST = mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST);
            String MEDIA_ID = mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID);
            int DURATION = (int) mediaMetadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
            Song song = new Song(TITLE, DURATION, ARTIST, MEDIA_ID);
            nowPlaying.setValue(song);


//
//
//            nowPlayingTitle.setValue(TITLE);
//            nowPlayingArtist.setValue(ARTIST);

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
            Log.d(Constants.TAG, "onQueueChanged: Called inside SongsViewModel");
            super.onQueueChanged(queue);
        }
    }


}
