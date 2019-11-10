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
import androidx.lifecycle.ViewModelProviders;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.google.android.material.snackbar.Snackbar;
import com.kimballleavitt.swipe_soundboard.R;
import com.kimballleavitt.swipe_soundboard.exception.MappingExistsException;
import com.kimballleavitt.swipe_soundboard.model.SoundMappings;

import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    private static final int PICKFILE_RESULT_CODE = 1;
    private DashboardViewModel dashboardViewModel;
    private PatternLockViewListener listener;
    private PatternLockView plv;

    private List<PatternLockView.Dot> currPattern;
    private Uri currFileUri;

    private DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    assert currFileUri != null;
                    assert currPattern != null;
                    try {
                        SoundMappings.getInstance().addMapping(currPattern, currFileUri, true);
                        Snackbar.make(getView(), "✅ Sound mapping added", Snackbar.LENGTH_SHORT).show();
                    } catch (MappingExistsException e) {
                        e.printStackTrace();
                        Snackbar.make(getView(), "Error occurred adding mapping...awkward", Snackbar.LENGTH_SHORT).show();
                    }
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    currFileUri = null;
                    currPattern = null;
                    Snackbar.make(getView(), "Cancelled", Snackbar.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
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
                currPattern = pattern;
                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.setType("audio/*");
                chooseFile = Intent.createChooser(chooseFile, "Pick a sound to go with this pattern");
                startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);
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
        if (requestCode == PICKFILE_RESULT_CODE) {
            if (resultCode == -1) {
                currFileUri = data.getData();
                assert currFileUri != null;
                assert currPattern != null;
                try {
                    SoundMappings.getInstance().addMapping(currPattern, currFileUri, false);
                    Snackbar.make(getView(), "✅ Sound mapping added", Snackbar.LENGTH_SHORT).show();
                } catch (MappingExistsException e) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage(String.format(Locale.US, "Pattern is already mapped to '%s'. Change to '%s'?",
                            e.getFileUri().getPath(), currFileUri.getPath())).setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                }
            }
        }
    }
}