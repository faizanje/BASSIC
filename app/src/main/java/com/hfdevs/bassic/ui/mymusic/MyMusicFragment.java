package com.hfdevs.bassic.ui.mymusic;

import android.Manifest;
import android.content.Context;
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
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
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
import com.hfdevs.bassic.services.MusicService;
import com.hfdevs.bassic.services.client.MediaBrowserHelper;
import com.hfdevs.bassic.utils.Constants;
import com.hfdevs.bassic.utils.NavControllerUtils;
import com.hfdevs.bassic.viewmodels.SongsViewModel;
import com.tbruyelle.rxpermissions3.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class MyMusicFragment extends Fragment {

    final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final ArrayList<Song> songArrayList = new ArrayList<>();
    RxPermissions rxPermissions; // where this is an Activity or Fragment instance
    FragmentMyMusicBinding binding;
    ListSongsAdapter adapter;
    SongsViewModel songsViewModel;
    private MediaBrowserHelper mMediaBrowserHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMyMusicBinding.inflate(getLayoutInflater());
        rxPermissions = new RxPermissions(this);

        mMediaBrowserHelper = new MediaBrowserConnection(getContext());
        mMediaBrowserHelper.registerCallback(new MediaBrowserListener());

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

    @Override
    public void onStart() {
        super.onStart();
        mMediaBrowserHelper.onStart();
    }

    private void setListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                songsViewModel.refreshSongsList();
            }
        });

        binding.btnMusicLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavControllerUtils.getNavController(getActivity()).navigate(R.id.action_myMusicFragment_to_nowPlayingFragment);
            }
        });
    }

    private void init() {
        songsViewModel = new ViewModelProvider(this).get(SongsViewModel.class);
        adapter = new ListSongsAdapter(requireContext(), songArrayList);
        adapter.setOnListSongsClickListener(new ListSongsAdapter.OnListSongsClickListener() {
            @Override
            public void onListSongsClicked(int position, Song listSongs) {
                mMediaBrowserHelper.getTransportControls().play();
            }
        });
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

        songsViewModel.getNowPlaying().observe(requireActivity(), song -> {
            binding.tvSongName.setText(song.getSongTitle());
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
                }, throwable -> {
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


    /**
     * and implement our app specific desires.
     */
    private class MediaBrowserConnection extends MediaBrowserHelper {
        private MediaBrowserConnection(Context context) {
            super(context, MusicService.class);
        }

        @Override
        protected void onConnected(@NonNull MediaControllerCompat mediaController) {
//            mSeekBarAudio.setMediaController(mediaController);
        }

        @Override
        protected void onChildrenLoaded(@NonNull String parentId,
                                        @NonNull List<MediaBrowserCompat.MediaItem> children) {
            super.onChildrenLoaded(parentId, children);

            final MediaControllerCompat mediaController = getMediaController();

            // Queue up all media items for this simple sample.
            for (final MediaBrowserCompat.MediaItem mediaItem : children) {
                mediaController.addQueueItem(mediaItem.getDescription());
            }

            // Call prepare now so pressing play just works.
            mediaController.getTransportControls().prepare();
        }
    }

    /**
     * Implementation of the {@link MediaControllerCompat.Callback} methods we're interested in.
     * <p>
     * Here would also be where one could override
     * {@code onQueueChanged(List<MediaSessionCompat.QueueItem> queue)} to get informed when items
     * are added or removed from the queue. We don't do this here in order to keep the UI
     * simple.
     */
    private class MediaBrowserListener extends MediaControllerCompat.Callback {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat playbackState) {
//            mIsPlaying = playbackState != null &&
//                    playbackState.getState() == PlaybackStateCompat.STATE_PLAYING;
//            mMediaControlsImage.setPressed(mIsPlaying);
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat mediaMetadata) {
            if (mediaMetadata == null) {
                return;
            }
//            mTitleTextView.setText(
//                    mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
//            mArtistTextView.setText(
//                    mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));
//            mAlbumArt.setImageBitmap(MusicLibrary.getAlbumBitmap(
//                    MainActivity.this,
//                    mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)));
        }

        @Override
        public void onSessionDestroyed() {
            super.onSessionDestroyed();
        }

        @Override
        public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {
            super.onQueueChanged(queue);
        }
    }


}