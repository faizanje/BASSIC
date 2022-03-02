package com.hfdevs.bassic.ui.slideshow;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    SongsViewModel songsViewModel;
    private FragmentSleepTimerBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSleepTimerBinding.inflate(inflater, container, false);
        init();
        setListeners();

        return binding.getRoot();
    }

    private void setListeners() {
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
            }, hour, minute, true);//Yes 24 hour time
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        });

        binding.btnSave.setOnClickListener(v -> {
            SharedPrefs.saveSleepTime(timeInMillis);
            Toast.makeText(requireContext(), "Sleep timer has been set", Toast.LENGTH_SHORT).show();
            songsViewModel.startSleepTimer();
        });
    }

    private void init() {
        songsViewModel =
                new ViewModelProvider(requireActivity()).get(SongsViewModel.class);
        timeInMillis = SharedPrefs.getSleepTime();
        binding.etSelectTime.setText(getFormattedDateTime(timeInMillis));
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