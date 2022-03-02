package com.hfdevs.bassic.fragments;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.transition.ArcMotion;

import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.material.transition.MaterialContainerTransform;
import com.google.android.material.transition.MaterialSharedAxis;
import com.hfdevs.bassic.R;
import com.hfdevs.bassic.databinding.FragmentNowPlayingBinding;
import com.hfdevs.bassic.utils.NavControllerUtils;
import com.hfdevs.bassic.utils.Utils;
import com.hfdevs.bassic.viewmodels.SongsViewModel;

public class NowPlayingFragment extends Fragment {


    FragmentNowPlayingBinding binding;
    SongsViewModel songsViewModel;
    //    Object transition = new MaterialFadeThrough();
//    Object transition = new MaterialFade();
    Object transition = new MaterialSharedAxis(MaterialSharedAxis.Y, true);
    private ValueAnimator mProgressAnimator;
    private boolean mIsTracking = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setEnterTransition(transition);
//        setReturnTransition(transition);
//        returnTransition = MaterialFadeThrough()
        MaterialContainerTransform object = new MaterialContainerTransform();

        object.setDrawingViewId(R.id.nav_host_fragment_content_main);
//        object.setDrawDebugEnabled(true);
//        object.setPathMotion(new ArcMotion());
        object.setPathMotion(new ArcMotion());
        object.setScrimColor(Color.TRANSPARENT);
        object.setFadeMode(MaterialContainerTransform.FADE_MODE_CROSS);
        setSharedElementEnterTransition(object);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        setSharedElementEnterTransition(new ChangeBounds());
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
//        Toast.makeText(requireContext(), "MediaController is null = " + (mediaControllerCompat == null), Toast.LENGTH_SHORT).show();

//        binding.seekBar2.setMediaController(mediaControllerCompat);
    }

    private void observeChanges() {

        songsViewModel.getNowPlaying().observe(getViewLifecycleOwner(), song -> {
            binding.tvSongNameExpanded.setText(song.getSongTitle());
            binding.tvSongDuration.setText(Utils.formatDuration(song.getSongDuration()));
            binding.seekBar2.setMax(song.getSongDuration());
            binding.seekBar2.setProgress(0);

        });

        songsViewModel.getShuffleMode().observe(getViewLifecycleOwner(), shuffleMode -> {

            Toast.makeText(requireContext(),
                    shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_ALL ?
                            "Shuffle mode on" :
                            "Shuffle mode off",
                    Toast.LENGTH_SHORT).show();

            binding.btnShuffle.setImageResource(
                    shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_ALL ?
                            R.drawable.icon_material_shuffle :
                            R.drawable.icon_material_shuffle_gray
            );

        });

        songsViewModel.getRepeatMode().observe(getViewLifecycleOwner(), repeatMode -> {


            Toast.makeText(requireContext(),
                    repeatMode == PlaybackStateCompat.REPEAT_MODE_ONE ?
                            "Repeat mode on" :
                            "Repeat mode off",
                    Toast.LENGTH_SHORT).show();

            binding.btnRepeat.setImageResource(
                    repeatMode == PlaybackStateCompat.REPEAT_MODE_ONE ?
                            R.drawable.icon_material_repeat_one :
                            R.drawable.icon_material_repeat_one_gray
            );


        });

        songsViewModel.getIsPlaying().observe(getViewLifecycleOwner(), isPlaying -> {

            binding.btnPlayPause.setImageResource(
                    isPlaying ?
                            R.drawable.pause_filled :
                            R.drawable.icon_material_play_circle_filled_blue
            );
        });

        songsViewModel.getCurrentPlayingDuration().observe(getViewLifecycleOwner(), duration -> {
            binding.seekBar2.setProgress(duration.intValue());
            String timeElapsed = Utils.formatDuration(duration);
            binding.tvTimeElapsed.setText(timeElapsed);
        });

    }

    private void setListeners() {
        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songsViewModel.nextSong();
            }
        });

        binding.btnLyrics.setOnClickListener(v -> {
//            NavControllerUtils.getNavController(getActivity())
//                    .navigate(R.id.action_nowPlayingFragment_to_lyricsFragment);
            FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                    .addSharedElement(binding.tvSongNameExpanded, "shared_transition2")
                    .build();
            NavControllerUtils.getNavController(getActivity())
                    .navigate(R.id.action_nowPlayingFragment_to_lyricsFragment, null, null, extras);
        });
        binding.seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsTracking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                songsViewModel.seekTo(seekBar.getProgress());
                mIsTracking = false;
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
//            Toast.makeText(requireContext(), "Shuffled", Toast.LENGTH_SHORT).show();
            songsViewModel.shuffle();
        });
        binding.btnRepeat.setOnClickListener(v -> {
//            Toast.makeText(requireContext(), "Repeat mode", Toast.LENGTH_SHORT).show();
            songsViewModel.toggleRepeatMode();
        });

    }

    @Override
    public void onStop() {
        super.onStop();
//        binding.seekBar2.disconnectController();
    }
}