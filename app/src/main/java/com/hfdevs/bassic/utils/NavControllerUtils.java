package com.hfdevs.bassic.utils;

import android.app.Activity;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.hfdevs.bassic.R;

public class NavControllerUtils {
    public static NavController getNavController(Activity activity) {
        return Navigation.findNavController(activity, R.id.nav_host_fragment_content_main);
    }
}
