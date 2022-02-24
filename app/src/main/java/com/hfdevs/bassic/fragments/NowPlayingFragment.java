package com.hfdevs.bassic.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.support.v4.media.session.MediaControllerCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hfdevs.bassic.R;
import com.hfdevs.bassic.adapters.ListSongsAdapter;
import com.hfdevs.bassic.databinding.FragmentMyMusicBinding;
import com.hfdevs.bassic.databinding.FragmentNowPlayingBinding;
import com.hfdevs.bassic.models.Song;
import com.hfdevs.bassic.utils.NavControllerUtils;
import com.hfdevs.bassic.utils.Utils;
import com.hfdevs.bassic.viewmodels.SongsViewModel;

public class NowPlayingFragment extends Fragment {


    FragmentNowPlayingBinding binding;
    SongsViewModel songsViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentNowPlayingBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        observeChanges();
        setListeners();
    }

    private void init() {
        songsViewModel = new ViewModelProvider(requireActivity()).get(SongsViewModel.class);
        MediaControllerCompat mediaControllerCompat = songsViewModel.getmMediaController();
        Toast.makeText(requireContext(), "MediaController is null = " + (mediaControllerCompat == null), Toast.LENGTH_SHORT).show();

//        binding.seekBar2.setMediaController(mediaControllerCompat);
    }

    private void observeChanges() {

        songsViewModel.getNowPlaying().observe(requireActivity(), song -> {
            binding.tvSongNameExpanded.setText(song.getSongTitle());
            binding.tvSongDuration.setText(Utils.formatDuration(song.getSongDuration()));
        });

        songsViewModel.getIsPlaying().observe(requireActivity(), isPlaying -> {
            binding.btnPlayPause.setImageResource(
                    isPlaying ?
                            R.drawable.pause_filled :
                            R.drawable.icon_material_play_circle_filled_blue
            );
        });
    }

    private void setListeners() {
        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songsViewModel.nextSong();
            }
        });
        binding.btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songsViewModel.PlayPauseResume();
            }
        });

        binding.btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songsViewModel.prevSong();
            }
        });

        binding.btnShuffle.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Shuffled", Toast.LENGTH_SHORT).show();
            songsViewModel.shuffle();
        });

    }

    @Override
    public void onStop() {
        super.onStop();
//        binding.seekBar2.disconnectController();
    }
}