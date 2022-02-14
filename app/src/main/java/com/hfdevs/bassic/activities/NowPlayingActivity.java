package com.hfdevs.bassic.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.hfdevs.bassic.databinding.ActivityMainBinding;
import com.hfdevs.bassic.databinding.ActivityNowPlayingBinding;

public class NowPlayingActivity extends AppCompatActivity {
    private ActivityNowPlayingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNowPlayingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}