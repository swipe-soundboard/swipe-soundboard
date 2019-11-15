package com.kimballleavitt.swipe_soundboard.ui.dashboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.google.android.material.snackbar.Snackbar;
import com.kimballleavitt.swipe_soundboard.AddRemoveMenu;
import com.kimballleavitt.swipe_soundboard.R;
import com.kimballleavitt.swipe_soundboard.exception.MappingExistsException;
import com.kimballleavitt.swipe_soundboard.model.SoundMappings;

import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private PatternLockViewListener listener;
    private PatternLockView plv;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final DashboardFragment iAm = this;
        dashboardViewModel =
                ViewModelProviders.of(iAm).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        listener = new PatternLockViewListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {

            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                Fragment newFragment = AddRemoveMenu.newInstance(iAm, pattern);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.popup_menu_holder, newFragment, "Menu");
                transaction.addToBackStack(null);
                transaction.commit();
            }

            @Override
            public void onCleared() {

            }
        };
        plv = root.findViewById(R.id.pattern_lock_view);
        plv.addPatternLockListener(listener);
        return root;
    }
    public void closeMenu() {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.remove(manager.findFragmentByTag("Menu"));
        transaction.commit();
    }
}