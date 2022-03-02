package com.hfdevs.bassic.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hfdevs.bassic.api.RetrofitApiClient;
import com.hfdevs.bassic.models.Song;
import com.hfdevs.bassic.utils.Constants;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LyricsViewModel extends ViewModel {

    public Song song = new Song();
    public Song customSong = new Song();
    MutableLiveData<String> lyricsLiveData = new MutableLiveData<>();
    MutableLiveData<Boolean> isLoading = new MutableLiveData<>(true);

    public LiveData<String> getLyricsLiveData() {
        return lyricsLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void getLyrics(String songTitle, String songArtist) {
        this.isLoading.setValue(true);
        RetrofitApiClient
                .getLyricsAPIInterface().getLyrics(songArtist, songTitle)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        isLoading.setValue(false);
                        if (response.isSuccessful()) {
                            Log.d(Constants.TAG, "onResponse: is successful: " + response.code());
                            try {
                                String responseStr = response.body().string();
                                Log.d(Constants.TAG, "onResponse:" + responseStr);
                                JSONObject jsonObject = new JSONObject(responseStr);
                                String lyrics = (String) jsonObject.get("lyrics");
                                lyricsLiveData.setValue(lyrics);
                            } catch (Exception e) {
                                Log.d(Constants.TAG, "onResponse: Exception" + e.getMessage());
                            }
                        } else {
                            lyricsLiveData.setValue("Lyrics not found");
                            Log.d(Constants.TAG, "onResponse: is not successful " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d(Constants.TAG, "onFailure:  " + t.getMessage());
                    }
                });
    }
}
