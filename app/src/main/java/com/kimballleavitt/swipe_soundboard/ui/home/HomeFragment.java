package com.kimballleavitt.swipe_soundboard.ui.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.kimballleavitt.swipe_soundboard.AddDefaultSound;
import com.kimballleavitt.swipe_soundboard.R;
import com.kimballleavitt.swipe_soundboard.SoundPlayer;
import com.kimballleavitt.swipe_soundboard.VideoActivity;
import com.kimballleavitt.swipe_soundboard.model.SoundMappings;

import java.net.URLConnection;
import java.util.List;

import static com.andrognito.patternlockview.PatternLockView.PatternViewMode.CORRECT;
import static com.andrognito.patternlockview.PatternLockView.PatternViewMode.WRONG;

public class HomeFragment extends Fragment {

    private static final int PLAY_VIDEO = 1;
    private HomeViewModel homeViewModel;
    private PatternLockViewListener listener;
    private PatternLockView plv;
    private VideoView videoView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        videoView = root.findViewById(R.id.video_view);
        listener = new PatternLockViewListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {

            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                try {
                    Uri soundPath = SoundMappings.getInstance().getSoundPath(pattern);
                    plv.setViewMode(CORRECT);
                    String mimeType = URLConnection.guessContentTypeFromName(soundPath.getPath());
                    if (mimeType != null && mimeType.startsWith("video/")) {
                        Intent videoIntent = new Intent(getContext(), VideoActivity.class);
                        videoIntent.setData(soundPath);
                        startActivityForResult(videoIntent, PLAY_VIDEO);
                    } else {
                        SoundPlayer.getSoundPlayer().playSound(getContext(), soundPath);
                    }
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLAY_VIDEO) {
            /* great, we're done :) */
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        plv.removePatternLockListener(listener);
    }
}