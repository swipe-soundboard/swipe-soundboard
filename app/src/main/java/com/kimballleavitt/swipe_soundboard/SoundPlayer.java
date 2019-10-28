package com.kimballleavitt.swipe_soundboard;

import android.content.Context;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;
import java.net.URL;

public class SoundPlayer {
    private static SoundPlayer soundPlayer = new SoundPlayer();
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private SoundPlayer(){}

    public static SoundPlayer getSoundPlayer() {
        return soundPlayer;
    }
    public void playSound(Context c, Uri pathToFile) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(c, pathToFile);
            mediaPlayer.setLooping(false);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }
            });
            mediaPlayer.prepare();
            //requestAudioFocus();
            AudioManager mAudioManager = (AudioManager) c.getSystemService(Context.AUDIO_SERVICE);
            AudioManager.OnAudioFocusChangeListener listener = new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int i) {
                    System.out.println("Focus changed");
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }
            };
            mAudioManager.requestAudioFocus(listener, 1, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

            mediaPlayer.start();

        } catch (IOException exception){
            System.out.println("File not found!\n" + exception.getMessage());
            exception.printStackTrace();
        }
    }
}
