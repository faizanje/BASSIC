package com.hfdevs.bassic.ui.mymusic;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.hfdevs.bassic.R;
import com.hfdevs.bassic.adapters.ListSongsAdapter;
import com.hfdevs.bassic.databinding.FragmentMyMusicBinding;
import com.hfdevs.bassic.fragments.SongBottomSheetDialogFragment;
import com.hfdevs.bassic.models.Song;
import com.hfdevs.bassic.utils.Constants;
import com.hfdevs.bassic.viewmodels.SongsViewModel;
import com.tbruyelle.rxpermissions3.RxPermissions;

import java.util.ArrayList;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class MyMusicFragment extends Fragment {

    final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final ArrayList<Song> songArrayList = new ArrayList<>();
    RxPermissions rxPermissions; // where this is an Activity or Fragment instance
    FragmentMyMusicBinding binding;
    ListSongsAdapter adapter;
    SongsViewModel songsViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMyMusicBinding.inflate(getLayoutInflater());
        rxPermissions = new RxPermissions(this);

//        SongBottomSheetDialogFragment songBottomSheetDialogFragment = new SongBottomSheetDialogFragment();
//        songBottomSheetDialogFragment.show(getChildFragmentManager(),"BOTTOM_SHEET");
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
        observeChanges();
        getReadPermission();
        setListeners();

    }

    private void setListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                songsViewModel.refreshSongsList();
            }
        });
    }

    private void init() {
        songsViewModel = new ViewModelProvider(this).get(SongsViewModel.class);
        adapter = new ListSongsAdapter(requireContext(), songArrayList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);
        binding.swipeRefreshLayout.setRefreshing(true);
    }


    private void observeChanges() {
        songsViewModel.getSongsLiveData().observe(requireActivity(), songs -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            songArrayList.clear();
            songArrayList.addAll(songs);
            adapter.notifyDataSetChanged();
        });
    }

    private void getReadPermission() {

        compositeDisposable.add(rxPermissions.requestEach(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(permission -> {
                    // Denied permission without ask never again
                    // Denied permission with ask never again
                    // Need to go to the settings
                    if (permission.granted) {
                        Toast.makeText(requireContext(), permission.name + " is granted", Toast.LENGTH_SHORT).show();
                        songsViewModel.refreshSongsList();
                    } else
                        showPermissionRequiredDialog(!permission.shouldShowRequestPermissionRationale);
                },throwable -> {
                    Log.d(Constants.TAG, "getReadPermission: " + throwable.getMessage());
                    Toast.makeText(requireContext(), "Error:" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }));
    }

    private void showPermissionRequiredDialog(boolean isNeverAskAgainSelected) {
        MaterialAlertDialogBuilder materialAlertDialogBuilder =
                new MaterialAlertDialogBuilder(requireContext())
                        .setCancelable(false)
                        .setTitle("Permission required")
                        .setMessage("Read storage permission is required to show local audio files")
                        .setNegativeButton("Close", (dialog, which) -> {
                            requireActivity().finish();
                        });
        if (isNeverAskAgainSelected) {
            materialAlertDialogBuilder.setPositiveButton("Settings", (dialog, which) -> {
                openSettingsIntent();
            });
        } else {
            materialAlertDialogBuilder.setPositiveButton("Grant Permission", (dialog, which) -> {
                getReadPermission();
            });
        }
        materialAlertDialogBuilder.show();
    }

    private void openSettingsIntent() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", requireContext().getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }


    @Override
    public void onDestroyView() {
        compositeDisposable.dispose();
        super.onDestroyView();
    }

}