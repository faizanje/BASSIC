package com.hfdevs.bassic.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.hfdevs.bassic.databinding.LayoutSongBottomSheetBinding;

public class SongBottomSheetDialogFragment extends BottomSheetDialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
            ViewGroup container, @Nullable Bundle savedInstanceState) {
        LayoutSongBottomSheetBinding binding = LayoutSongBottomSheetBinding.inflate(getLayoutInflater(),
                container, false);


        return binding.getRoot();
    }
}