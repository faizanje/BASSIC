package com.hfdevs.bassic.fragments;

import android.os.Bundle;
import android.transition.ChangeBounds;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.transition.MaterialSharedAxis;
import com.hfdevs.bassic.models.Song;
import com.hfdevs.bassic.utils.Constants;
import com.hfdevs.bassic.viewmodels.LyricsViewModel;
import com.hfdevs.bassic.viewmodels.SongsViewModel;

public class LyricsFragment extends Fragment {

    SongsViewModel songsViewModel;
    LyricsViewModel lyricsViewModel;

    private com.hfdevs.bassic.databinding.FragmentLyricsBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSharedElementEnterTransition(new ChangeBounds());
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = com.hfdevs.bassic.databinding.FragmentLyricsBinding.inflate(inflater, container, false);
        songsViewModel = new ViewModelProvider(requireActivity()).get(SongsViewModel.class);
        lyricsViewModel = new ViewModelProvider(requireActivity()).get(LyricsViewModel.class);

        setListeners();
        populateViews();
        observeChanges();

        return binding.getRoot();
    }

    private void setListeners() {
        binding.btnSearchLyrics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = binding.etSongTitle.getText().toString().trim();
                String artist = binding.etArtistName.getText().toString().trim();
                lyricsViewModel.getLyrics(title, artist);
                lyricsViewModel.customSong = lyricsViewModel.song.clone();
                lyricsViewModel.customSong.setmTitle(title);
                lyricsViewModel.customSong.setmArtistName(artist);
            }
        });
    }

    private void populateViews() {

    }

    private void observeChanges() {
        songsViewModel.getNowPlaying().observe(getViewLifecycleOwner(), song -> {

            if (lyricsViewModel.song != null) {
                Log.d(Constants.TAG, "lyrics song is not null");
                Log.d(Constants.TAG, String.format("%s.equals(%s) == %s", lyricsViewModel.song.getSongTitle(), song.getSongTitle(), lyricsViewModel.song.getSongTitle().equals(song.getSongTitle())));
                if (!lyricsViewModel.song.getSongTitle().equals(song.getSongTitle())) {
                    //Different song. Populate the artist and song name
                    lyricsViewModel.getLyrics(song.getSongTitle(), song.getArtistName());
                    lyricsViewModel.song = song.clone();

                    binding.etSongTitle.setText(lyricsViewModel.song.getSongTitle());
                    binding.etArtistName.setText(lyricsViewModel.song.getArtistName());

                } else {
                    // Same song or already got the lyrics. Populate the artist and song name that user last searched for
                    Log.d(Constants.TAG, "lyrics song title: " + lyricsViewModel.song.getSongTitle());
                    if(lyricsViewModel.customSong.getSongTitle().isEmpty()){
                        binding.etSongTitle.setText(lyricsViewModel.song.getSongTitle());
                        binding.etArtistName.setText(lyricsViewModel.song.getArtistName());
                    }else{
                        binding.etSongTitle.setText(lyricsViewModel.customSong.getSongTitle());
                        binding.etArtistName.setText(lyricsViewModel.customSong.getArtistName());
                    }

                }
            } else {
                //Different song. Populate the artist and song name
                Log.d(Constants.TAG, "lyrics song is null: ");
                lyricsViewModel.getLyrics(song.getSongTitle(), song.getArtistName());
                lyricsViewModel.song = song.clone();
                Log.d(Constants.TAG, "lyrics song title: " + lyricsViewModel.song.getSongTitle());

                binding.etSongTitle.setText(lyricsViewModel.song.getSongTitle());
                binding.etArtistName.setText(lyricsViewModel.song.getArtistName());

            }


        });

        lyricsViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.tvLyrics.setVisibility(isLoading ? View.GONE : View.VISIBLE);
            binding.btnSearchLyrics.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        });

        lyricsViewModel.getLyricsLiveData().observe(getViewLifecycleOwner(), lyrics -> {
            binding.tvLyrics.setText(lyrics);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}