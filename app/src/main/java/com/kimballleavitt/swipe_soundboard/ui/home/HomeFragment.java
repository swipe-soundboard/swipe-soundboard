package com.kimballleavitt.swipe_soundboard.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.PatternLockView.PatternViewMode;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.kimballleavitt.swipe_soundboard.R;
import com.kimballleavitt.swipe_soundboard.SoundPlayer;
import com.kimballleavitt.swipe_soundboard.model.SoundMappings;

import java.util.List;

import static com.andrognito.patternlockview.PatternLockView.PatternViewMode.*;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private PatternLockViewListener listener;
    private PatternLockView plv;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        listener = new PatternLockViewListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {

            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                try {
                    int soundID = SoundMappings.getInstance().getSoundID(pattern);
                    plv.setViewMode(CORRECT);
                    SoundPlayer.getSoundPlayer().playSound(getContext(), soundID);
                } catch (IndexOutOfBoundsException e) {
                    plv.setViewMode(WRONG);
                }
            }

            @Override
            public void onCleared() {

            }
        };
        plv = root.findViewById(R.id.pattern_lock_view);
        plv.addPatternLockListener(listener);
        return root;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        plv.removePatternLockListener(listener);
    }
}