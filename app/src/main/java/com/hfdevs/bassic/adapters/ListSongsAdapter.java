package com.hfdevs.bassic.adapters;


import android.content.Context;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.hfdevs.bassic.databinding.ItemMusicListBinding;
import com.hfdevs.bassic.loaders.MusicLibrary;
import com.hfdevs.bassic.models.Song;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class ListSongsAdapter extends RecyclerView.Adapter<ListSongsAdapter.MyViewHolder> {

    ArrayList<MediaBrowserCompat.MediaItem> listSongsArrayList;
    Context context;
    OnListSongsClickListener onListSongsClickListener;

    public ListSongsAdapter(Context context, ArrayList<MediaBrowserCompat.MediaItem> listSongsArrayList) {
        this.context = context;
        this.listSongsArrayList = listSongsArrayList;
    }

    public void setOnListSongsClickListener(OnListSongsClickListener onListSongsClickListener) {
        this.onListSongsClickListener = onListSongsClickListener;
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        ItemMusicListBinding binding = ItemMusicListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NotNull MyViewHolder holder, int position) {
        MediaBrowserCompat.MediaItem song = listSongsArrayList.get(holder.getAbsoluteAdapterPosition());

        MediaMetadataCompat mediaMetadataCompat = MusicLibrary.getMetadata(song.getMediaId());
        String artist = mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_ARTIST);
        holder.binding.tvSongName.setText(String.format("%s - %s", song.getDescription().getTitle(), artist));
        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onListSongsClickListener != null) {
                    onListSongsClickListener.onListSongsClicked(holder.getAdapterPosition(), listSongsArrayList.get(holder.getAbsoluteAdapterPosition()));
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return listSongsArrayList.size();
    }

    public interface OnListSongsClickListener {
        void onListSongsClicked(int position, MediaBrowserCompat.MediaItem song);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemMusicListBinding binding;

        public MyViewHolder(@NotNull ItemMusicListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;


        }
    }
}