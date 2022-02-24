package com.hfdevs.bassic.models;

import androidx.annotation.NonNull;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Song {

    static final Song EMPTY_SONG = new Song("", -1, -1, -1, null, "", -1, "");

    private /*final*/ String mTitle;
    private /*final*/ int mTrackNumber;
    private /*final*/ int mDuration;
    private /*final*/ String mPath;
    private /*final*/ String mAlbumName;
    private /*final*/ int mArtistId;
    private /*final*/ String mArtistName;
    private /*final*/ int mYear;
    private String mId;

    public Song() {
    }

    public Song(String mTitle, int mDuration, String mArtistName, String mId) {
        this.mTitle = mTitle;
        this.mDuration = mDuration;
        this.mArtistName = mArtistName;
        this.mId = mId;
    }

    public Song(@NonNull final String id, String title, final int trackNumber, final int year, final int duration, final String path, final String albumName, final int artistId, final String artistName) {
        mId = id;
        mTitle = title;
        mTrackNumber = trackNumber;
        mYear = year;
        mDuration = duration;
        mPath = path;
        mAlbumName = albumName;
        mArtistId = artistId;
        mArtistName = artistName;
    }

    public Song(@NonNull final String title, final int trackNumber, final int year, final int duration, final String path, final String albumName, final int artistId, final String artistName) {
        mTitle = title;
        mTrackNumber = trackNumber;
        mYear = year;
        mDuration = duration;
        mPath = path;
        mAlbumName = albumName;
        mArtistId = artistId;
        mArtistName = artistName;
    }

    public Song(@NonNull final String title, final int trackNumber, final int year, final int duration, final String path) {
        mTitle = title;
        mTrackNumber = trackNumber;
        mYear = year;
        mDuration = duration;
        mPath = path;
    }

    @NonNull
    public static String formatDuration(final int duration) {
        return String.format(Locale.getDefault(), "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }

    public static int formatTrack(final int trackNumber) {
        int formatted = trackNumber;
        if (trackNumber >= 1000) {
            formatted = trackNumber % 1000;
        }
        return formatted;
    }

    @NonNull
    public final String getSongTitle() {
        return mTitle;
    }

    public final int getTrackNumber() {
        return mTrackNumber;
    }

    public final int getSongDuration() {
        return mDuration;
    }

    @NonNull
    public final String getSongPath() {
        return mPath;
    }

    @NonNull
    public final String getAlbumName() {
        return mAlbumName;
    }

    public final int getArtistId() {
        return mArtistId;
    }


    public final String getArtistName() {
        return mArtistName;
    }

    public final int getYear() {
        return mYear;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    @Override
    public String toString() {
        return "Song{" +
                "mID='" + mId + '\'' +
                "mTitle='" + mTitle + '\'' +
                ", mTrackNumber=" + mTrackNumber +
                ", mDuration=" + mDuration +
                ", mPath='" + mPath + '\'' +
                ", mAlbumName='" + mAlbumName + '\'' +
                ", mArtistId=" + mArtistId +
                ", mArtistName='" + mArtistName + '\'' +
                ", mYear=" + mYear +
                '}';
    }
}
