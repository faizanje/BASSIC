package com.hfdevs.bassic.fragments;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.hfdevs.bassic.databinding.FragmentSleepTimerBinding;
import com.hfdevs.bassic.utils.Constants;
import com.hfdevs.bassic.utils.SharedPrefs;
import com.hfdevs.bassic.viewmodels.SongsViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SleepTimerFragment extends Fragment {

    long timeInMillis;
    boolean isSleepTimerOn;
    SongsViewModel songsViewModel;
    private FragmentSleepTimerBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSleepTimerBinding.inflate(inflater, container, false);
        init();
        setListeners();
        populateViews();

        return binding.getRoot();
    }

    private void setListeners() {

        binding.switchSleepTimer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPrefs.saveSleepTimerToggle(isChecked);
                showSleepTimerLayout(isChecked);
            }
        });
        binding.etSelectTime.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(requireContext(), (timePicker, selectedHour, selectedMinute) -> {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                calendar.set(Calendar.MINUTE, selectedMinute);
                timeInMillis = calendar.getTimeInMillis();
                binding.etSelectTime.setText(getFormattedDateTime(timeInMillis));
            }, hour, minute, false);//Yes 24 hour time
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        });

        binding.btnSave.setOnClickListener(v -> {
            SharedPrefs.saveSleepTime(timeInMillis);
            Toast.makeText(requireContext(), "Sleep timer has been set", Toast.LENGTH_SHORT).show();
            songsViewModel.startSleepTimer();
        });
    }

    private void showSleepTimerLayout(boolean show) {
        isSleepTimerOn = show;
        binding.layoutSleepTimer.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void init() {
        songsViewModel =
                new ViewModelProvider(requireActivity()).get(SongsViewModel.class);
        isSleepTimerOn = SharedPrefs.isSleepTimerOn();
        timeInMillis = SharedPrefs.getSleepTime();
    }

    private void populateViews() {
        binding.switchSleepTimer.setChecked(isSleepTimerOn);
        binding.etSelectTime.setText(getFormattedDateTime(timeInMillis));
        showSleepTimerLayout(isSleepTimerOn);
    }

    @NonNull
    private String getFormattedDateTime(long timeInMillis) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_TIME_FORMAT, Locale.getDefault());
        return simpleDateFormat.format(timeInMillis);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}