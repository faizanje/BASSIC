package com.hfdevs.bassic.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hfdevs.bassic.loaders.SongProvider;
import com.hfdevs.bassic.models.Song;
import com.hfdevs.bassic.utils.Constants;

import java.util.List;

public class SongsViewModel extends AndroidViewModel {

    MutableLiveData<List<Song>> songsMutableLiveData = new MutableLiveData<>();
    public SongsViewModel(@NonNull Application application) {
        super(application);
    }

    public void refreshSongsList() {
        final List<Song> songs = SongProvider.getSongs(SongProvider.makeSongCursor(
                getApplication(), SongProvider.getSongLoaderSortOrder())
        );
        for (Song song : songs) {
            Log.d(Constants.TAG, "getFiles: " + song);
        }
        songsMutableLiveData.setValue(songs);
    }

    public LiveData<List<Song>> getSongsLiveData() {
        return songsMutableLiveData;
    }
}
