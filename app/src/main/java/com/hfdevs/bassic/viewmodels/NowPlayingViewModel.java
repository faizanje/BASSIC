package com.hfdevs.bassic.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hfdevs.bassic.loaders.SongProvider;
import com.hfdevs.bassic.models.Song;
import com.hfdevs.bassic.utils.Constants;

import java.util.List;

public class NowPlayingViewModel extends AndroidViewModel {

    MutableLiveData<Song> nowPlayingSongMutableLiveData = new MutableLiveData<>();


    public NowPlayingViewModel(@NonNull Application application) {
        super(application);
    }


    public MutableLiveData<Song> getNowPlayingSongMutableLiveData() {
        return nowPlayingSongMutableLiveData;
    }
}
