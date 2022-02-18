/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hfdevs.bassic.loaders;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadata;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;


import com.hfdevs.bassic.models.Song;
import com.hfdevs.bassic.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;


public class MusicLibrary {

    private static final TreeMap<String, MediaBrowserCompat.MediaItem>
            music = new TreeMap<>();
    //    private static final HashMap<String, Integer> albumRes = new HashMap<>();
    private static final HashMap<String, String> musicFileName = new HashMap<>();
    private static final TreeMap<String, Song> songs = new TreeMap<>();

    public static String getRoot() {
        return "root";
    }

    public static String getMusicFilename(String mediaId) {
        return musicFileName.containsKey(mediaId) ? musicFileName.get(mediaId) : null;
    }


    public static List<MediaBrowserCompat.MediaItem> getMediaItems() {
        //            result.add(
        //                    new MediaBrowserCompat.MediaItem(
        //                            metadata.getDescription(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
        return new ArrayList<>(music.values());
    }

    public static MediaBrowserCompat.MediaItem getMediaItemByID(String mediaId) {
        return music.get(mediaId);
    }

    public static MediaMetadataCompat getMetadata(Context context, String mediaId) {
        Song song = songs.get(mediaId);
       MediaMetadataCompat.Builder  metaDataBuilder = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, song.getmId())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM,song.getAlbumName())
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST,song.getArtistName())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE,song.getSongTitle())
               .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, song.getSongDuration());

//        MediaMetadataCompat metadataWithoutBitmap = music.get(mediaId);
//        Bitmap albumArt = getAlbumBitmap(context, mediaId);


        // Since MediaMetadataCompat is immutable, we need to create a copy to set the album art.
        // We don't set it initially on all items so that they don't take unnecessary memory.
//        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
//        for (String key :
//                new String[]{
//                        MediaMetadataCompat.METADATA_KEY_MEDIA_ID,
//                        MediaMetadataCompat.METADATA_KEY_ALBUM,
//                        MediaMetadataCompat.METADATA_KEY_ARTIST,
//                        MediaMetadataCompat.METADATA_KEY_GENRE,
//                        MediaMetadataCompat.METADATA_KEY_TITLE
//                }) {
//            builder.putString(key, metadataWithoutBitmap.getString(key));
//        }
//        builder.putLong(
//                MediaMetadataCompat.METADATA_KEY_DURATION,
//                metadataWithoutBitmap.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
//        builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt);
        return metaDataBuilder.build();
    }

    private static void createMediaMetadataCompat(
            String mediaId,
            String title,
            String artist,
            String album,
//            String genre,
            long duration,
            TimeUnit durationUnit,
            String path
//            String musicFilename,
//            int albumArtResId,
//            String albumArtResName
    ) {


        music.put(
                mediaId,
                new MediaBrowserCompat.MediaItem(new MediaDescriptionCompat.Builder()
                        .setMediaId(mediaId)
                        .setMediaUri(ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Long.parseLong(mediaId)))
                        .setTitle(title)
                        .build(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
//                new MediaMetadataCompat.Builder()
//                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
//
//                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
//                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
//                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION,
//                                TimeUnit.MILLISECONDS.convert(duration, durationUnit))
//
//                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, path)
////                        .putString(MediaMetadataCompat.METADATA_KEY_GENRE, genre)
////                        .putString(
////                                MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,
////                                getAlbumArtUri(albumArtResName))
////                        .putString(
////                                MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI,
////                                getAlbumArtUri(albumArtResName))
//                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
//                        .build()
        );
//        albumRes.put(mediaId, albumArtResId);
        musicFileName.put(mediaId, title);
    }

    public static void buildMediaItems(List<Song> songs) {
        for (Song song : songs) {
            MusicLibrary.songs.put(song.getmId(), song);
            Log.d(Constants.TAG, "buildMediaItems: path" + song.getSongPath());
            createMediaMetadataCompat(
                    song.getmId(),
                    song.getSongTitle(),
                    song.getArtistName(),
                    song.getAlbumName(),
                    song.getSongDuration(),
                    TimeUnit.MILLISECONDS,
                    song.getSongPath()
//                    "",
//                    song.getSongTitle(),
//                    song.getArtistId(),
//                    ""
            );
        }
    }
}