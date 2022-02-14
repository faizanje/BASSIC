package com.hfdevs.bassic.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.hfdevs.bassic.databinding.ItemMusicListBinding;
import com.hfdevs.bassic.models.Song;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class ListSongsAdapter extends RecyclerView.Adapter<ListSongsAdapter.MyViewHolder> {

    ArrayList<Song> listSongsArrayList;
    Context context;
    OnListSongsClickListener onListSongsClickListener;

    public ListSongsAdapter(Context context, ArrayList<Song> listSongsArrayList) {
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
        Song listSongs = listSongsArrayList.get(holder.getAdapterPosition());

        holder.binding.tvSongName.setText(listSongs.getSongTitle());

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onListSongsClickListener != null) {
                    onListSongsClickListener.onListSongsClicked(holder.getAdapterPosition(), listSongsArrayList.get(holder.getAdapterPosition()));
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return listSongsArrayList.size();
    }

    public interface OnListSongsClickListener {
        void onListSongsClicked(int position, Song listSongs);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemMusicListBinding binding;

        public MyViewHolder(@NotNull ItemMusicListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;


        }
    }
}