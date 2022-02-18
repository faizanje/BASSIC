package com.hfdevs.bassic.loaders;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hfdevs.bassic.models.Song;
import com.hfdevs.bassic.utils.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SongProvider {

    private static final int TITLE = 0;
    private static final int TRACK = 1;
    private static final int YEAR = 2;
    private static final int DURATION = 3;
    private static final int PATH = 4;
    private static final int ALBUM = 5;
    private static final int ARTIST_ID = 6;
    private static final int ARTIST = 7;
    private static final int _ID = 8;

    private static final String[] BASE_PROJECTION = new String[]{
            AudioColumns.TITLE,// 0
            AudioColumns.TRACK,// 1
            AudioColumns.YEAR,// 2
            AudioColumns.DURATION,// 3
            AudioColumns.DATA,// 4
            AudioColumns.ALBUM,// 5
            AudioColumns.ARTIST_ID,// 6
            AudioColumns.ARTIST,// 7
            AudioColumns._ID, //8
    };
//    private static final String[] BASE_PROJECTION = new String[]{
//        MediaStore.Audio.Media._ID,
//        MediaStore.Audio.Media.ARTIST,
//        MediaStore.Audio.Media.YEAR,
//        MediaStore.Audio.Media.TITLE,
//        MediaStore.Audio.Media.DATA
//    };

    private static Uri getUri() {
        return MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    }

    private static final List<Song> mAllDeviceSongs = new ArrayList<>();

    @NonNull
    public static List<Song> getAllDeviceSongs() {
        return mAllDeviceSongs;
    }

//    @NonNull
//    public static List<Song> getAllArtistSongs(@NonNull final List<Album> albums) {
//        final List<Song> songsList = new ArrayList<>();
//        try {
//            for (Album album : albums) {
//                songsList.addAll(album.getSongs());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return songsList;
//    }

    @NonNull
    public static List<Song> getSongs(@Nullable final Cursor cursor) {
        final List<Song> songs = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                final Song song = getSongFromCursorImpl(cursor);
                if (song.getSongDuration() >= 5000) {
                    songs.add(song);
                    mAllDeviceSongs.add(song);
                }
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        if (songs.size() > 1) {
            sortSongsByTrack(songs);
        }
        return songs;
    }

    private static void sortSongsByTrack(@NonNull final List<Song> songs) {
        Collections.sort(songs, (obj1, obj2) -> Long.compare(obj1.getTrackNumber(), obj2.getTrackNumber()));
    }

    @NonNull
    private static Song getSongFromCursorImpl(@NonNull final Cursor cursor) {
//        Log.d(Constants.TAG, "getSongFromCursorImpl: " + cursor.getColumnCount());

        for (int i = 0; i < cursor.getColumnCount(); i++) {
//            Log.d(Constants.TAG, "getSongFromCursorImpl: " + cursor.getString(i));
        }
        final String title = cursor.getString(TITLE);
        final int trackNumber = cursor.getInt(TRACK);
        final int year = cursor.getInt(YEAR);
        final int duration = cursor.getInt(DURATION);
        final String uri = cursor.getString(PATH);
        final String albumName = cursor.getString(ALBUM);
        final int artistId = cursor.getInt(ARTIST_ID);
        final String artistName = cursor.getString(ARTIST);
        final String id = cursor.getString(_ID);

        return new Song(id, title, trackNumber, year, duration, uri, albumName, artistId, artistName);
    }

    @Nullable
    public static Cursor makeSongCursor(@NonNull final Context context, @NonNull final String sortOrder) {
        try {
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            return context.getContentResolver().query(uri,
                    BASE_PROJECTION, null, null, sortOrder);
        } catch (SecurityException e) {
            Log.d(Constants.TAG, "makeSongCursor: " + e.getLocalizedMessage());
            return null;
        }
    }

    public static String getSongLoaderSortOrder() {
        return MediaStore.Audio.Artists.DEFAULT_SORT_ORDER + ", " + MediaStore.Audio.Albums.DEFAULT_SORT_ORDER + ", " + MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
    }
}
