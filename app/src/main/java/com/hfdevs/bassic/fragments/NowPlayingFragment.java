package com.hfdevs.bassic.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hfdevs.bassic.R;
import com.hfdevs.bassic.databinding.FragmentMyMusicBinding;
import com.hfdevs.bassic.databinding.FragmentNowPlayingBinding;

public class NowPlayingFragment extends Fragment {


    FragmentNowPlayingBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentNowPlayingBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
}