package com.hfdevs.bassic.utils;

import android.support.v4.media.session.MediaSessionCompat;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.hfdevs.bassic.R;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Utils {
    public static int findItemPositionInList(ArrayList<MediaSessionCompat.QueueItem> list, String mediaId) {
        for (int i = 0; i < list.size(); i++) {
            MediaSessionCompat.QueueItem queueItem = list.get(i);
            if (queueItem.getDescription().getMediaId().equals(mediaId))
                return i;
        }
        return -1;
    }

    public   static String formatDuration(long duration) {
        long minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS);
        long seconds = TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS)
                - minutes * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES);

        return String.format("%02d:%02d", minutes, seconds);
    }

}
