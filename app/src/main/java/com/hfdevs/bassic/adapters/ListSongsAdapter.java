package com.hfdevs.bassic.adapters;


import android.content.Context;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.recyclerview.widget.RecyclerView;

import com.hfdevs.bassic.databinding.ItemMusicListBinding;
import com.hfdevs.bassic.loaders.MusicLibrary;
import com.hfdevs.bassic.models.Song;
import com.hfdevs.bassic.utils.Constants;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class ListSongsAdapter extends RecyclerView.Adapter<ListSongsAdapter.MyViewHolder> implements Filterable {

    ArrayList<MediaBrowserCompat.MediaItem> listSongsArrayList;
    ArrayList<MediaBrowserCompat.MediaItem> listSongsArrayListFiltered;
    Context context;
    OnListSongsClickListener onListSongsClickListener;

    public ListSongsAdapter(Context context, ArrayList<MediaBrowserCompat.MediaItem> listSongsArrayList) {
        this.context = context;
        this.listSongsArrayList = listSongsArrayList;
        this.listSongsArrayListFiltered = listSongsArrayList;
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
        MediaBrowserCompat.MediaItem song = listSongsArrayListFiltered.get(holder.getAbsoluteAdapterPosition());

        MediaMetadataCompat mediaMetadataCompat = MusicLibrary.getMetadata(song.getMediaId());
        String artist = mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_ARTIST);
        holder.binding.tvSongName.setText(String.format("%s - %s", song.getDescription().getTitle(), artist));
        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onListSongsClickListener != null) {
                    onListSongsClickListener.onListSongsClicked(holder.getAdapterPosition(), listSongsArrayListFiltered.get(holder.getAbsoluteAdapterPosition()));
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return listSongsArrayListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    Log.d(Constants.TAG, "performFiltering: Empty");
                    listSongsArrayListFiltered = listSongsArrayList;
                } else {
                    ArrayList<MediaBrowserCompat.MediaItem> filteredList = new ArrayList<>();
                    for (MediaBrowserCompat.MediaItem row : listSongsArrayList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getDescription().getTitle().toString().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    listSongsArrayListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = listSongsArrayListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                listSongsArrayListFiltered = (ArrayList<MediaBrowserCompat.MediaItem>) filterResults.values;
                notifyDataSetChanged();
            }
        };
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