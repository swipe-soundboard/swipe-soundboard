package com.kimballleavitt.swipe_soundboard;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.google.android.material.snackbar.Snackbar;
import com.kimballleavitt.swipe_soundboard.exception.MappingExistsException;
import com.kimballleavitt.swipe_soundboard.model.SoundMappings;
import com.kimballleavitt.swipe_soundboard.ui.dashboard.DashboardFragment;

import java.util.List;
import java.util.Locale;


public class AddRemoveMenu extends Fragment {
    private static final int PICK_CUSTOM_SOUND = 1;
    private static final int PICK_DEFAULT_SOUND = 2;

    private DashboardFragment parent;
    private List<PatternLockView.Dot> currPattern;
    private Uri currFileUri;
    public AddRemoveMenu() {}

    public static AddRemoveMenu newInstance(DashboardFragment parent, List<PatternLockView.Dot> pattern) {
        AddRemoveMenu fragment = new AddRemoveMenu();
        fragment.parent = parent;
        fragment.currPattern = pattern;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_remove_menu, container, false);
        if (SoundMappings.getInstance().contains(currPattern)) {
            LinearLayout ll = v.findViewById(R.id.AddOptions);
            ll.setVisibility(View.GONE);
            ll = v.findViewById(R.id.RemoveOptions);
            ll.setVisibility(View.VISIBLE);
        }
        TextView tv = v.findViewById(R.id.cancel_add);
        tv.setOnClickListener(new CancelOnClickListener(parent));
        tv = v.findViewById(R.id.cancel_remove);
        tv.setOnClickListener(new CancelOnClickListener(parent));
        tv = v.findViewById(R.id.add_default_sound);
        tv.setOnClickListener(new DefaultSoundOnClickListener(parent));
        tv = v.findViewById(R.id.add_custom_sound);
        tv.setOnClickListener(new CustomSoundOnClickListener(parent));
        tv = v.findViewById(R.id.remove_sound);
        tv.setOnClickListener(new RemoveOnClickListener(parent));
        tv = v.findViewById(R.id.replace_sound);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout ll = getView().findViewById(R.id.AddOptions);
                ll.setVisibility(View.VISIBLE);
                ll = getView().findViewById(R.id.RemoveOptions);
                ll.setVisibility(View.GONE);
            }
        });
        return v;
    }

    public class DefaultSoundOnClickListener implements View.OnClickListener {
        private DashboardFragment df;
        public DefaultSoundOnClickListener(DashboardFragment df) {this.df = df;}
        @Override
        public void onClick(View v) {
            Intent chooseDefaultSound = new Intent(getContext(), AddDefaultSound.class);
            startActivityForResult(chooseDefaultSound, PICK_DEFAULT_SOUND);
        }
    }

    public class CancelOnClickListener implements View.OnClickListener {
        private DashboardFragment df;
        public CancelOnClickListener(DashboardFragment df) {this.df = df;}
        public void onClick(View v) {
            df.closeMenu();
        }
    }
    public class CustomSoundOnClickListener implements View.OnClickListener {
        private DashboardFragment df;
        public CustomSoundOnClickListener(DashboardFragment df) {this.df = df;}
        public void onClick(View v) {
            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.setType("*/*");
            chooseFile = Intent.createChooser(chooseFile, "Pick a sound to go with this currPattern");
            startActivityForResult(chooseFile, PICK_CUSTOM_SOUND);
        }
    }
    public class RemoveOnClickListener implements View.OnClickListener {
        private DashboardFragment df;
        public RemoveOnClickListener(DashboardFragment df) {this.df = df;}
        public void onClick(View v) {
            SoundMappings.getInstance().removeMapping(currPattern);
            df.closeMenu();
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
    private DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            String filename = "";
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    assert currFileUri != null;
                    filename = getResources().getResourceEntryName(which);
                    assert currPattern != null;
                    try {
                        SoundMappings.getInstance().addMapping(currPattern, currFileUri, filename, true);
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String filename = "";
        int resId = 0;
        if (requestCode == PICK_CUSTOM_SOUND || requestCode == PICK_DEFAULT_SOUND) {
            if (resultCode == -1) {
                if (requestCode == PICK_DEFAULT_SOUND) {
                    resId = data.getIntExtra("id", -1);
                    currFileUri = Uri.parse("android.resource://" + getContext().getPackageName() + '/' + resId);
                } else {
                    currFileUri = data.getData();
                }
                assert currFileUri != null;
                filename = getResources().getResourceEntryName(resId);
                assert currPattern != null;
                try {
                    SoundMappings.getInstance().addMapping(currPattern, currFileUri, filename, true);
                    Snackbar.make(getView(), "✅ Sound mapping added", Snackbar.LENGTH_SHORT).show();
                } catch (MappingExistsException e) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage(String.format(Locale.US, "Pattern is already mapped to '%s'. Change to '%s'?",
                            e.getFileUri().getPath(), currFileUri.getPath())).setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                }
            }
        }
        parent.closeMenu();
    }
}
